package model;

public class Note {

    private int    id;
    private double valeur;
    private int    etudiantId;
    private int    coursId;
    private String dateSaisie;

    public Note() {}

    public Note(double valeur, int etudiantId,
                int coursId, String dateSaisie) {
        this.valeur      = valeur;
        this.etudiantId  = etudiantId;
        this.coursId     = coursId;
        this.dateSaisie  = dateSaisie;
    }

    public int    getId()                  { return id; }
    public void   setId(int id)            { this.id = id; }
    public double getValeur()              { return valeur; }
    public void   setValeur(double v)      { this.valeur = v; }
    public int    getEtudiantId()          { return etudiantId; }
    public void   setEtudiantId(int v)     { this.etudiantId = v; }
    public int    getCoursId()             { return coursId; }
    public void   setCoursId(int v)        { this.coursId = v; }
    public String getDateSaisie()          { return dateSaisie; }
    public void   setDateSaisie(String v)  { this.dateSaisie = v; }

    @Override
    public String toString() {
        return "[" + id + "] Note=" + valeur
                + " | Etudiant#" + etudiantId
                + " | Cours#" + coursId;
    }
}
