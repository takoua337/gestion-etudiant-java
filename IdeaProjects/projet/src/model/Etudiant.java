package model;

import java.sql.Date;

public class Etudiant {

    private int    id;
    private String nom;
    private String prenom;
    private String filiere;
    private String email;
    private Date   dateNaissance;
    private boolean actif;

    public Etudiant() { this.actif = true; }

    public Etudiant(String nom, String prenom,
                    String filiere, String email,
                    Date dateNaissance) {
        this.nom           = nom;
        this.prenom        = prenom;
        this.filiere       = filiere;
        this.email         = email;
        this.dateNaissance = dateNaissance;
        this.actif         = true;
    }

    public int     getId()            { return id; }
    public void    setId(int id)      { this.id = id; }
    public String  getNom()           { return nom; }
    public void    setNom(String v)   { this.nom = v; }
    public String  getPrenom()        { return prenom; }
    public void    setPrenom(String v){ this.prenom = v; }
    public String  getFiliere()       { return filiere; }
    public void    setFiliere(String v){ this.filiere = v; }
    public String  getEmail()         { return email; }
    public void    setEmail(String v) { this.email = v; }
    public Date    getDateNaissance()           { return dateNaissance; }
    public void    setDateNaissance(Date v)     { this.dateNaissance = v; }
    public boolean isActif()          { return actif; }
    public void    setActif(boolean v){ this.actif = v; }

    @Override
    public String toString() {
        return "[" + id + "] " + prenom + " " + nom
                + " | " + filiere + " | " + email;
    }
}
