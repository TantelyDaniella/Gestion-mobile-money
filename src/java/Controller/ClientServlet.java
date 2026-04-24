package Controller;

import Dao.ClientDao;
import Model.Client;
import Service.RelevePdfService;
import com.lowagie.text.DocumentException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/ClientServlet")
public class ClientServlet extends HttpServlet {

    private ClientDao clientDao;

    public void init() {
        clientDao = new ClientDao();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        try {
            if (action == null) action = "liste";

            switch (action) {
                case "ajouter": ajouterClient(request, response); break;
                case "modifier": modifierClient(request, response); break;
                case "supprimer": supprimerClient(request, response); break;
                case "rechercher": rechercherClient(request, response); break;
                case "verifierNumtel": verifierNumtel(request, response); break;
                case "genererPdf": genererPdf(request, response); break;
                default: listerClients(request, response); break;
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    // ✅ LISTER LES CLIENTS
    private void listerClients(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        List<Client> liste = clientDao.listeClient();
        request.setAttribute("listeClients", liste);
        RequestDispatcher dispatcher = request.getRequestDispatcher("Client.jsp");
        dispatcher.forward(request, response);
    }

    // ✅ RECHERCHER UN CLIENT
    private void rechercherClient(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        
        String motCle = request.getParameter("motCle");
        
        if (motCle != null && !motCle.trim().isEmpty()) {
            List<Client> liste = clientDao.rechercherClient(motCle.trim());
            request.setAttribute("listeClients", liste);
            request.setAttribute("motCle", motCle);
        } else {
            List<Client> liste = clientDao.listeClient();
            request.setAttribute("listeClients", liste);
        }
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("Client.jsp");
        dispatcher.forward(request, response);
    }

    // ✅ AJOUTER UN CLIENT
    private void ajouterClient(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        
        String numtel = request.getParameter("numtel");
        String nom = request.getParameter("nom");
        String sexe = request.getParameter("sexe");
        String ageStr = request.getParameter("age");
        String soldeStr = request.getParameter("solde");
        String mail = request.getParameter("mail");

        // Validation des champs obligatoires
        if (numtel == null || numtel.trim().isEmpty() || 
            nom == null || nom.trim().isEmpty()) {
            
            request.setAttribute("message", "Le numéro de téléphone et le nom sont obligatoires");
            request.setAttribute("typeMessage", "error");
            listerClients(request, response);
            return;
        }

        try {
            // Vérifier si le numéro de téléphone existe déjà
            List<Client> existingClients = clientDao.listeClient();
            for (Client c : existingClients) {
                if (c.getNumTel().equals(numtel.trim())) {
                    request.setAttribute("message", "Ce numéro de téléphone existe déjà !");
                    request.setAttribute("typeMessage", "error");
                    listerClients(request, response);
                    return;
                }
            }

            Integer age = null;
            if (ageStr != null && !ageStr.trim().isEmpty()) {
                age = Integer.parseInt(ageStr.trim());
                if (age < 0 || age > 150) {
                    request.setAttribute("message", "L'âge doit être compris entre 0 et 150 ans");
                    request.setAttribute("typeMessage", "error");
                    listerClients(request, response);
                    return;
                }
            }

            Integer solde = 0;
            if (soldeStr != null && !soldeStr.trim().isEmpty()) {
                solde = Integer.parseInt(soldeStr.trim());
                if (solde < 0) {
                    request.setAttribute("message", "Le solde ne peut pas être négatif");
                    request.setAttribute("typeMessage", "error");
                    listerClients(request, response);
                    return;
                }
            }

            Client client = new Client(numtel.trim(), nom.trim(), sexe, age, solde, mail);
            clientDao.ajouterClient(client);

            request.setAttribute("message", "Client ajouté avec succès !");
            request.setAttribute("typeMessage", "success");
            
        } catch (NumberFormatException e) {
            request.setAttribute("message", "L'âge et le solde doivent être des nombres valides");
            request.setAttribute("typeMessage", "error");
        }
        
        listerClients(request, response);
    }

    // ✅ MODIFIER UN CLIENT
    private void modifierClient(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        
        String numtelOriginal = request.getParameter("numtelOriginal");
        String numtel = request.getParameter("numtel");
        String nom = request.getParameter("nom");
        String sexe = request.getParameter("sexe");
        String ageStr = request.getParameter("age");
        String soldeStr = request.getParameter("solde");
        String mail = request.getParameter("mail");

        // Validation des champs obligatoires
        if (numtel == null || numtel.trim().isEmpty() || 
            nom == null || nom.trim().isEmpty()) {
            
            request.setAttribute("message", "Le numéro de téléphone et le nom sont obligatoires");
            request.setAttribute("typeMessage", "error");
            listerClients(request, response);
            return;
        }

        try {
            // Vérifier si le client existe
            List<Client> allClients = clientDao.listeClient();
            Client existingClient = null;
            for (Client c : allClients) {
                if (c.getNumTel().equals(numtelOriginal)) {
                    existingClient = c;
                    break;
                }
            }

            if (existingClient == null) {
                request.setAttribute("message", "Client non trouvé");
                request.setAttribute("typeMessage", "error");
                listerClients(request, response);
                return;
            }

            // Vérifier si le nouveau numéro n'est pas déjà utilisé par un autre client
            if (!numtelOriginal.equals(numtel.trim())) {
                for (Client c : allClients) {
                    if (c.getNumTel().equals(numtel.trim())) {
                        request.setAttribute("message", "Ce numéro de téléphone est déjà utilisé par un autre client");
                        request.setAttribute("typeMessage", "error");
                        listerClients(request, response);
                        return;
                    }
                }
            }

            Integer age = existingClient.getAge();
            if (ageStr != null && !ageStr.trim().isEmpty()) {
                age = Integer.parseInt(ageStr.trim());
                if (age < 0 || age > 150) {
                    request.setAttribute("message", "L'âge doit être compris entre 0 et 150 ans");
                    request.setAttribute("typeMessage", "error");
                    listerClients(request, response);
                    return;
                }
            }

            Integer solde = existingClient.getSolde();
            if (soldeStr != null && !soldeStr.trim().isEmpty()) {
                solde = Integer.parseInt(soldeStr.trim());
                if (solde < 0) {
                    request.setAttribute("message", "Le solde ne peut pas être négatif");
                    request.setAttribute("typeMessage", "error");
                    listerClients(request, response);
                    return;
                }
            }

            Client client = new Client(numtel.trim(), nom.trim(), sexe, age, solde, mail);
            boolean updated = clientDao.modifierClient(client, numtelOriginal);

            if (updated) {
                request.setAttribute("message", "Client modifié avec succès !");
                request.setAttribute("typeMessage", "success");
            } else {
                request.setAttribute("message", "Erreur lors de la modification du client");
                request.setAttribute("typeMessage", "error");
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("message", "L'âge et le solde doivent être des nombres valides");
            request.setAttribute("typeMessage", "error");
        }
        
        listerClients(request, response);
    }

    // ✅ SUPPRIMER UN CLIENT
    private void supprimerClient(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        
        String numtel = request.getParameter("numtel");

        if (numtel == null || numtel.trim().isEmpty()) {
            request.setAttribute("message", "Numéro de téléphone du client manquant");
            request.setAttribute("typeMessage", "error");
            listerClients(request, response);
            return;
        }

        boolean deleted = clientDao.supprimerClient(numtel.trim());

        if (deleted) {
            request.setAttribute("message", "Client supprimé avec succès !");
            request.setAttribute("typeMessage", "success");
        } else {
            request.setAttribute("message", "Erreur lors de la suppression du client");
            request.setAttribute("typeMessage", "error");
        }
        
        listerClients(request, response);
    }

    // ✅ VÉRIFIER SI UN NUMÉRO DE TÉLÉPHONE EXISTE DÉJÀ (AJAX)
    private void verifierNumtel(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        
        String numtel = request.getParameter("numtel");
        boolean existe = false;
        
        if (numtel != null && !numtel.trim().isEmpty()) {
            List<Client> clients = clientDao.listeClient();
            for (Client c : clients) {
                if (c.getNumTel().equals(numtel.trim())) {
                    existe = true;
                    break;
                }
            }
        }
        
        response.setContentType("text/plain");
        response.getWriter().print(existe);
    }
    
    private void genererPdf(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {

        String numtel = request.getParameter("numtel");
        String moisStr = request.getParameter("mois");
        String anneeStr = request.getParameter("annee");

        if (numtel == null || moisStr == null || anneeStr == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Paramètres manquants");
            return;
        }

        try {
            int mois = Integer.parseInt(moisStr);
            int annee = Integer.parseInt(anneeStr);

            Client client = clientDao.rechercherClientParNumero(numtel);
            if (client == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Client non trouvé");
                return;
            }

            RelevePdfService pdfService = new RelevePdfService();
            pdfService.genererReleve(response, client, mois, annee);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Mois ou année invalide");
        } catch (DocumentException e) {
            throw new IOException("Erreur lors de la génération du PDF", e);
        }
    }
}