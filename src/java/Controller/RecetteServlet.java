package Controller;

import Dao.EnvoiDao;
import Dao.RetraitDao;
import java.io.IOException;
import java.sql.SQLException;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/RecetteServlet")
public class RecetteServlet extends HttpServlet {

    private EnvoiDao envoiDao;
    private RetraitDao retraitDao;

    public void init() {
        envoiDao = new EnvoiDao();
        retraitDao = new RetraitDao();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("getRecette".equals(action)) {
            // Réponse JSON pour AJAX
            try {
                int totalFraisEnvoi = envoiDao.getSommeFraisEnvoiReels();
                int totalFraisRetrait = retraitDao.getSommeFraisRetraitReels();
                int recetteTotale = totalFraisEnvoi + totalFraisRetrait;

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(
                    "{\"totalFraisEnvoi\":" + totalFraisEnvoi + 
                    ",\"totalFraisRetrait\":" + totalFraisRetrait + 
                    ",\"recetteTotale\":" + recetteTotale + "}"
                );
            } catch (SQLException e) {
                response.setStatus(500);
                response.getWriter().write("{\"error\":\"Erreur SQL\"}");
            }
        } else {
            // Redirection vers MobileMoney.jsp
            RequestDispatcher dispatcher = request.getRequestDispatcher("MobileMoney.jsp");
            dispatcher.forward(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}