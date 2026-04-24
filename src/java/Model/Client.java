/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author Asus
 */
public class Client {
    private String numtel;
    private String nom;
    private String sexe;
    private Integer age;
    private Integer solde;
    private String mail;
    
    // Constructeur complet (ID saisi manuellement)
    public Client(String numtel, String nom, String sexe, Integer age, Integer solde, String mail){
        this.numtel = numtel;
        this.nom = nom;
        this.sexe = sexe;
        this.age = age;
        this.solde = solde;
        this.mail = mail;
    }
    
    // Constructeur vide (utile pour les formulaires ou DAO)
    public Client(){}
    
    // Constructeur sans ID (tu peux l’utiliser si tu veux que l’ID soit ajouté plus tard)
    public Client(String nom, String sexe, Integer age, Integer solde, String mail){
        this.nom = nom;
        this.sexe = sexe;
        this.age = age;
        this.solde = solde;
        this.mail = mail;
    }
    
    // Getters et Setters
    // numtel
    public String getNumTel() {
        return numtel;
    }

    public void setNumTel(String numtel) {
        this.numtel = numtel;
    }
    
    // nom
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }
    
    // sexe
    public String getSexe() {
        return sexe;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
    }
    
    // age
    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
    
    // solde
    public Integer getSolde() {
        return solde;
    }

    public void setSolde(Integer solde) {
        this.solde = solde;
    }
    
   // mail
    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
}
