public class Cours {
    private int id;
    private String intitule;
    private String semestre;
    private int capacite;
    private int nbInscrits;

    public Cours() {}

    public Cours(String intitule, String semestre,
                 int capacite) {
        this.intitule = intitule;
        this.semestre = semestre;
        this.capacite = capacite;
        this.nbInscrits = 0;
    }

    public boolean estComplet() {
        return nbInscrits >= capacite;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getIntitule() { return intitule; }
    public void setIntitule(String i) { this.intitule = i; }
    public String getSemestre() { return semestre; }
    public void setSemestre(String s) { this.semestre = s; }
    public int getCapacite() { return capacite; }
    public void setCapacite(int c) { this.capacite = c; }
    public int getNbInscrits() { return nbInscrits; }
    public void setNbInscrits(int n) { this.nbInscrits = n; }

    @Override
    public String toString() {
        return "[" + id + "] " + intitule
                + " | S:" + semestre
                + " | " + nbInscrits + "/" + capacite;
    }
}
