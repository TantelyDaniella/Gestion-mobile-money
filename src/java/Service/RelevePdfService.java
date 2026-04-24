package Service;

import Model.Client;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RelevePdfService {
    
    private static final String URL = "jdbc:postgresql://localhost:5432/mobilemoney";
    private static final String USER = "postgres";
    private static final String PWD = "nancy";
    
    private static final String SQL_ENVOIS_ENVOYEUR = 
        "SELECT idenvoyer, numrecepteur, montant, date, raison FROM envoi " +
        "WHERE numenvoyeur = ? AND EXTRACT(MONTH FROM date) = ? AND EXTRACT(YEAR FROM date) = ?";
    
    private static final String SQL_ENVOIS_RECEPTEUR = 
        "SELECT idenvoyer, numenvoyeur, montant, date, raison FROM envoi " +
        "WHERE numrecepteur = ? AND EXTRACT(MONTH FROM date) = ? AND EXTRACT(YEAR FROM date) = ?";
    
    private static final String SQL_RETRAITS = 
        "SELECT daterecep, montant FROM retrait " +
        "WHERE numtel = ? AND EXTRACT(MONTH FROM daterecep) = ? AND EXTRACT(YEAR FROM daterecep) = ?";
    
    private static final String[] NOMS_MOIS = {
        "Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
        "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"
    };
    
    public void genererReleve(HttpServletResponse response, Client client, int mois, int annee) 
            throws SQLException, IOException, DocumentException {
        
        java.util.List<Map<String, Object>> envoisEnvoyeur = getEnvoisEnvoyeur(client.getNumTel(), mois, annee);
        java.util.List<Map<String, Object>> envoisRecepteur = getEnvoisRecepteur(client.getNumTel(), mois, annee);
        java.util.List<Map<String, Object>> retraits = getRetraits(client.getNumTel(), mois, annee);
        
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", 
            "attachment; filename=\"releve_" + client.getNumTel() + "_" + mois + "_" + annee + ".pdf\"");
        
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();
        
        // Date centrée
        addDate(document, mois, annee);
        
        // Infos client (sans espace supplémentaire)
        addClientInfo(document, client);
        
        // Tableau des opérations
        addOperationsTable(document, envoisEnvoyeur, envoisRecepteur, retraits);
        
        // Totaux
        addTotals(document, envoisEnvoyeur, envoisRecepteur, retraits);
        
        document.close();
    }
    
    private Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(URL, USER, PWD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver PostgreSQL non trouvé", e);
        }
    }
    
    private java.util.List<Map<String, Object>> getEnvoisEnvoyeur(String numtel, int mois, int annee) throws SQLException {
        java.util.List<Map<String, Object>> resultats = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_ENVOIS_ENVOYEUR)) {
            stmt.setString(1, numtel);
            stmt.setInt(2, mois);
            stmt.setInt(3, annee);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("date", rs.getDate("date"));
                row.put("montant", rs.getInt("montant"));
                row.put("raison", rs.getString("raison") != null ? rs.getString("raison") : "");
                row.put("type", "debit");
                resultats.add(row);
            }
        }
        return resultats;
    }
    
    private java.util.List<Map<String, Object>> getEnvoisRecepteur(String numtel, int mois, int annee) throws SQLException {
        java.util.List<Map<String, Object>> resultats = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_ENVOIS_RECEPTEUR)) {
            stmt.setString(1, numtel);
            stmt.setInt(2, mois);
            stmt.setInt(3, annee);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("date", rs.getDate("date"));
                row.put("montant", rs.getInt("montant"));
                row.put("raison", rs.getString("raison") != null ? rs.getString("raison") : "");
                row.put("type", "credit");
                resultats.add(row);
            }
        }
        return resultats;
    }
    
    private java.util.List<Map<String, Object>> getRetraits(String numtel, int mois, int annee) throws SQLException {
        java.util.List<Map<String, Object>> resultats = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_RETRAITS)) {
            stmt.setString(1, numtel);
            stmt.setInt(2, mois);
            stmt.setInt(3, annee);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("date", rs.getDate("daterecep"));
                row.put("montant", rs.getInt("montant"));
                row.put("raison", "Retrait");
                row.put("type", "debit");
                resultats.add(row);
            }
        }
        return resultats;
    }
    
    private void addDate(Document document, int mois, int annee) throws DocumentException {
        Font dateFont = new Font(Font.HELVETICA, 11, Font.NORMAL);
        Paragraph datePara = new Paragraph("Date : " + getNomMois(mois) + " " + annee, dateFont);
        datePara.setAlignment(Element.ALIGN_CENTER);
        datePara.setSpacingAfter(15);
        document.add(datePara);
    }
    
    private void addClientInfo(Document document, Client client) throws DocumentException {
        Font normalFont = new Font(Font.HELVETICA, 11, Font.NORMAL);
        Font boldFont = new Font(Font.HELVETICA, 11, Font.BOLD);
        
        // Contact
        Paragraph contact = new Paragraph("Contact : " + client.getNumTel(), normalFont);
        contact.setSpacingAfter(0);
        document.add(contact);
        
        // Nom
        Paragraph nom = new Paragraph(client.getNom(), boldFont);
        nom.setSpacingAfter(0);
        document.add(nom);
        
        // Âge
        if (client.getAge() != null) {
            Paragraph age = new Paragraph(client.getAge() + " ans", normalFont);
            age.setSpacingAfter(0);
            document.add(age);
        }
        
        // Sexe
        if (client.getSexe() != null && !client.getSexe().isEmpty()) {
            Paragraph sexe = new Paragraph(client.getSexe(), normalFont);
            sexe.setSpacingAfter(0);
            document.add(sexe);
        }
        
        // Solde
        Paragraph solde = new Paragraph("Solde actuel : " + String.format("%,d", client.getSolde()) + " Ariary", normalFont);
        solde.setSpacingAfter(12);
        document.add(solde);
    }
    
    private void addOperationsTable(Document document,
                                     java.util.List<Map<String, Object>> envoisEnvoyeur,
                                     java.util.List<Map<String, Object>> envoisRecepteur,
                                     java.util.List<Map<String, Object>> retraits) throws DocumentException {
        
        Font headerFont = new Font(Font.HELVETICA, 11, Font.BOLD);
        Font normalFont = new Font(Font.HELVETICA, 10, Font.NORMAL);
        
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{20, 45, 17, 18});
        
        // En-têtes (sans couleur de fond)
        String[] headers = {"Date", "Raison", "Débit", "Crédit"};
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
            cell.setPadding(6);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        
        // Créer une liste combinée de toutes les opérations
        java.util.List<Map<String, Object>> toutesOperations = new ArrayList<>();
        for (Map<String, Object> e : envoisEnvoyeur) {
            toutesOperations.add(e);
        }
        for (Map<String, Object> r : retraits) {
            toutesOperations.add(r);
        }
        for (Map<String, Object> e : envoisRecepteur) {
            toutesOperations.add(e);
        }
        
        // Trier par date
        toutesOperations.sort((o1, o2) -> {
            Date d1 = (Date) o1.get("date");
            Date d2 = (Date) o2.get("date");
            return d1.compareTo(d2);
        });
        
        // Ajouter les lignes
        for (Map<String, Object> op : toutesOperations) {
            String date = sdf.format((Date) op.get("date"));
            String raison = (String) op.get("raison");
            String type = (String) op.get("type");
            int montant = (int) op.get("montant");
            
            PdfPCell dateCell = new PdfPCell(new Phrase(date, normalFont));
            dateCell.setPadding(4);
            table.addCell(dateCell);
            
            PdfPCell raisonCell = new PdfPCell(new Phrase(raison, normalFont));
            raisonCell.setPadding(4);
            table.addCell(raisonCell);
            
            if (type.equals("debit")) {
                PdfPCell debitCell = new PdfPCell(new Phrase(String.format("%,d", montant), normalFont));
                debitCell.setPadding(4);
                debitCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(debitCell);
                
                PdfPCell creditCell = new PdfPCell(new Phrase("", normalFont));
                creditCell.setPadding(4);
                table.addCell(creditCell);
            } else {
                PdfPCell debitCell = new PdfPCell(new Phrase("", normalFont));
                debitCell.setPadding(4);
                table.addCell(debitCell);
                
                PdfPCell creditCell = new PdfPCell(new Phrase(String.format("%,d", montant), normalFont));
                creditCell.setPadding(4);
                creditCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(creditCell);
            }
        }
        
        document.add(table);
    }
    
    private void addTotals(Document document,
                           java.util.List<Map<String, Object>> envoisEnvoyeur,
                           java.util.List<Map<String, Object>> envoisRecepteur,
                           java.util.List<Map<String, Object>> retraits) throws DocumentException {
        
        Font normalFont = new Font(Font.HELVETICA, 11, Font.NORMAL);
        
        int totalDebit = 0;
        for (Map<String, Object> e : envoisEnvoyeur) totalDebit += (int) e.get("montant");
        for (Map<String, Object> r : retraits) totalDebit += (int) r.get("montant");
        
        int totalCredit = 0;
        for (Map<String, Object> e : envoisRecepteur) totalCredit += (int) e.get("montant");
        
        document.add(new Paragraph(" "));
        
        Paragraph totalDebitPara = new Paragraph("Total Débit : " + String.format("%,d", totalDebit) + " Ar", normalFont);
        totalDebitPara.setSpacingAfter(2);
        document.add(totalDebitPara);
        
        Paragraph totalCreditPara = new Paragraph("Total Crédit : " + String.format("%,d", totalCredit) + " Ar", normalFont);
        document.add(totalCreditPara);
    }
    
    private String getNomMois(int mois) {
        if (mois >= 1 && mois <= 12) return NOMS_MOIS[mois - 1];
        return "";
    }
}