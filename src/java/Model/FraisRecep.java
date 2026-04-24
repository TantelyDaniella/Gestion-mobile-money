/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author Asus
 */
public class FraisRecep {
    private String idrec;
    private Integer montant1;
    private Integer montant2;
    private Integer fraisrec;
    
    // Constructeur complet (ID saisi manuellement)
    public FraisRecep(String idrec, Integer montant1, Integer montant2, Integer fraisrec){
        this.idrec = idrec;
        this.montant1 = montant1;
        this.montant2 = montant2;
        this.fraisrec = fraisrec;
    }
    
    // Constructeur vide (utile pour les formulaires ou DAO)
    public FraisRecep(){}
    
    // Constructeur sans ID (tu peux l’utiliser si tu veux que l’ID soit ajouté plus tard)
    public FraisRecep(Integer montant1, Integer montant2, Integer fraisrec){
        this.montant1 = montant1;
        this.montant2 = montant2;
        this.fraisrec = fraisrec;
    }
    
    // Getters et Setters
    // idrec
    public String getIdRec() {
        return idrec;
    }

    public void setIdRec(String idrec) {
        this.idrec = idrec;
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
    
    // fraisrec
    public Integer getFraisRec() {
        return fraisrec;
    }

    public void setFraisRec(Integer fraisrec) {
        this.fraisrec = fraisrec;
    }   
}
