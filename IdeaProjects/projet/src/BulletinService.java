import java.util.List;
public class BulletinService {

    private NoteDAOImpl noteDAO;
    private EtudiantDAOImpl etudiantDAO;

    public BulletinService() throws Exception {
        this.noteDAO = new NoteDAOImpl();
        this.etudiantDAO = new EtudiantDAOImpl();
    }

    public void afficherBulletin(int etudiantId)
            throws Exception {
        Etudiant e = etudiantDAO.trouverParId(etudiantId);
        if (e == null) {
            System.out.println("Etudiant introuvable.");
            return;
        }
        List<Note> notes =
                noteDAO.notesParEtudiant(etudiantId);
        double moy = noteDAO.moyenneEtudiant(etudiantId);
        String mention = calculerMention(moy);

        System.out.println("=== BULLETIN ===");
        System.out.println("Etudiant : "
                + e.getPrenom() + " " + e.getNom());
        System.out.println("Filiere  : " + e.getFiliere());
        System.out.println("Notes    : " + notes.size());
        for (Note n : notes)
            System.out.printf("  Cours#%d  =>  %.2f/20%n",
                    n.getCoursId(), n.getValeur());
        System.out.printf("Moyenne  : %.2f/20%n", moy);
        System.out.println("Mention  : " + mention);
    }

    private String calculerMention(double moy) {
        if (moy >= 16) return "Tres bien";
        if (moy >= 14) return "Bien";
        if (moy >= 12) return "Assez bien";
        if (moy >= 10) return "Passable";
        return "Insuffisant";
    }
}