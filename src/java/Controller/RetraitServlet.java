package Controller;

import Dao.RetraitDao;
import Dao.ClientDao;
import Dao.FraisRecepDao;
import Model.Retrait;
import Model.Client;
import Model.FraisRecep;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/RetraitServlet")
public class RetraitServlet extends HttpServlet {

    private RetraitDao retraitDao;
    private ClientDao clientDao;
    private FraisRecepDao fraisRecepDao;

    public void init() {
        retraitDao = new RetraitDao();
        clientDao = new ClientDao();
        fraisRecepDao = new FraisRecepDao();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        try {
            if (action == null) action = "liste";

            switch (action) {
                case "ajouter": ajouterRetrait(request, response); break;
                case "modifier": modifierRetrait(request, response); break;
                case "supprimer": supprimerRetrait(request, response); break;
                case "rechercherParDate": rechercherParDate(request, response); break;
                case "getFraisRates": getFraisRates(request, response); break;
                default: listerRetraits(request, response); break;
            }
        } catch (SQLException | ParseException e) {
            throw new ServletException(e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    // ✅ LISTER LES RETRAITS ET LES CLIENTS POUR LE FORMULAIRE
    private void listerRetraits(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        List<Retrait> liste = retraitDao.listeRetrait();
        List<Client> clients = clientDao.listeClient();
        request.setAttribute("listeRetraits", liste);
        request.setAttribute("listeClients", clients);
        RequestDispatcher dispatcher = request.getRequestDispatcher("Retrait.jsp");
        dispatcher.forward(request, response);
    }

    // ✅ AJOUTER UN RETRAIT
    private void ajouterRetrait(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        
        String numtel = request.getParameter("numtel");
        String montantStr = request.getParameter("montant");

        // Validation des champs obligatoires
        if (numtel == null || numtel.trim().isEmpty()) {
            request.setAttribute("message", "Veuillez sélectionner un client");
            request.setAttribute("typeMessage", "error");
            listerRetraits(request, response);
            return;
        }

        if (montantStr == null || montantStr.trim().isEmpty()) {
            request.setAttribute("message", "Veuillez saisir un montant");
            request.setAttribute("typeMessage", "error");
            listerRetraits(request, response);
            return;
        }

        try {
            int montant = Integer.parseInt(montantStr.trim());
            
            if (montant <= 0) {
                request.setAttribute("message", "Le montant doit être supérieur à 0");
                request.setAttribute("typeMessage", "error");
                listerRetraits(request, response);
                return;
            }

            // Vérifier si le client existe
            Client client = clientDao.rechercherClientParNumero(numtel.trim());
            if (client == null) {
                request.setAttribute("message", "Client non trouvé");
                request.setAttribute("typeMessage", "error");
                listerRetraits(request, response);
                return;
            }

            // Créer l'objet Retrait avec la date actuelle
            Retrait retrait = new Retrait(numtel.trim(), montant, new Date());
            
            // Appeler la méthode ajouterRetrait qui gère la vérification du solde et la déduction
            retraitDao.ajouterRetrait(retrait);

            request.setAttribute("message", "Retrait effectué avec succès !");
            request.setAttribute("typeMessage", "success");
            
        } catch (NumberFormatException e) {
            request.setAttribute("message", "Le montant doit être un nombre valide");
            request.setAttribute("typeMessage", "error");
        } catch (SQLException e) {
            if (e.getMessage().contains("Solde insuffisant")) {
                request.setAttribute("message", e.getMessage());
                request.setAttribute("typeMessage", "error");
            } else {
                request.setAttribute("message", "Erreur lors de l'ajout du retrait: " + e.getMessage());
                request.setAttribute("typeMessage", "error");
            }
        }
        
        listerRetraits(request, response);
    }

    // ✅ MODIFIER UN RETRAIT
    private void modifierRetrait(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        
        String idrecep = request.getParameter("idrecep");
        String numtel = request.getParameter("numtel");
        String montantStr = request.getParameter("montant");

        // Validation des champs obligatoires
        if (idrecep == null || idrecep.trim().isEmpty()) {
            request.setAttribute("message", "ID du retrait manquant");
            request.setAttribute("typeMessage", "error");
            listerRetraits(request, response);
            return;
        }

        if (numtel == null || numtel.trim().isEmpty()) {
            request.setAttribute("message", "Veuillez sélectionner un client");
            request.setAttribute("typeMessage", "error");
            listerRetraits(request, response);
            return;
        }

        if (montantStr == null || montantStr.trim().isEmpty()) {
            request.setAttribute("message", "Veuillez saisir un montant");
            request.setAttribute("typeMessage", "error");
            listerRetraits(request, response);
            return;
        }

        try {
            int montant = Integer.parseInt(montantStr.trim());
            
            if (montant <= 0) {
                request.setAttribute("message", "Le montant doit être supérieur à 0");
                request.setAttribute("typeMessage", "error");
                listerRetraits(request, response);
                return;
            }

            // Récupérer l'ancien retrait pour connaître l'ancien montant
            List<Retrait> allRetraits = retraitDao.listeRetrait();
            Retrait ancienRetrait = null;
            for (Retrait r : allRetraits) {
                if (r.getIdRecep().equals(idrecep.trim())) {
                    ancienRetrait = r;
                    break;
                }
            }

            if (ancienRetrait == null) {
                request.setAttribute("message", "Retrait non trouvé");
                request.setAttribute("typeMessage", "error");
                listerRetraits(request, response);
                return;
            }

            // Créer l'objet Retrait modifié
            Retrait retrait = new Retrait(idrecep.trim(), numtel.trim(), montant, ancienRetrait.getDateRecep());
            
            boolean updated = retraitDao.modifierRetrait(retrait);

            if (updated) {
                request.setAttribute("message", "Retrait modifié avec succès !");
                request.setAttribute("typeMessage", "success");
            } else {
                request.setAttribute("message", "Erreur lors de la modification du retrait");
                request.setAttribute("typeMessage", "error");
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("message", "Le montant doit être un nombre valide");
            request.setAttribute("typeMessage", "error");
        }
        
        listerRetraits(request, response);
    }

    // ✅ SUPPRIMER UN RETRAIT
    private void supprimerRetrait(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        
        String idrecep = request.getParameter("idrecep");

        if (idrecep == null || idrecep.trim().isEmpty()) {
            request.setAttribute("message", "ID du retrait manquant");
            request.setAttribute("typeMessage", "error");
            listerRetraits(request, response);
            return;
        }

        boolean deleted = retraitDao.supprimerRetrait(idrecep.trim());

        if (deleted) {
            request.setAttribute("message", "Retrait supprimé avec succès !");
            request.setAttribute("typeMessage", "success");
        } else {
            request.setAttribute("message", "Erreur lors de la suppression du retrait");
            request.setAttribute("typeMessage", "error");
        }
        
        listerRetraits(request, response);
    }

    // ✅ RECHERCHER PAR DATE
    private void rechercherParDate(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException, ParseException {
        
        String dateStr = request.getParameter("dateRecherche");
        List<Retrait> liste;
        List<Client> clients = clientDao.listeClient();
        
        if (dateStr != null && !dateStr.trim().isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date dateRecherche = sdf.parse(dateStr.trim());
            liste = retraitDao.rechercherParDateRecep(dateRecherche);
            request.setAttribute("dateRecherche", dateStr);
        } else {
            liste = retraitDao.listeRetrait();
        }
        
        request.setAttribute("listeRetraits", liste);
        request.setAttribute("listeClients", clients);
        RequestDispatcher dispatcher = request.getRequestDispatcher("Retrait.jsp");
        dispatcher.forward(request, response);
    }

    // ✅ RÉCUPÉRER LES TAUX DE FRAIS DE RETRAIT POUR LE JS (AJAX)
    private void getFraisRates(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        
        List<FraisRecep> fraisList = fraisRecepDao.listeFraisRecep();
        
        // Construire le JSON manuellement
        StringBuilder json = new StringBuilder();
        json.append("{\"fraisRetrait\":[");
        
        for (int i = 0; i < fraisList.size(); i++) {
            FraisRecep f = fraisList.get(i);
            json.append("{\"montant1\":").append(f.getMontant1())
                .append(",\"montant2\":").append(f.getMontant2())
                .append(",\"frais\":").append(f.getFraisRec())
                .append("}");
            if (i < fraisList.size() - 1) {
                json.append(",");
            }
        }
        
        json.append("]}");
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json.toString());
    }
}