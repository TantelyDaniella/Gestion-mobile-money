/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author Asus
 */
public class FraisEnvoi {
    private String idenv;
    private Integer montant1;
    private Integer montant2;
    private Integer fraisenv;
    
    // Constructeur complet (ID saisi manuellement)
    public FraisEnvoi(String idenv, Integer montant1, Integer montant2, Integer fraisenv){
        this.idenv = idenv;
        this.montant1 = montant1;
        this.montant2 = montant2;
        this.fraisenv = fraisenv;
    }
    
    // Constructeur vide (utile pour les formulaires ou DAO)
    public FraisEnvoi(){}
    
    // Constructeur sans ID (tu peux l’utiliser si tu veux que l’ID soit ajouté plus tard)
    public FraisEnvoi(Integer montant1, Integer montant2, Integer fraisenv){
        this.montant1 = montant1;
        this.montant2 = montant2;
        this.fraisenv = fraisenv;
    }
    
    // Getters et Setters
    // idenv
    public String getIdEnv() {
        return idenv;
    }

    public void setIdEnv(String idenv) {
        this.idenv = idenv;
    }
    
    // montant1
    public Integer getMontant1() {
        return montant1;
    }

    public void setMontant1(Integer montant1) {
        this.montant1 = montant1;
    }
    
    // montant2
    public Integer getMontant2() {
        return montant2;
    }

    public void setMontant2(Integer montant2) {
        this.montant2 = montant2;
    }
    
    // fraisenv
    public Integer getFraisEnv() {
        return fraisenv;
    }

    public void setFraisEnv(Integer fraisenv) {
        this.fraisenv = fraisenv;
    }
    
}
