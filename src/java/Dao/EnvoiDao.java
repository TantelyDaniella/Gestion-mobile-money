/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

import Model.Envoi;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author Asus
 */
public class EnvoiDao {
    private String url = "jdbc:postgresql://localhost:5432/mobilemoney";
    private String user = "postgres";
    private String pwd = "nancy";
    
    // Requêtes SQL
    private static final String AJOUT_ENVOI = "INSERT INTO envoi (idenvoyer, numenvoyeur, numrecepteur, montant, date, payer_frais_retrait, raison) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String LISTE_ENVOI = "SELECT * FROM envoi ORDER BY idenvoyer ASC";
    private static final String MISE_A_JOUR_ENVOI = "UPDATE envoi SET numenvoyeur = ?, numrecepteur = ?, montant = ?, payer_frais_retrait = ?, raison = ? WHERE idenvoyer = ?";
    private static final String SUPPRIMER_ENVOI = "DELETE FROM envoi WHERE idenvoyer = ?";
    private static final String GENERER_ID_ENVOI = "SELECT MAX(idenvoyer) AS dernier FROM envoi";
    
    private static final String SQL_RECHERCHE_PAR_DATE = "SELECT * FROM envoi WHERE date = ? ORDER BY idenvoyer ASC";
    private static final String SQL_GET_EMAIL_CLIENT = "SELECT mail, nom FROM client WHERE numtel = ?";
    
    // Requêtes pour la mise à jour des soldes
    private static final String SQL_UPDATE_SOLDE_ENVOYEUR = "UPDATE client SET solde = solde - ? WHERE numtel = ?";
    private static final String SQL_UPDATE_SOLDE_RECEPTEUR = "UPDATE client SET solde = solde + ? WHERE numtel = ?";
    
    // Requêtes pour les frais
    private static final String SQL_GET_FRAIS_ENVOI = "SELECT fraisenv FROM fraisenvoi WHERE ? BETWEEN montant1 AND montant2";
    private static final String SQL_GET_FRAIS_RETRAIT = "SELECT fraisrec FROM fraisrecep WHERE ? BETWEEN montant1 AND montant2";
    
    private static final String SOMME_FRAIS_ENVOI_REELS = "SELECT COALESCE(SUM(f.fraisenv), 0) as total FROM envoi e JOIN fraisenvoi f ON e.montant BETWEEN f.montant1 AND f.montant2";
    
    // Connexion à Postgresql
    public Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url, user, pwd);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return connection;
    }
    
    // ✅ Générer automatiquement un idenvoyer
    private String genererIdEnvoyer() {
        String nouveauCode = "ENV-0001";

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(GENERER_ID_ENVOI);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                String dernierCode = rs.getString("dernier");
                if (dernierCode != null && !dernierCode.isEmpty()) {
                    // Extraire le numéro après le tiret
                    String[] parts = dernierCode.split("-");
                    if (parts.length == 2) {
                        int numero = Integer.parseInt(parts[1]);
                        numero++;
                        nouveauCode = String.format("ENV-%04d", numero);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nouveauCode;
    }
    
    // AJOUTER UN ENVOI
    public void ajouterEnvoi(Envoi envoi) throws SQLException {
        String codeGenere = genererIdEnvoyer();
        
        try (Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(AJOUT_ENVOI)) {
            
            preparedStatement.setString(1, codeGenere);        
            preparedStatement.setString(2, envoi.getNumEnvoyeur());
            preparedStatement.setString(3, envoi.getNumRecepteur());
            preparedStatement.setInt(4, envoi.getMontant());
            preparedStatement.setDate(5, new java.sql.Date(envoi.getDate().getTime()));
            preparedStatement.setBoolean(6, envoi.getPayer_Frais_Retrait());
            preparedStatement.setString(7, envoi.getRaison());
            
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            printSQLException(e);
        }
    }
           
    // ✅ LISTER TOUS LES ENVOIS
    public List<Envoi> listeEnvoi() {
        List<Envoi> envoi = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(LISTE_ENVOI)) {
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {            
                String idenvoyer = rs.getString("idenvoyer");
                String numenvoyeur = rs.getString("numenvoyeur");
                String numrecepteur = rs.getString("numrecepteur");
                Integer montant = rs.getInt("montant");
                Date date = rs.getDate("date");
                Boolean payer_frais_retrait = rs.getBoolean("payer_frais_retrait");
                String raison = rs.getString("raison");
                
                envoi.add(new Envoi(idenvoyer, numenvoyeur, numrecepteur, montant, date, payer_frais_retrait, raison));
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return envoi;
    }
    
    // ✅ MODIFIER UN ENVOI
    public boolean modifierEnvoi(Envoi envoi) throws SQLException {
        boolean rowUpdated;
        try (Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(MISE_A_JOUR_ENVOI)) {
            
            statement.setString(1, envoi.getNumEnvoyeur());        
            statement.setString(2, envoi.getNumRecepteur());
            statement.setInt(3, envoi.getMontant());
            statement.setBoolean(4, envoi.getPayer_Frais_Retrait());
            statement.setString(5, envoi.getRaison());
            statement.setString(6, envoi.getIdEnvoyer());
            
            rowUpdated = statement.executeUpdate() > 0;
        }
        return rowUpdated;
    }
    
    // ✅ SUPPRIMER UN ENVOI
    public boolean supprimerFraisEnvoi(String idenvoyer) throws SQLException {
        boolean rowDeleted;
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(SUPPRIMER_ENVOI)) {
            statement.setString(1, idenvoyer);
            rowDeleted = statement.executeUpdate() > 0;
        }
        return rowDeleted;
    }
    
     // Recherche d'opération à une date (sujet)
    public List<Envoi> rechercherParDate(Date dateRecherche) throws SQLException {
        List<Envoi> envois = new ArrayList<>();
        
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_RECHERCHE_PAR_DATE)) {
            
            statement.setDate(1, new java.sql.Date(dateRecherche.getTime()));
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {            
                String idenvoyer = rs.getString("idenvoyer");
                String numenvoyeur = rs.getString("numenvoyeur");
                String numrecepteur = rs.getString("numrecepteur");
                Integer montant = rs.getInt("montant");
                Date date = rs.getDate("date");
                Boolean payer_frais_retrait = rs.getBoolean("payer_frais_retrait");
                String raison = rs.getString("raison");
                
                envois.add(new Envoi(idenvoyer, numenvoyeur, numrecepteur, montant, date, payer_frais_retrait, raison));
            }
        }
        return envois;
    }
    
// Mettre à jour les soldes lors d'un envoi
public void mettreAJourSoldes(Envoi envoi, int fraisEnvoi, int fraisRetrait) throws SQLException {
    try (Connection connection = getConnection()) {
        connection.setAutoCommit(false);
        
        try {
            // Calcul de ce que paie l'envoyeur
            int deductionEnvoyeur = envoi.getMontant() + fraisEnvoi;
            
            // Si l'envoyeur paie aussi les frais de retrait du récepteur
            if (envoi.getPayer_Frais_Retrait()) {
                deductionEnvoyeur += fraisRetrait;
            }
            
            // 1. Déduire du solde de l'envoyeur
            try (PreparedStatement stmt = connection.prepareStatement(SQL_UPDATE_SOLDE_ENVOYEUR)) {
                stmt.setInt(1, deductionEnvoyeur);
                stmt.setString(2, envoi.getNumEnvoyeur());
                stmt.executeUpdate();
            }
            
            // 2. Ajouter le montant au solde du récepteur
            try (PreparedStatement stmt = connection.prepareStatement(SQL_UPDATE_SOLDE_RECEPTEUR)) {
                stmt.setInt(1, envoi.getMontant());
                stmt.setString(2, envoi.getNumRecepteur());
                stmt.executeUpdate();
            }
            
            connection.commit();
            
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }
}
    
    // Calculer le frais d'envoi selon le montant
    public int getFraisEnvoiForMontant(int montant) {
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(SQL_GET_FRAIS_ENVOI)) {
            
            stmt.setInt(1, montant);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("fraisenv");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    // Calculer le frais de retrait selon le montant
    public int getFraisRetraitForMontant(int montant) {
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(SQL_GET_FRAIS_RETRAIT)) {
            
            stmt.setInt(1, montant);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("fraisrec");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    // ==================== METHODES POUR NOTIFICATION PAR EMAIL ====================
    
// Message pour l'envoyeur
private String getMessageEnvoyeur(Envoi envoi, String nom, int fraisEnvoi, int fraisRetrait) {
    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
    
    int totalDebite = envoi.getMontant() + fraisEnvoi;
    
    StringBuilder message = new StringBuilder();
    message.append("Bonjour ").append(nom).append(",\n\n");
    message.append("Vous avez effectué un envoi d'argent de ").append(String.format("%,d", envoi.getMontant())).append(" Ar.\n");
    message.append("Frais d'envoi : ").append(String.format("%,d", fraisEnvoi)).append(" Ar\n");
    
    // Si l'envoyeur a payé les frais de retrait
    if (envoi.getPayer_Frais_Retrait()) {
        message.append("Frais de retrait (payés par vous) : ").append(String.format("%,d", fraisRetrait)).append(" Ar\n");
        totalDebite += fraisRetrait;
        message.append("Total débité de votre compte : ").append(String.format("%,d", totalDebite)).append(" Ar\n\n");
    } else {
        message.append("Total débité de votre compte : ").append(String.format("%,d", totalDebite)).append(" Ar\n");
        message.append("Les frais de retrait seront déduits du compte du récepteur.\n\n");
    }
    
    message.append("Destinataire : ").append(envoi.getNumRecepteur()).append("\n");
    message.append("Raison : ").append(envoi.getRaison() != null ? envoi.getRaison() : "Non spécifiée").append("\n");
    message.append("Date : ").append(sdf.format(envoi.getDate())).append("\n\n");
    message.append("Merci de votre confiance.\n");
    message.append("Mobile Money");
    
    return message.toString();
}

// Message pour le récepteur
private String getMessageRecepteur(Envoi envoi, String nom, int fraisRetrait) {
    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
    
    StringBuilder message = new StringBuilder();
    message.append("Bonjour ").append(nom).append(",\n\n");
    message.append("Vous avez reçu un virement de ").append(String.format("%,d", envoi.getMontant())).append(" Ar.\n");
    message.append("Expéditeur : ").append(envoi.getNumEnvoyeur()).append("\n");
    message.append("Raison : ").append(envoi.getRaison() != null ? envoi.getRaison() : "Non spécifiée").append("\n");
    message.append("Date : ").append(sdf.format(envoi.getDate())).append("\n\n");
    
    // Si l'envoyeur a payé les frais de retrait
    if (envoi.getPayer_Frais_Retrait()) {
        int montantTotalRecu = envoi.getMontant() + fraisRetrait;
        message.append("✅ L'expéditeur a payé vos frais de retrait.\n");
        message.append("Montant à recevoir : ").append(String.format("%,d", envoi.getMontant())).append(" Ar\n");
        message.append("Frais de retrait (payés par l'expéditeur) : ").append(String.format("%,d", fraisRetrait)).append(" Ar\n");
        message.append("Montant total crédité sur votre compte : ").append(String.format("%,d", montantTotalRecu)).append(" Ar\n\n");
    } else {
        message.append("⚠️ Les frais de retrait seront déduits lors de votre retrait.\n");
        message.append("Montant à recevoir : ").append(String.format("%,d", envoi.getMontant())).append(" Ar\n");
        message.append("Frais de retrait à payer : ").append(String.format("%,d", fraisRetrait)).append(" Ar\n");
        message.append("Montant net après retrait : ").append(String.format("%,d", envoi.getMontant() - fraisRetrait)).append(" Ar\n\n");
    }
    
    message.append("Merci de votre confiance.\n");
    message.append("Mobile Money");
    
    return message.toString();
}

// Notifier par mail l'envoyeur et le récepteur
public void notifierParMail(Envoi envoi, int fraisEnvoi, int fraisRetrait) throws SQLException {
    
    String emailEnvoyeur = null;
    String emailRecepteur = null;
    String nomEnvoyeur = null;
    String nomRecepteur = null;
    
    try (Connection connection = getConnection()) {
        // Récupérer email et nom de l'envoyeur
        try (PreparedStatement stmt = connection.prepareStatement(SQL_GET_EMAIL_CLIENT)) {
            stmt.setString(1, envoi.getNumEnvoyeur());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                emailEnvoyeur = rs.getString("mail");
                nomEnvoyeur = rs.getString("nom");
            }
        }
        
        // Récupérer email et nom du récepteur
        try (PreparedStatement stmt = connection.prepareStatement(SQL_GET_EMAIL_CLIENT)) {
            stmt.setString(1, envoi.getNumRecepteur());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                emailRecepteur = rs.getString("mail");
                nomRecepteur = rs.getString("nom");
            }
        }
    }
    
    if (emailEnvoyeur != null && !emailEnvoyeur.isEmpty()) {
        envoyerEmail(emailEnvoyeur, getMessageEnvoyeur(envoi, nomEnvoyeur, fraisEnvoi, fraisRetrait));
    }
    
    if (emailRecepteur != null && !emailRecepteur.isEmpty()) {
        envoyerEmail(emailRecepteur, getMessageRecepteur(envoi, nomRecepteur, fraisRetrait));
    }
}
    
    // Envoyer un email
    private void envoyerEmail(String destinataire, String message) {
        String host = "smtp.gmail.com";
        String port = "587";
        String username = "miosatsuki968@gmail.com";
        String password = "yblr pzng obev vwsj";
        
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        
        try {
            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(username));
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(destinataire));
            mimeMessage.setSubject("Notification d'envoi d'argent - Mobile Money");
            mimeMessage.setText(message);
            
            Transport.send(mimeMessage);
            
        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'envoi de l'email à " + destinataire);
        }
    }
    
    public int getSommeFraisEnvoiReels() throws SQLException {
        int total = 0;
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(SOMME_FRAIS_ENVOI_REELS);
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
