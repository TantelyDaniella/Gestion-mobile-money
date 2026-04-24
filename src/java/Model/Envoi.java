/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.util.Date;

/**
 *
 * @author Asus
 */
public class Envoi {
    private String idenvoyer;
    private String numenvoyeur;
    private String numrecepteur;
    private Integer montant;
    private Date date;
    private Boolean payer_frais_retrait;
    private String raison;
    
    // Constructeur complet (ID saisi manuellement)
    public Envoi(String idenvoyer, String numenvoyeur, String numrecepteur, Integer montant, Date date, Boolean payer_frais_retrait, String raison){
        this.idenvoyer = idenvoyer;
        this.numenvoyeur = numenvoyeur;
        this.numrecepteur = numrecepteur;
        this.montant = montant;
        this.date = date;
        this.payer_frais_retrait = payer_frais_retrait;
        this.raison = raison;
    }
    
    // Constructeur vide (utile pour les formulaires ou DAO)
    public Envoi(){}
    
    // Constructeur sans ID (tu peux l’utiliser si tu veux que l’ID soit ajouté plus tard)
   public Envoi(String numenvoyeur, String numrecepteur, Integer montant, Date date, Boolean payer_frais_retrait, String raison){
        this.numenvoyeur = numenvoyeur;
        this.numrecepteur = numrecepteur;
        this.montant = montant;
        this.date = date;
        this.payer_frais_retrait = payer_frais_retrait;
        this.raison = raison;
    }
    
    // Getters et Setters
    // idenvoyer
    public String getIdEnvoyer() {
        return idenvoyer;
    }

    public void setIdEnvoyer(String idenvoyer) {
        this.idenvoyer = idenvoyer;
    }
    
    // numenvoyeur
    public String getNumEnvoyeur() {
        return numenvoyeur;
    }

    public void setNumEnvoyeur(String numenvoyeur) {
        this.numenvoyeur = numenvoyeur;
    }
    
    // numrecepteur
    public String getNumRecepteur() {
        return numrecepteur;
    }

    public void setNumRecepteur(String numrecepteur) {
        this.numrecepteur = numrecepteur;
    }
    
    // montant
    public Integer getMontant() {
        return montant;
    }

    public void setMontant(Integer montant) {
        this.montant = montant;
    }
    
    // date
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    
   // payer_frais_retrait
    public Boolean getPayer_Frais_Retrait() {
        return payer_frais_retrait;
    }

    public void setPayer_Frais_Retrait(Boolean payer_frais_retrait) {
        this.payer_frais_retrait = payer_frais_retrait;
    } 
    
    // raison
    public String getRaison() {
        return raison;
    }

    public void setRaison(String raison) {
        this.raison = raison;
    }
}
