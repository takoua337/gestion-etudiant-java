package ui;

import dao.CoursDAOImpl;
import dao.DatabaseConnection;
import dao.EtudiantDAOImpl;
import dao.NoteDAOImpl;
import service.BulletinService;
import service.DashboardService;
import javax.swing.*;
import java.awt.*;

public class MainGUI {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                EtudiantDAOImpl  etudiantDAO      = new EtudiantDAOImpl();
                NoteDAOImpl      noteDAO          = new NoteDAOImpl();
                CoursDAOImpl     coursDAO         = new CoursDAOImpl();
                BulletinService  bulletinService  = new BulletinService(etudiantDAO, noteDAO);
                DashboardService dashboardService = new DashboardService();

                JFrame fenetre = new JFrame("Gestion Étudiants — TEK-UP");
                fenetre.setSize(1200, 700);
                fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                fenetre.setLocationRelativeTo(null);

                JTabbedPane tabs = new JTabbedPane();
                tabs.setFont(new Font("SansSerif", Font.BOLD, 13));
                tabs.addTab("Dashboard", new PanelDashboard(dashboardService));
                tabs.addTab("Étudiants", new PanelEtudiant(etudiantDAO));
                tabs.addTab("Cours",     new PanelCours(coursDAO));
                tabs.addTab("Notes",     new PanelNote(noteDAO, etudiantDAO, coursDAO));
                tabs.addTab("Bulletin",  new PanelBulletin(etudiantDAO, noteDAO));

                fenetre.add(tabs);
                fenetre.setVisible(true);

                fenetre.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        DatabaseConnection.close();
                    }
                });

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "Erreur de démarrage : " + e.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}
