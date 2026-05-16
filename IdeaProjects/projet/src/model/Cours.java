package model;

public class Cours {

    private int    id;
    private String intitule;
    private String semestre;
    private int    capacite;
    private int    nbInscrits;

    public Cours() {}

    public Cours(String intitule, String semestre, int capacite) {
        this.intitule   = intitule;
        this.semestre   = semestre;
        this.capacite   = capacite;
        this.nbInscrits = 0;
    }

    public boolean estComplet() { return nbInscrits >= capacite; }

    public int    getId()              { return id; }
    public void   setId(int v)         { this.id = v; }
    public String getIntitule()        { return intitule; }
    public void   setIntitule(String v){ this.intitule = v; }
    public String getSemestre()        { return semestre; }
    public void   setSemestre(String v){ this.semestre = v; }
    public int    getCapacite()        { return capacite; }
    public void   setCapacite(int v)   { this.capacite = v; }
    public int    getNbInscrits()      { return nbInscrits; }
    public void   setNbInscrits(int v) { this.nbInscrits = v; }

    @Override
    public String toString() {
        return "[" + id + "] " + intitule
                + " | S:" + semestre
                + " | " + nbInscrits + "/" + capacite;
    }
}
