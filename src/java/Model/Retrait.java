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
public class Retrait {
    private String idrecep;
    private String numtel;
    private Integer montant;
    private Date daterecep;
    
    // Constructeur complet (ID saisi manuellement)
    public Retrait(String idrecep, String numtel, Integer montant, Date daterecep){
        this.idrecep = idrecep;
        this.numtel = numtel;
        this.montant = montant;
        this.daterecep = daterecep;
    }
    
    // Constructeur vide (utile pour les formulaires ou DAO)
    public Retrait(){}
    
    // Constructeur sans ID (tu peux l’utiliser si tu veux que l’ID soit ajouté plus tard)
    public Retrait(String numtel, Integer montant, Date daterecep){
        this.numtel = numtel;
        this.montant = montant;
        this.daterecep = daterecep;
    }
    
    // Getters et Setters
    // idrecep
    public String getIdRecep() {
        return idrecep;
    }

    public void setIdEnvoyer(String idrecep) {
        this.idrecep = idrecep;
    }
    
    // numtel
    public String getNumTel() {
        return numtel;
    }

    public void setNumTel(String numtel) {
        this.numtel = numtel;
    }   
    
    // montant
    public Integer getMontant() {
        return montant;
    }

    public void setMontant(Integer montant) {
        this.montant = montant;
    }
    
    // daterecep
    public Date getDateRecep() {
        return daterecep;
    }

    public void setDateRecep(Date daterecep) {
        this.daterecep = daterecep;
    }  
}
