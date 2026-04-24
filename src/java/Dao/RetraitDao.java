/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

import Model.Retrait;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Asus
 */
public class RetraitDao {
    private String url = "jdbc:postgresql://localhost:5432/mobilemoney";
    private String user = "postgres";
    private String pwd = "nancy";
    
    // Requêtes SQL
    private static final String AJOUT_RETRAIT = "INSERT INTO retrait (idrecep, numtel, montant, daterecep) VALUES (?, ?, ?, ?)";
    private static final String LISTE_RETRAIT = "SELECT * FROM retrait ORDER BY idrecep ASC";
    private static final String MISE_A_JOUR_RETRAIT = "UPDATE retrait SET numtel = ?, montant = ? WHERE idrecep = ?";
    private static final String SUPPRIMER_RETRAIT = "DELETE FROM retrait WHERE idrecep = ?";
    private static final String GENERER_ID_RETRAIT = "SELECT MAX(idrecep) AS dernier FROM retrait";
    
    private static final String VERIFIER_SOLDE_SUFFISANT = "SELECT solde FROM client WHERE numtel = ? AND solde >= ?";
    private static final String RECHERCHE_PAR_DATERECEP = "SELECT * FROM retrait WHERE daterecep = ? ORDER BY idrecep ASC";
    private static final String RECHERCHER_FRAIS_RETRAIT_PAR_MONTANT = "SELECT fraisrec FROM fraisrecep WHERE ? BETWEEN montant1 AND montant2";
    private static final String METTRE_A_JOUR_SOLDE_CLIENT_RETRAIT = "UPDATE client SET solde = solde - ? WHERE numtel = ?";
    
    private static final String SOMME_FRAIS_RETRAIT_REELS = "SELECT COALESCE(SUM(f.fraisrec), 0) as total FROM retrait r JOIN fraisrecep f ON r.montant BETWEEN f.montant1 AND f.montant2";
    
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
    
    // ✅ Générer automatiquement un idrecep
    private String genererIdRecep() {
        String nouveauCode = "RET-0001";

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(GENERER_ID_RETRAIT);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                String dernierCode = rs.getString("dernier");
                if (dernierCode != null && !dernierCode.isEmpty()) {
                    // Extraire le numéro après le tiret
                    String[] parts = dernierCode.split("-");
                    if (parts.length == 2) {
                        int numero = Integer.parseInt(parts[1]);
                        numero++;
                        nouveauCode = String.format("RET-%04d", numero);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nouveauCode;
    }
    
    public boolean ajouterRetrait(Retrait retrait) throws SQLException {
        String codeGenere = genererIdRecep();
        int fraisRetrait = getFraisRetrait(retrait.getMontant());
        int montantTotal = retrait.getMontant() + fraisRetrait;

        // Vérifier solde suffisant
        if (!verifierSoldeSuffisant(retrait.getNumTel(), montantTotal)) {
            throw new SQLException("Solde insuffisant pour effectuer ce retrait (montant: " + retrait.getMontant() + " + frais: " + fraisRetrait + ")");
        }

        // Démarrer transaction
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);

            // 1. Mettre à jour solde client
            boolean soldeMisAJour = mettreAJourSoldeClient(retrait.getNumTel(), montantTotal);
            if (!soldeMisAJour) {
                throw new SQLException("Erreur lors de la mise à jour du solde");
            }

            // 2. Insérer le retrait
            try (PreparedStatement preparedStatement = connection.prepareStatement(AJOUT_RETRAIT)) {
                preparedStatement.setString(1, codeGenere);
                preparedStatement.setString(2, retrait.getNumTel());
                preparedStatement.setInt(3, retrait.getMontant());
                preparedStatement.setDate(4, new java.sql.Date(retrait.getDateRecep().getTime()));
                preparedStatement.executeUpdate();
            }

            connection.commit();
            return true;

        } catch (SQLException e) {
            if (connection != null) {
                try { connection.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            printSQLException(e);
            throw e;
        } finally {
            if (connection != null) {
                try { connection.setAutoCommit(true); connection.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }
           
    // ✅ LISTER TOUS LES RETRAITS
    public List<Retrait> listeRetrait() {
        List<Retrait> retrait = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(LISTE_RETRAIT)) {
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {            
                String idrecep = rs.getString("idrecep");
                String numtel = rs.getString("numtel");
                Integer montant = rs.getInt("montant");
                Date daterecep = rs.getDate("daterecep");
                
                retrait.add(new Retrait(idrecep, numtel, montant, daterecep));
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return retrait;
    }
    
    // ✅ MODIFIER UN RETRAIT
    public boolean modifierRetrait(Retrait retrait) throws SQLException {
        boolean rowUpdated;
        try (Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(MISE_A_JOUR_RETRAIT)) {
            statement.setString(1, retrait.getNumTel());
            statement.setInt(2, retrait.getMontant());
            statement.setString(3, retrait.getIdRecep());
            rowUpdated = statement.executeUpdate() > 0;
        }
        return rowUpdated;
    }
    
    // ✅ SUPPRIMER UN RETRAIT
    public boolean supprimerRetrait(String idrecep) throws SQLException {
        boolean rowDeleted;
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(SUPPRIMER_RETRAIT)) {
            statement.setString(1, idrecep);
            rowDeleted = statement.executeUpdate() > 0;
        }
        return rowDeleted;
    }

         // Recherche d'opération à une date (sujet)
    public List<Retrait> rechercherParDateRecep(Date dateRecepRecherche) throws SQLException {
        List<Retrait> retraits = new ArrayList<>();
        
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(RECHERCHE_PAR_DATERECEP)) {
            
            statement.setDate(1, new java.sql.Date(dateRecepRecherche.getTime()));
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {            
                String idrecep = rs.getString("idrecep");
                String numtel = rs.getString("numtel");
                Integer montant = rs.getInt("montant");
                Date daterecep = rs.getDate("daterecep");
                
                retraits.add(new Retrait(idrecep, numtel, montant, daterecep));
            }
        }
        return retraits;
    }
    // Récupérer le frais de retrait selon le montant
    private int getFraisRetrait(int montant) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(RECHERCHER_FRAIS_RETRAIT_PAR_MONTANT)) {
            stmt.setInt(1, montant);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("fraisrec");
            }
        }
        return 0;
    }
    
    // Vérifier si solde suffisant (montant + frais)
    private boolean verifierSoldeSuffisant(String numtel, int montantTotal) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(VERIFIER_SOLDE_SUFFISANT)) {
            stmt.setString(1, numtel);
            stmt.setInt(2, montantTotal);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }
    
    // Mettre à jour le solde du client (soustraction)
    private boolean mettreAJourSoldeClient(String numtel, int montantTotal) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(METTRE_A_JOUR_SOLDE_CLIENT_RETRAIT)) {
            stmt.setInt(1, montantTotal);
            stmt.setString(2, numtel);
            return stmt.executeUpdate() > 0;
        }
    }
    
    public int getSommeFraisRetraitReels() throws SQLException {
        int total = 0;
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(SOMME_FRAIS_RETRAIT_REELS);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                total = rs.getInt("total");
            }
        }
        return total;
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
