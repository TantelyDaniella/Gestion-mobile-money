/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

import Model.Client;
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
public class ClientDao {
    private String url = "jdbc:postgresql://localhost:5432/mobilemoney";
    private String user = "postgres";
    private String pwd = "nancy";
    
    // Requêtes SQL
    private static final String AJOUT_CLIENT = "INSERT INTO client (numtel, nom, sexe, age, solde, mail) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String LISTE_CLIENT = "SELECT * FROM client ORDER BY nom ASC";
    private static final String MISE_A_JOUR_CLIENT = "UPDATE client SET numtel = ?, nom = ?, sexe = ?, age = ?, solde = ?, mail = ? WHERE numtel = ?";
    private static final String SUPPRIMER_CLIENT = "DELETE FROM client WHERE numtel = ?";
    private static final String RECHERCHE_CLIENT = "SELECT * FROM client WHERE LOWER(numtel) LIKE ? OR LOWER(nom) LIKE ? OR LOWER(sexe) LIKE ? OR CAST(age as TEXT) LIKE ? OR  CAST(solde as TEXT) like ? OR LOWER(mail) LIKE ?";
    private static final String RECHERCHE_CLIENT_PAR_NUMERO_TEL = "SELECT * FROM client WHERE numtel = ?";
    
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
    
    // Recherche de client par mot-clé
    public List<Client> rechercherClient(String motCle) {
        List<Client> clients = new ArrayList<>();
        String motCleLike = "%" + motCle.toLowerCase() + "%";
        
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(RECHERCHE_CLIENT)) {
            
            preparedStatement.setString(1, motCleLike);
            preparedStatement.setString(2, motCleLike);
            preparedStatement.setString(3, motCleLike);
            preparedStatement.setString(4, motCleLike);
            preparedStatement.setString(5, motCleLike);
            preparedStatement.setString(6, motCleLike); 
             
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                String numtel = rs.getString("numtel");
                String nom = rs.getString("nom");
                String sexe = rs.getString("sexe");
                Integer age = rs.getInt("age");
                Integer solde = rs.getInt("solde");
                String mail = rs.getString("mail");

                clients.add(new Client(numtel, nom, sexe, age, solde, mail));
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return clients;
    }
    
    // AJOUTER UN CLIENT
    public void ajouterClient(Client client) throws SQLException {
        try (Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(AJOUT_CLIENT)){
            
            preparedStatement.setString(1, client.getNumTel());  
            preparedStatement.setString(2, client.getNom()); 
            preparedStatement.setString(3, client.getSexe()); 
            preparedStatement.setInt(4, client.getAge());
            preparedStatement.setInt(5, client.getSolde());
            preparedStatement.setString(6, client.getMail()); 
            
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            printSQLException(e);
        }
    }
           
    // ✅ LISTER TOUS LES CLIENTS
    public List<Client> listeClient() {
        List<Client> client = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(LISTE_CLIENT)) {
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {            
                String numtel = rs.getString("numtel");
                String nom = rs.getString("nom");
                String sexe = rs.getString("sexe");
                Integer age = rs.getInt("age");
                Integer solde = rs.getInt("solde");
                String mail = rs.getString("mail");
                
                client.add(new Client(numtel, nom, sexe, age, solde, mail));
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return client;
    }
    
    // ✅ MODIFIER UN CLIENT
    public boolean modifierClient(Client client, String ancienNumtel) throws SQLException {
        boolean rowUpdated;
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(MISE_A_JOUR_CLIENT)) {
            statement.setString(1, client.getNumTel());  // Nouveau numéro
            statement.setString(2, client.getNom()); 
            statement.setString(3, client.getSexe()); 
            statement.setInt(4, client.getAge());
            statement.setInt(5, client.getSolde());
            statement.setString(6, client.getMail());
            statement.setString(7, ancienNumtel);  // Ancien numéro pour la condition WHERE
            rowUpdated = statement.executeUpdate() > 0;
        }
        return rowUpdated;
    }
    
    // ✅ SUPPRIMER UN Client
    public boolean supprimerClient(String numtel) throws SQLException {
        boolean rowDeleted;
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(SUPPRIMER_CLIENT)) {
            statement.setString(1, numtel);
            rowDeleted = statement.executeUpdate() > 0;
        }
        return rowDeleted;
    }

    public Client rechercherClientParNumero(String numtel) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(RECHERCHE_CLIENT_PAR_NUMERO_TEL)) {
            stmt.setString(1, numtel);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Client(
                    rs.getString("numtel"),
                    rs.getString("nom"),
                    rs.getString("sexe"),
                    rs.getInt("age"),
                    rs.getInt("solde"),
                    rs.getString("mail")
                );
            }
        }
        return null;
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
