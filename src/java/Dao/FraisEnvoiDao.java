/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

import Model.FraisEnvoi;
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
public class FraisEnvoiDao {
    private String url = "jdbc:postgresql://localhost:5432/mobilemoney";
    private String user = "postgres";
    private String pwd = "nancy";
    
    // Requêtes SQL
    private static final String AJOUT_FRAIS_ENVOI = "INSERT INTO fraisenvoi (idenv, montant1, montant2, fraisenv) VALUES (?, ?, ?, ?)";
    private static final String LISTE_FRAIS_ENVOI = "SELECT * FROM fraisenvoi ORDER BY idenv ASC";
    private static final String MISE_A_JOUR_FRAIS_ENVOI = "UPDATE fraisenvoi SET montant1 = ?, montant2 = ?, fraisenv = ? WHERE idenv = ?";
    private static final String SUPPRIMER_FRAIS_ENVOI = "DELETE FROM fraisenvoi WHERE idenv = ?";
    private static final String GENERER_ID_ENV = "SELECT MAX(idenv) AS dernier FROM fraisenvoi";
    
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
    
    // ✅ Générer automatiquement un idenv
    private String genererIdEnv() {
        String nouveauCode = "FRENV-001";

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(GENERER_ID_ENV);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                String dernierCode = rs.getString("dernier");
                if (dernierCode != null && !dernierCode.isEmpty()) {
                    // Extraire le numéro après le tiret
                    String[] parts = dernierCode.split("-");
                    if (parts.length == 2) {
                        int numero = Integer.parseInt(parts[1]);
                        numero++;
                        nouveauCode = String.format("FRENV-%03d", numero);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nouveauCode;
    }
    
    // AJOUTER UN FRAISENVOI
    public void ajouterFraisEnvoi(FraisEnvoi fraisenvoi) throws SQLException {
        String codeGenere = genererIdEnv();
        
        try (Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(AJOUT_FRAIS_ENVOI)) {
            
            preparedStatement.setString(1, codeGenere);        
            preparedStatement.setInt(2, fraisenvoi.getMontant1());
            preparedStatement.setInt(3, fraisenvoi.getMontant2());
            preparedStatement.setInt(4, fraisenvoi.getFraisEnv());
            
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            printSQLException(e);
        }
    }
           
    // ✅ LISTER TOUS LES FRAISENVOIS
    public List<FraisEnvoi> listeFraisEnvoi() {
        List<FraisEnvoi> fraisenvoi = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(LISTE_FRAIS_ENVOI)) {
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {            
                String idenv = rs.getString("idenv");
                Integer montant1 = rs.getInt("montant1");
                Integer montant2 = rs.getInt("montant2");
                Integer fraisenv = rs.getInt("fraisenv");
                
                fraisenvoi.add(new FraisEnvoi(idenv, montant1, montant2, fraisenv));
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return fraisenvoi;
    }
    
    // ✅ MODIFIER UN FRAISENVOI
    public boolean modifierFraisEnvoi(FraisEnvoi fraisenvoi) throws SQLException {
        boolean rowUpdated;
        try (Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(MISE_A_JOUR_FRAIS_ENVOI)) {
            statement.setInt(1, fraisenvoi.getMontant1());
            statement.setInt(2, fraisenvoi.getMontant2());
            statement.setInt(3, fraisenvoi.getFraisEnv());
            statement.setString(4, fraisenvoi.getIdEnv());
            rowUpdated = statement.executeUpdate() > 0;
        }
        return rowUpdated;
    }
    
    // ✅ SUPPRIMER UN FRAISENVOI
    public boolean supprimerFraisEnvoi(String idenv) throws SQLException {
        boolean rowDeleted;
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(SUPPRIMER_FRAIS_ENVOI)) {
            statement.setString(1, idenv);
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
