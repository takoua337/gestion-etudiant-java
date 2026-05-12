import javax.swing.*;

public class MainGUI {

    static EtudiantDAOImpl etudiantDAO;
    static NoteDAOImpl     noteDAO;
    static BulletinService bulletinService;

    public static void main(String[] args) throws Exception {
        etudiantDAO     = new EtudiantDAOImpl();
        noteDAO         = new NoteDAOImpl();
        bulletinService = new BulletinService(etudiantDAO, noteDAO);

        SwingUtilities.invokeLater(() -> {
            JFrame fenetre = new JFrame("Gestion Etudiants");
            fenetre.setSize(900, 600);
            fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            fenetre.setLocationRelativeTo(null);

            JTabbedPane tabs = new JTabbedPane();
            tabs.addTab("Etudiants", new PanelEtudiant(etudiantDAO));
            tabs.addTab("Notes",     new PanelNote(noteDAO));
            tabs.addTab("Bulletin",  new PanelBulletin(etudiantDAO, noteDAO));

            fenetre.add(tabs);
            fenetre.setVisible(true);

            fenetre.addWindowListener(new java.awt.event.WindowAdapter() {
                public void windowClosing(java.awt.event.WindowEvent e) {
                    DatabaseConnection.close();
                }
            });
        });
    }
}