import java.sql.Date;

public class Etudiant {

    private int id;
    private String nom;
    private String prenom;
    private String filiere;
    private String email;
    private Date dateNaissance;
    private boolean actif;

    public Etudiant() {
        this.actif = true;
    }

    public Etudiant(String nom, String prenom,
                    String filiere, String email,
                    Date dateNaissance) {
        this.nom = nom;
        this.prenom = prenom;
        this.filiere = filiere;
        this.email = email;
        this.dateNaissance = dateNaissance;
        this.actif = true;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getFiliere() { return filiere; }
    public void setFiliere(String filiere) { this.filiere = filiere; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Date getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(Date dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }

    @Override
    public String toString() {
        return "[" + id + "] " + prenom + " " + nom +
                " | " + filiere + " | " + email;
    }
}