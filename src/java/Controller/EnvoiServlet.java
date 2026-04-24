package Controller;

import Dao.EnvoiDao;
import Dao.ClientDao;
import Model.Envoi;
import Model.Client;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/EnvoiServlet")
public class EnvoiServlet extends HttpServlet {

    private EnvoiDao envoiDao;
    private ClientDao clientDao;

    public void init() {
        envoiDao = new EnvoiDao();
        clientDao = new ClientDao();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        try {
            if (action == null) action = "liste";

            switch (action) {
                case "ajouter": ajouterEnvoi(request, response); break;
                case "modifier": modifierEnvoi(request, response); break;
                case "supprimer": supprimerEnvoi(request, response); break;
                case "rechercherParDate": rechercherParDate(request, response); break;
                case "getFraisRates": getFraisRates(request, response); break;
                case "renvoyerEmail": renvoyerEmail(request, response); break;
                default: listerEnvois(request, response); break;
            }
        } catch (SQLException | ParseException e) {
            throw new ServletException(e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    // ✅ LISTER LES ENVOIS
    private void listerEnvois(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        
        List<Envoi> liste = envoiDao.listeEnvoi();
        List<Client> clients = clientDao.listeClient();
        
        request.setAttribute("listeEnvois", liste);
        request.setAttribute("listeClients", clients);
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("Envoi.jsp");
        dispatcher.forward(request, response);
    }

    // ✅ RECHERCHER PAR DATE
    private void rechercherParDate(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException, ParseException {
        
        String dateStr = request.getParameter("dateRecherche");
        List<Client> clients = clientDao.listeClient();
        request.setAttribute("listeClients", clients);
        
        if (dateStr != null && !dateStr.trim().isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date dateRecherche = sdf.parse(dateStr.trim());
            List<Envoi> liste = envoiDao.rechercherParDate(dateRecherche);
            request.setAttribute("listeEnvois", liste);
            request.setAttribute("dateRecherche", dateStr);
        } else {
            List<Envoi> liste = envoiDao.listeEnvoi();
            request.setAttribute("listeEnvois", liste);
        }
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("Envoi.jsp");
        dispatcher.forward(request, response);
    }

    // ✅ AJOUTER UN ENVOI
    private void ajouterEnvoi(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        
        String numenvoyeur = request.getParameter("numenvoyeur");
        String numrecepteur = request.getParameter("numrecepteur");
        String montantStr = request.getParameter("montant");
        String payerFraisRetraitStr = request.getParameter("payer_frais_retrait");
        String raison = request.getParameter("raison");

        // Validation des champs obligatoires
        if (numenvoyeur == null || numenvoyeur.trim().isEmpty() ||
            numrecepteur == null || numrecepteur.trim().isEmpty() ||
            montantStr == null || montantStr.trim().isEmpty()) {
            
            request.setAttribute("message", "Tous les champs obligatoires doivent être remplis");
            request.setAttribute("typeMessage", "error");
            listerEnvois(request, response);
            return;
        }

        try {
            int montant = Integer.parseInt(montantStr.trim());
            
            if (montant <= 0) {
                request.setAttribute("message", "Le montant doit être supérieur à 0");
                request.setAttribute("typeMessage", "error");
                listerEnvois(request, response);
                return;
            }
            
            // Vérifier que l'envoyeur et le récepteur sont différents
            if (numenvoyeur.trim().equals(numrecepteur.trim())) {
                request.setAttribute("message", "L'envoyeur et le récepteur ne peuvent pas être la même personne");
                request.setAttribute("typeMessage", "error");
                listerEnvois(request, response);
                return;
            }
            
            // Vérifier que l'envoyeur existe
            Client envoyeur = clientDao.rechercherClientParNumero(numenvoyeur.trim());
            if (envoyeur == null) {
                request.setAttribute("message", "L'envoyeur n'existe pas dans la base de données");
                request.setAttribute("typeMessage", "error");
                listerEnvois(request, response);
                return;
            }
            
            // Vérifier que le récepteur existe
            Client recepteur = clientDao.rechercherClientParNumero(numrecepteur.trim());
            if (recepteur == null) {
                request.setAttribute("message", "Le récepteur n'existe pas dans la base de données");
                request.setAttribute("typeMessage", "error");
                listerEnvois(request, response);
                return;
            }
            
            // Calculer les frais en utilisant les méthodes du DAO
            int fraisEnvoi = envoiDao.getFraisEnvoiForMontant(montant);
            int fraisRetrait = envoiDao.getFraisRetraitForMontant(montant);
            
            boolean payerFraisRetrait = "true".equals(payerFraisRetraitStr);
            
            // Calculer le total à débiter de l'envoyeur
            int totalDebit = montant + fraisEnvoi;
            if (payerFraisRetrait) {
                totalDebit += fraisRetrait;
            }
            
            // Vérifier que l'envoyeur a assez de solde
            if (envoyeur.getSolde() < totalDebit) {
                request.setAttribute("message", "Solde insuffisant pour effectuer cet envoi. Solde disponible: " + 
                                   String.format("%,d", envoyeur.getSolde()) + " Ar, Total nécessaire: " + 
                                   String.format("%,d", totalDebit) + " Ar");
                request.setAttribute("typeMessage", "error");
                listerEnvois(request, response);
                return;
            }
            
            // Créer l'envoi
            Date dateActuelle = new Date();
            Envoi envoi = new Envoi(numenvoyeur.trim(), numrecepteur.trim(), montant, dateActuelle, payerFraisRetrait, raison);
            
            // Ajouter l'envoi
            envoiDao.ajouterEnvoi(envoi);
            
            // Mettre à jour les soldes
            envoiDao.mettreAJourSoldes(envoi, fraisEnvoi, fraisRetrait);
            
            // Envoyer les notifications par email
            envoiDao.notifierParMail(envoi, fraisEnvoi, fraisRetrait);
            
            request.setAttribute("message", "Envoi effectué avec succès ! Un email a été envoyé aux deux parties.");
            request.setAttribute("typeMessage", "success");
            
        } catch (NumberFormatException e) {
            request.setAttribute("message", "Le montant doit être un nombre valide");
            request.setAttribute("typeMessage", "error");
        } catch (SQLException e) {
            request.setAttribute("message", "Erreur lors de l'envoi: " + e.getMessage());
            request.setAttribute("typeMessage", "error");
        }
        
        listerEnvois(request, response);
    }

    // ✅ MODIFIER UN ENVOI
    private void modifierEnvoi(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        
        String idenvoyer = request.getParameter("idenvoyer");
        String numenvoyeur = request.getParameter("numenvoyeur");
        String numrecepteur = request.getParameter("numrecepteur");
        String montantStr = request.getParameter("montant");
        String payerFraisRetraitStr = request.getParameter("payer_frais_retrait");
        String raison = request.getParameter("raison");

        // Validation des champs obligatoires
        if (idenvoyer == null || idenvoyer.trim().isEmpty() ||
            numenvoyeur == null || numenvoyeur.trim().isEmpty() ||
            numrecepteur == null || numrecepteur.trim().isEmpty() ||
            montantStr == null || montantStr.trim().isEmpty()) {
            
            request.setAttribute("message", "Tous les champs obligatoires doivent être remplis");
            request.setAttribute("typeMessage", "error");
            listerEnvois(request, response);
            return;
        }

        try {
            int montant = Integer.parseInt(montantStr.trim());
            
            if (montant <= 0) {
                request.setAttribute("message", "Le montant doit être supérieur à 0");
                request.setAttribute("typeMessage", "error");
                listerEnvois(request, response);
                return;
            }
            
            boolean payerFraisRetrait = "true".equals(payerFraisRetraitStr);
            
            // Récupérer l'envoi existant pour conserver la date
            List<Envoi> envoisExistants = envoiDao.listeEnvoi();
            Envoi envoiExistant = null;
            for (Envoi e : envoisExistants) {
                if (e.getIdEnvoyer().equals(idenvoyer.trim())) {
                    envoiExistant = e;
                    break;
                }
            }
            
            if (envoiExistant == null) {
                request.setAttribute("message", "Envoi non trouvé");
                request.setAttribute("typeMessage", "error");
                listerEnvois(request, response);
                return;
            }
            
            Envoi envoi = new Envoi(idenvoyer.trim(), numenvoyeur.trim(), numrecepteur.trim(), 
                                    montant, envoiExistant.getDate(), payerFraisRetrait, raison);
            
            boolean updated = envoiDao.modifierEnvoi(envoi);
            
            if (updated) {
                request.setAttribute("message", "Envoi modifié avec succès !");
                request.setAttribute("typeMessage", "success");
            } else {
                request.setAttribute("message", "Erreur lors de la modification de l'envoi");
                request.setAttribute("typeMessage", "error");
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("message", "Le montant doit être un nombre valide");
            request.setAttribute("typeMessage", "error");
        }
        
        listerEnvois(request, response);
    }

    // ✅ SUPPRIMER UN ENVOI
    private void supprimerEnvoi(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        
        String idenvoyer = request.getParameter("idenvoyer");

        if (idenvoyer == null || idenvoyer.trim().isEmpty()) {
            request.setAttribute("message", "ID de l'envoi manquant");
            request.setAttribute("typeMessage", "error");
            listerEnvois(request, response);
            return;
        }

        boolean deleted = envoiDao.supprimerFraisEnvoi(idenvoyer.trim());

        if (deleted) {
            request.setAttribute("message", "Envoi supprimé avec succès !");
            request.setAttribute("typeMessage", "success");
        } else {
            request.setAttribute("message", "Erreur lors de la suppression de l'envoi");
            request.setAttribute("typeMessage", "error");
        }
        
        listerEnvois(request, response);
    }
    
    // ✅ RÉCUPÉRER LES BARÈMES DE FRAIS DEPUIS LA BDD (AJAX)
    private void getFraisRates(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        
        Map<String, Object> result = new HashMap<>();
        
        // Récupérer les barèmes de frais d'envoi depuis la BDD
        List<Map<String, Object>> fraisEnvoi = getFraisEnvoiRatesFromDB();
        List<Map<String, Object>> fraisRetrait = getFraisRetraitRatesFromDB();
        
        result.put("fraisEnvoi", fraisEnvoi);
        result.put("fraisRetrait", fraisRetrait);
        
        Gson gson = new Gson();
        response.setContentType("application/json");
        response.getWriter().print(gson.toJson(result));
    }
    
    // Récupérer les barèmes de frais d'envoi depuis la table FRAISENVOI
    private List<Map<String, Object>> getFraisEnvoiRatesFromDB() throws SQLException {
        List<Map<String, Object>> rates = new ArrayList<>();
        String sql = "SELECT idenv, montant1, montant2, fraisenv FROM fraisenvoi ORDER BY montant1 ASC";
        
        try (java.sql.Connection connection = envoiDao.getConnection();
             java.sql.PreparedStatement stmt = connection.prepareStatement(sql);
             java.sql.ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Map<String, Object> rate = new HashMap<>();
                rate.put("idenv", rs.getString("idenv"));
                rate.put("montant1", rs.getInt("montant1"));
                rate.put("montant2", rs.getInt("montant2"));
                rate.put("frais", rs.getInt("fraisenv"));
                rates.add(rate);
            }
        }
        return rates;
    }
    
    // Récupérer les barèmes de frais de retrait depuis la table FRAISRECEP
    private List<Map<String, Object>> getFraisRetraitRatesFromDB() throws SQLException {
        List<Map<String, Object>> rates = new ArrayList<>();
        String sql = "SELECT idrec, montant1, montant2, fraisrec FROM fraisrecep ORDER BY montant1 ASC";
        
        try (java.sql.Connection connection = envoiDao.getConnection();
             java.sql.PreparedStatement stmt = connection.prepareStatement(sql);
             java.sql.ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Map<String, Object> rate = new HashMap<>();
                rate.put("idrec", rs.getString("idrec"));
                rate.put("montant1", rs.getInt("montant1"));
                rate.put("montant2", rs.getInt("montant2"));
                rate.put("frais", rs.getInt("fraisrec"));
                rates.add(rate);
            }
        }
        return rates;
    }
    
    // ✅ RENVOYER L'EMAIL POUR UN ENVOI EXISTANT (AJAX)
    private void renvoyerEmail(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        
        String idenvoyer = request.getParameter("idenvoyer");
        JsonObject jsonResponse = new JsonObject();
        
        if (idenvoyer == null || idenvoyer.trim().isEmpty()) {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "ID d'envoi manquant");
            response.setContentType("application/json");
            response.getWriter().print(jsonResponse.toString());
            return;
        }
        
        try {
            // Récupérer l'envoi
            List<Envoi> envois = envoiDao.listeEnvoi();
            Envoi envoi = null;
            for (Envoi e : envois) {
                if (e.getIdEnvoyer().equals(idenvoyer.trim())) {
                    envoi = e;
                    break;
                }
            }
            
            if (envoi == null) {
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Envoi non trouvé");
                response.setContentType("application/json");
                response.getWriter().print(jsonResponse.toString());
                return;
            }
            
            // Calculer les frais en utilisant les méthodes du DAO
            int fraisEnvoi = envoiDao.getFraisEnvoiForMontant(envoi.getMontant());
            int fraisRetrait = envoiDao.getFraisRetraitForMontant(envoi.getMontant());
            
            // Renvoyer les emails
            envoiDao.notifierParMail(envoi, fraisEnvoi, fraisRetrait);
            
            jsonResponse.addProperty("success", true);
            jsonResponse.addProperty("message", "Email renvoyé avec succès");
            
        } catch (Exception e) {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Erreur lors de l'envoi de l'email: " + e.getMessage());
        }
        
        response.setContentType("application/json");
        response.getWriter().print(jsonResponse.toString());
    }
}