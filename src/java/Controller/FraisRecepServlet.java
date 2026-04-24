package Controller;

import Dao.FraisRecepDao;
import Model.FraisRecep;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/FraisRecepServlet")
public class FraisRecepServlet extends HttpServlet {

    private FraisRecepDao fraisRecepDao;

    public void init() {
        fraisRecepDao = new FraisRecepDao();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        try {
            if (action == null) action = "liste";

            switch (action) {
                case "ajouter": ajouterFraisRecep(request, response); break;
                case "modifier": modifierFraisRecep(request, response); break;
                case "supprimer": supprimerFraisRecep(request, response); break;
                default: listerFraisRecep(request, response); break;
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    // ✅ LISTER LES FRAIS DE RETRAIT
    private void listerFraisRecep(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        List<FraisRecep> liste = fraisRecepDao.listeFraisRecep();
        request.setAttribute("listeFraisRecep", liste);
        RequestDispatcher dispatcher = request.getRequestDispatcher("FraisRecep.jsp");
        dispatcher.forward(request, response);
    }

    // ✅ AJOUTER UN FRAIS DE RETRAIT
    private void ajouterFraisRecep(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        
        String montant1Str = request.getParameter("montant1");
        String montant2Str = request.getParameter("montant2");
        String fraisrecStr = request.getParameter("fraisrec");

        // Validation des champs
        if (montant1Str == null || montant1Str.trim().isEmpty() || 
            montant2Str == null || montant2Str.trim().isEmpty() || 
            fraisrecStr == null || fraisrecStr.trim().isEmpty()) {
            
            request.setAttribute("message", "Tous les champs sont obligatoires");
            request.setAttribute("typeMessage", "error");
            listerFraisRecep(request, response);
            return;
        }

        try {
            Integer montant1 = Integer.parseInt(montant1Str.trim());
            Integer montant2 = Integer.parseInt(montant2Str.trim());
            Integer fraisrec = Integer.parseInt(fraisrecStr.trim());

            // Validation des valeurs
            if (montant1 < 0) {
                request.setAttribute("message", "Le montant minimum doit être positif");
                request.setAttribute("typeMessage", "error");
                listerFraisRecep(request, response);
                return;
            }

            if (montant2 <= montant1) {
                request.setAttribute("message", "Le montant maximum doit être supérieur au montant minimum");
                request.setAttribute("typeMessage", "error");
                listerFraisRecep(request, response);
                return;
            }

            if (fraisrec < 0) {
                request.setAttribute("message", "Les frais de retrait doivent être positifs");
                request.setAttribute("typeMessage", "error");
                listerFraisRecep(request, response);
                return;
            }

            // Vérifier si la plage de montants n'existe pas déjà
            List<FraisRecep> existingList = fraisRecepDao.listeFraisRecep();
            for (FraisRecep existing : existingList) {
                // Vérifier s'il y a chevauchement ou doublon
                if ((montant1 >= existing.getMontant1() && montant1 <= existing.getMontant2()) ||
                    (montant2 >= existing.getMontant1() && montant2 <= existing.getMontant2()) ||
                    (montant1 <= existing.getMontant1() && montant2 >= existing.getMontant2())) {
                    request.setAttribute("message", "Cette plage de montants chevauche un barème existant");
                    request.setAttribute("typeMessage", "error");
                    listerFraisRecep(request, response);
                    return;
                }
            }

            FraisRecep fraisRecep = new FraisRecep(montant1, montant2, fraisrec);
            fraisRecepDao.ajouterFraisRecep(fraisRecep);

            request.setAttribute("message", "Barème de frais de retrait ajouté avec succès !");
            request.setAttribute("typeMessage", "success");
            
        } catch (NumberFormatException e) {
            request.setAttribute("message", "Les montants doivent être des nombres valides");
            request.setAttribute("typeMessage", "error");
        }
        
        listerFraisRecep(request, response);
    }

    // ✅ MODIFIER UN FRAIS DE RETRAIT
    private void modifierFraisRecep(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        
        String idrec = request.getParameter("idrec");
        String montant1Str = request.getParameter("montant1");
        String montant2Str = request.getParameter("montant2");
        String fraisrecStr = request.getParameter("fraisrec");

        // Validation des champs
        if (idrec == null || idrec.trim().isEmpty() ||
            montant1Str == null || montant1Str.trim().isEmpty() || 
            montant2Str == null || montant2Str.trim().isEmpty() || 
            fraisrecStr == null || fraisrecStr.trim().isEmpty()) {
            
            request.setAttribute("message", "Tous les champs sont obligatoires");
            request.setAttribute("typeMessage", "error");
            listerFraisRecep(request, response);
            return;
        }

        try {
            Integer montant1 = Integer.parseInt(montant1Str.trim());
            Integer montant2 = Integer.parseInt(montant2Str.trim());
            Integer fraisrec = Integer.parseInt(fraisrecStr.trim());

            // Validation des valeurs
            if (montant1 < 0) {
                request.setAttribute("message", "Le montant minimum doit être positif");
                request.setAttribute("typeMessage", "error");
                listerFraisRecep(request, response);
                return;
            }

            if (montant2 <= montant1) {
                request.setAttribute("message", "Le montant maximum doit être supérieur au montant minimum");
                request.setAttribute("typeMessage", "error");
                listerFraisRecep(request, response);
                return;
            }

            if (fraisrec < 0) {
                request.setAttribute("message", "Les frais de retrait doivent être positifs");
                request.setAttribute("typeMessage", "error");
                listerFraisRecep(request, response);
                return;
            }

            // Vérifier si le barème existe
            FraisRecep existingFrais = null;
            List<FraisRecep> allFrais = fraisRecepDao.listeFraisRecep();
            for (FraisRecep f : allFrais) {
                if (f.getIdRec().equals(idrec)) {
                    existingFrais = f;
                    break;
                }
            }

            if (existingFrais == null) {
                request.setAttribute("message", "Barème de frais de retrait non trouvé");
                request.setAttribute("typeMessage", "error");
                listerFraisRecep(request, response);
                return;
            }

            // Vérifier si la nouvelle plage de montants n'entre pas en conflit avec d'autres barèmes
            for (FraisRecep f : allFrais) {
                if (!f.getIdRec().equals(idrec)) {
                    if ((montant1 >= f.getMontant1() && montant1 <= f.getMontant2()) ||
                        (montant2 >= f.getMontant1() && montant2 <= f.getMontant2()) ||
                        (montant1 <= f.getMontant1() && montant2 >= f.getMontant2())) {
                        request.setAttribute("message", "Cette plage de montants chevauche un barème existant");
                        request.setAttribute("typeMessage", "error");
                        listerFraisRecep(request, response);
                        return;
                    }
                }
            }

            FraisRecep fraisRecep = new FraisRecep(idrec, montant1, montant2, fraisrec);
            boolean updated = fraisRecepDao.modifierFraisRecep(fraisRecep);

            if (updated) {
                request.setAttribute("message", "Barème de frais de retrait modifié avec succès !");
                request.setAttribute("typeMessage", "success");
            } else {
                request.setAttribute("message", "Erreur lors de la modification du barème");
                request.setAttribute("typeMessage", "error");
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("message", "Les montants doivent être des nombres valides");
            request.setAttribute("typeMessage", "error");
        }
        
        listerFraisRecep(request, response);
    }

    // ✅ SUPPRIMER UN FRAIS DE RETRAIT
    private void supprimerFraisRecep(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        
        String idrec = request.getParameter("idrec");

        if (idrec == null || idrec.trim().isEmpty()) {
            request.setAttribute("message", "Identifiant du barème manquant");
            request.setAttribute("typeMessage", "error");
            listerFraisRecep(request, response);
            return;
        }

        boolean deleted = fraisRecepDao.supprimerFraisRecep(idrec);

        if (deleted) {
            request.setAttribute("message", "Barème de frais de retrait supprimé avec succès !");
            request.setAttribute("typeMessage", "success");
        } else {
            request.setAttribute("message", "Erreur lors de la suppression du barème");
            request.setAttribute("typeMessage", "error");
        }
        
        listerFraisRecep(request, response);
    }
}