package Controller;

import Dao.FraisEnvoiDao;
import Model.FraisEnvoi;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/FraisEnvoiServlet")
public class FraisEnvoiServlet extends HttpServlet {

    private FraisEnvoiDao fraisEnvoiDao;

    public void init() {
        fraisEnvoiDao = new FraisEnvoiDao();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        try {
            if (action == null) action = "liste";

            switch (action) {
                case "ajouter": ajouterFraisEnvoi(request, response); break;
                case "modifier": modifierFraisEnvoi(request, response); break;
                case "supprimer": supprimerFraisEnvoi(request, response); break;
                default: listerFraisEnvoi(request, response); break;
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    // ✅ LISTER LES FRAIS D'ENVOI
    private void listerFraisEnvoi(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        List<FraisEnvoi> liste = fraisEnvoiDao.listeFraisEnvoi();
        request.setAttribute("listeFraisEnvoi", liste);
        RequestDispatcher dispatcher = request.getRequestDispatcher("FraisEnvoi.jsp");
        dispatcher.forward(request, response);
    }

    // ✅ AJOUTER UN FRAIS D'ENVOI
    private void ajouterFraisEnvoi(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        
        String montant1Str = request.getParameter("montant1");
        String montant2Str = request.getParameter("montant2");
        String fraisenvStr = request.getParameter("fraisenv");

        // Validation des champs
        if (montant1Str == null || montant1Str.trim().isEmpty() || 
            montant2Str == null || montant2Str.trim().isEmpty() || 
            fraisenvStr == null || fraisenvStr.trim().isEmpty()) {
            
            request.setAttribute("message", "Tous les champs sont obligatoires");
            request.setAttribute("typeMessage", "error");
            listerFraisEnvoi(request, response);
            return;
        }

        try {
            Integer montant1 = Integer.parseInt(montant1Str.trim());
            Integer montant2 = Integer.parseInt(montant2Str.trim());
            Integer fraisenv = Integer.parseInt(fraisenvStr.trim());

            // Validation des valeurs
            if (montant1 < 0) {
                request.setAttribute("message", "Le montant minimum doit être positif");
                request.setAttribute("typeMessage", "error");
                listerFraisEnvoi(request, response);
                return;
            }

            if (montant2 <= montant1) {
                request.setAttribute("message", "Le montant maximum doit être supérieur au montant minimum");
                request.setAttribute("typeMessage", "error");
                listerFraisEnvoi(request, response);
                return;
            }

            if (fraisenv < 0) {
                request.setAttribute("message", "Les frais d'envoi doivent être positifs");
                request.setAttribute("typeMessage", "error");
                listerFraisEnvoi(request, response);
                return;
            }

            // Vérifier si la plage de montants n'existe pas déjà
            List<FraisEnvoi> existingList = fraisEnvoiDao.listeFraisEnvoi();
            for (FraisEnvoi existing : existingList) {
                // Vérifier s'il y a chevauchement ou doublon
                if ((montant1 >= existing.getMontant1() && montant1 <= existing.getMontant2()) ||
                    (montant2 >= existing.getMontant1() && montant2 <= existing.getMontant2()) ||
                    (montant1 <= existing.getMontant1() && montant2 >= existing.getMontant2())) {
                    request.setAttribute("message", "Cette plage de montants chevauche un barème existant");
                    request.setAttribute("typeMessage", "error");
                    listerFraisEnvoi(request, response);
                    return;
                }
            }

            FraisEnvoi fraisEnvoi = new FraisEnvoi(montant1, montant2, fraisenv);
            fraisEnvoiDao.ajouterFraisEnvoi(fraisEnvoi);

            request.setAttribute("message", "Barème de frais d'envoi ajouté avec succès !");
            request.setAttribute("typeMessage", "success");
            
        } catch (NumberFormatException e) {
            request.setAttribute("message", "Les montants doivent être des nombres valides");
            request.setAttribute("typeMessage", "error");
        }
        
        listerFraisEnvoi(request, response);
    }

    // ✅ MODIFIER UN FRAIS D'ENVOI
    private void modifierFraisEnvoi(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        
        String idenv = request.getParameter("idenv");
        String montant1Str = request.getParameter("montant1");
        String montant2Str = request.getParameter("montant2");
        String fraisenvStr = request.getParameter("fraisenv");

        // Validation des champs
        if (idenv == null || idenv.trim().isEmpty() ||
            montant1Str == null || montant1Str.trim().isEmpty() || 
            montant2Str == null || montant2Str.trim().isEmpty() || 
            fraisenvStr == null || fraisenvStr.trim().isEmpty()) {
            
            request.setAttribute("message", "Tous les champs sont obligatoires");
            request.setAttribute("typeMessage", "error");
            listerFraisEnvoi(request, response);
            return;
        }

        try {
            Integer montant1 = Integer.parseInt(montant1Str.trim());
            Integer montant2 = Integer.parseInt(montant2Str.trim());
            Integer fraisenv = Integer.parseInt(fraisenvStr.trim());

            // Validation des valeurs
            if (montant1 < 0) {
                request.setAttribute("message", "Le montant minimum doit être positif");
                request.setAttribute("typeMessage", "error");
                listerFraisEnvoi(request, response);
                return;
            }

            if (montant2 <= montant1) {
                request.setAttribute("message", "Le montant maximum doit être supérieur au montant minimum");
                request.setAttribute("typeMessage", "error");
                listerFraisEnvoi(request, response);
                return;
            }

            if (fraisenv < 0) {
                request.setAttribute("message", "Les frais d'envoi doivent être positifs");
                request.setAttribute("typeMessage", "error");
                listerFraisEnvoi(request, response);
                return;
            }

            // Vérifier si le barème existe
            FraisEnvoi existingFrais = null;
            List<FraisEnvoi> allFrais = fraisEnvoiDao.listeFraisEnvoi();
            for (FraisEnvoi f : allFrais) {
                if (f.getIdEnv().equals(idenv)) {
                    existingFrais = f;
                    break;
                }
            }

            if (existingFrais == null) {
                request.setAttribute("message", "Barème de frais d'envoi non trouvé");
                request.setAttribute("typeMessage", "error");
                listerFraisEnvoi(request, response);
                return;
            }

            // Vérifier si la nouvelle plage de montants n'entre pas en conflit avec d'autres barèmes
            for (FraisEnvoi f : allFrais) {
                if (!f.getIdEnv().equals(idenv)) {
                    if ((montant1 >= f.getMontant1() && montant1 <= f.getMontant2()) ||
                        (montant2 >= f.getMontant1() && montant2 <= f.getMontant2()) ||
                        (montant1 <= f.getMontant1() && montant2 >= f.getMontant2())) {
                        request.setAttribute("message", "Cette plage de montants chevauche un barème existant");
                        request.setAttribute("typeMessage", "error");
                        listerFraisEnvoi(request, response);
                        return;
                    }
                }
            }

            FraisEnvoi fraisEnvoi = new FraisEnvoi(idenv, montant1, montant2, fraisenv);
            boolean updated = fraisEnvoiDao.modifierFraisEnvoi(fraisEnvoi);

            if (updated) {
                request.setAttribute("message", "Barème de frais d'envoi modifié avec succès !");
                request.setAttribute("typeMessage", "success");
            } else {
                request.setAttribute("message", "Erreur lors de la modification du barème");
                request.setAttribute("typeMessage", "error");
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("message", "Les montants doivent être des nombres valides");
            request.setAttribute("typeMessage", "error");
        }
        
        listerFraisEnvoi(request, response);
    }

    // ✅ SUPPRIMER UN FRAIS D'ENVOI
    private void supprimerFraisEnvoi(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        
        String idenv = request.getParameter("idenv");

        if (idenv == null || idenv.trim().isEmpty()) {
            request.setAttribute("message", "Identifiant du barème manquant");
            request.setAttribute("typeMessage", "error");
            listerFraisEnvoi(request, response);
            return;
        }

        boolean deleted = fraisEnvoiDao.supprimerFraisEnvoi(idenv);

        if (deleted) {
            request.setAttribute("message", "Barème de frais d'envoi supprimé avec succès !");
            request.setAttribute("typeMessage", "success");
        } else {
            request.setAttribute("message", "Erreur lors de la suppression du barème");
            request.setAttribute("typeMessage", "error");
        }
        
        listerFraisEnvoi(request, response);
    }
}