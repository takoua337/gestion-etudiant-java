import java.util.Scanner;

public class Main {

    static Scanner sc = new Scanner(System.in);
    static EtudiantDAOImpl etudiantDAO;
    static NoteDAOImpl noteDAO;
    static BulletinService bulletinService;

    public static void main(String[] args) throws Exception {
        etudiantDAO = new EtudiantDAOImpl();
        noteDAO     = new NoteDAOImpl();
        bulletinService = new BulletinService();

        int choix;
        do {
            System.out.println("\n=== MENU ===");
            System.out.println("1. Ajouter etudiant");
            System.out.println("2. Lister etudiants");
            System.out.println("3. Modifier etudiant");
            System.out.println("4. Supprimer etudiant");
            System.out.println("5. Ajouter note");
            System.out.println("6. Afficher bulletin");
            System.out.println("0. Quitter");
            System.out.print("Choix : ");
            choix = sc.nextInt();
            sc.nextLine();

            switch (choix) {
                case 1 -> ajouterEtudiant();
                case 2 -> listerEtudiants();
                case 3 -> modifierEtudiant();
                case 4 -> supprimerEtudiant();
                case 5 -> ajouterNote();
                case 6 -> afficherBulletin();
            }

        } while (choix != 0);

        DatabaseConnection.close();
        System.out.println("Au revoir !");
    }

    static void ajouterEtudiant() throws Exception {
        System.out.print("Nom : ");
        String nom = sc.nextLine();

        System.out.print("Prenom : ");
        String prenom = sc.nextLine();

        System.out.print("Filiere : ");
        String filiere = sc.nextLine();

        System.out.print("Email : ");
        String email = sc.nextLine();

        Etudiant e = new Etudiant(nom, prenom, filiere, email, null);

        etudiantDAO.ajouter(e);

        System.out.println("Etudiant ajoute (id=" + e.getId() + ")");
    }

    static void listerEtudiants() throws Exception {
        for (Etudiant e : etudiantDAO.listerTous()) {
            System.out.println(e);
        }
    }

    static void modifierEtudiant() throws Exception {

        System.out.print("ID etudiant : ");
        int id = sc.nextInt();
        sc.nextLine();

        Etudiant e = etudiantDAO.trouverParId(id);

        if (e == null) {
            System.out.println("Introuvable.");
            return;
        }

        System.out.print("Nouveau nom [" + e.getNom() + "] : ");

        String n = sc.nextLine();

        if (!n.isBlank()) {
            e.setNom(n);
        }

        etudiantDAO.modifier(e);

        System.out.println("Modifie.");
    }

    static void supprimerEtudiant() throws Exception {

        System.out.print("ID : ");
        int id = sc.nextInt();
        sc.nextLine();

        etudiantDAO.supprimerLogique(id);

        System.out.println("Desactive (soft delete).");
    }

    static void ajouterNote() throws Exception {

        System.out.print("ID etudiant : ");
        int eid = sc.nextInt();

        System.out.print("ID cours : ");
        int cid = sc.nextInt();

        System.out.print("Valeur /20 : ");
        double v = sc.nextDouble();
        sc.nextLine();

        Note n = new Note(
                v,
                eid,
                cid,
                java.time.LocalDate.now().toString()
        );

        noteDAO.ajouter(n);

        System.out.println("Note ajoutee (id=" + n.getId() + ")");
    }

    static void afficherBulletin() throws Exception {

        System.out.print("ID etudiant : ");
        int id = sc.nextInt();
        sc.nextLine();

        bulletinService.afficherBulletin(id);
    }

}