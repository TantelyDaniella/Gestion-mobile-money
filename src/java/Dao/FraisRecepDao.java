/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

import Model.FraisRecep;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Asus
 */
public class FraisRecepDao {
    private String url = "jdbc:postgresql://localhost:5432/mobilemoney";
    private String user = "postgres";
    private String pwd = "nancy";
    
    // Requêtes SQL
    private static final String AJOUT_FRAIS_RECEP = "INSERT INTO fraisrecep (idrec, montant1, montant2, fraisrec) VALUES (?, ?, ?, ?)";
    private static final String LISTE_FRAIS_RECEP = "SELECT * FROM fraisrecep ORDER BY idrec ASC";
    private static final String MISE_A_JOUR_FRAIS_RECEP = "UPDATE fraisrecep SET montant1 = ?, montant2 = ?, fraisrec = ? WHERE idrec = ?";
    private static final String SUPPRIMER_FRAIS_RECEP = "DELETE FROM fraisrecep WHERE idrec = ?";
    private static final String GENERER_ID_REC = "SELECT MAX(idrec) AS dernier FROM fraisrecep";
    
    // Connexion à Postgresql
    protected Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url, user, pwd);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return connection;
    }
    
    // ✅ Générer automatiquement un idrec
    private String genererIdEnv() {
        String nouveauCode = "FRREC-001";

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(GENERER_ID_REC);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                String dernierCode = rs.getString("dernier");
                if (dernierCode != null && !dernierCode.isEmpty()) {
                    // Extraire le numéro après le tiret
                    String[] parts = dernierCode.split("-");
                    if (parts.length == 2) {
                        int numero = Integer.parseInt(parts[1]);
                        numero++;
                        nouveauCode = String.format("FRREC-%03d", numero);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nouveauCode;
    }
    
    // AJOUTER UN FRAISRECEP
    public void ajouterFraisRecep(FraisRecep fraisrecep) throws SQLException {
        String codeGenere = genererIdEnv();
        
        try (Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(AJOUT_FRAIS_RECEP)) {
            
            preparedStatement.setString(1, codeGenere);        
            preparedStatement.setInt(2, fraisrecep.getMontant1());
            preparedStatement.setInt(3, fraisrecep.getMontant2());
            preparedStatement.setInt(4, fraisrecep.getFraisRec());
            
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            printSQLException(e);
        }
    }
           
    // ✅ LISTER TOUS LES FRAISRECEPS
    public List<FraisRecep> listeFraisRecep() {
        List<FraisRecep> fraisrecep = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(LISTE_FRAIS_RECEP)) {
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {            
                String idrec = rs.getString("idrec");
                Integer montant1 = rs.getInt("montant1");
                Integer montant2 = rs.getInt("montant2");
                Integer fraisrec = rs.getInt("fraisrec");
                
                fraisrecep.add(new FraisRecep(idrec, montant1, montant2, fraisrec));
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return fraisrecep;
    }
    
    // ✅ MODIFIER UN FRAISRECEP
    public boolean modifierFraisRecep(FraisRecep fraisrecep) throws SQLException {
        boolean rowUpdated;
        try (Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(MISE_A_JOUR_FRAIS_RECEP)) {
            statement.setInt(1, fraisrecep.getMontant1());
            statement.setInt(2, fraisrecep.getMontant2());
            statement.setInt(3, fraisrecep.getFraisRec());
            statement.setString(4, fraisrecep.getIdRec());
            rowUpdated = statement.executeUpdate() > 0;
        }
        return rowUpdated;
    }
    
    // ✅ SUPPRIMER UN FRAISERECEP
    public boolean supprimerFraisRecep(String idrec) throws SQLException {
        boolean rowDeleted;
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(SUPPRIMER_FRAIS_RECEP)) {
            statement.setString(1, idrec);
            rowDeleted = statement.executeUpdate() > 0;
        }
        return rowDeleted;
    }
      
    // Gestion des erreurs SQL
    private void printSQLException(SQLException ex) {
        for (Throwable e : ex) {
            if (e instanceof SQLException) {
                System.err.println("SQLState: " + ((SQLException) e).getSQLState());
                System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
                System.err.println("Message: " + e.getMessage());
                Throwable t = ex.getCause();
                while (t != null) {
                    System.out.println("Cause: " + t);
                    t = t.getCause();
                }
            }
        }
    }    
    
}
