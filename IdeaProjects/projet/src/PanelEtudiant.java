import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class PanelEtudiant extends JPanel {

    private EtudiantDAOImpl dao;
    private DefaultTableModel tableModel;
    private JTable table;

    private JTextField tfNom, tfPrenom, tfFiliere, tfEmail;

    public PanelEtudiant(EtudiantDAOImpl dao) {
        this.dao = dao;
        setLayout(new BorderLayout(10, 10));

        // === FORMULAIRE (haut) ===
        JPanel form = new JPanel(new GridLayout(2, 4, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Informations"));

        tfNom     = new JTextField();
        tfPrenom  = new JTextField();
        tfFiliere = new JTextField();
        tfEmail   = new JTextField();

        form.add(new JLabel("Nom :"));       form.add(tfNom);
        form.add(new JLabel("Prénom :"));    form.add(tfPrenom);
        form.add(new JLabel("Filière :"));   form.add(tfFiliere);
        form.add(new JLabel("Email :"));     form.add(tfEmail);

        // === BOUTONS ===
        JPanel buttons = new JPanel();
        JButton btnAjouter    = new JButton("Ajouter");
        JButton btnModifier   = new JButton("Modifier");
        JButton btnSupprimer  = new JButton("Supprimer");
        JButton btnActualiser = new JButton("Actualiser");

        buttons.add(btnAjouter);
        buttons.add(btnModifier);
        buttons.add(btnSupprimer);
        buttons.add(btnActualiser);

        // === TABLEAU (centre) ===
        String[] cols = {"ID", "Nom", "Prénom", "Filière", "Email"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Remplir formulaire au clic sur une ligne
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                tfNom.setText((String) tableModel.getValueAt(row, 1));
                tfPrenom.setText((String) tableModel.getValueAt(row, 2));
                tfFiliere.setText((String) tableModel.getValueAt(row, 3));
                tfEmail.setText((String) tableModel.getValueAt(row, 4));
            }
        });

        // === ACTIONS ===
        btnAjouter.addActionListener(e -> {
            try {
                Etudiant et = new Etudiant(
                        tfNom.getText(), tfPrenom.getText(),
                        tfFiliere.getText(), tfEmail.getText(), null
                );
                dao.ajouter(et);
                chargerDonnees();
                viderFormulaire();
                JOptionPane.showMessageDialog(this, "Etudiant ajouté !");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
            }
        });

        btnModifier.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Sélectionnez une ligne");
                return;
            }
            try {
                int id = (int) tableModel.getValueAt(row, 0);
                Etudiant et = dao.trouverParId(id);
                et.setNom(tfNom.getText());
                et.setPrenom(tfPrenom.getText());
                et.setFiliere(tfFiliere.getText());
                et.setEmail(tfEmail.getText());
                dao.modifier(et);
                chargerDonnees();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
            }
        });

        btnSupprimer.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) return;
            int id = (int) tableModel.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Confirmer suppression ?");
            if (confirm == JOptionPane.YES_OPTION) {
                try { dao.supprimerLogique(id); chargerDonnees(); }
                catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage()); }
            }
        });

        btnActualiser.addActionListener(e -> chargerDonnees());

        // === ASSEMBLAGE ===
        JPanel top = new JPanel(new BorderLayout());
        top.add(form, BorderLayout.CENTER);
        top.add(buttons, BorderLayout.SOUTH);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        chargerDonnees();
    }

    private void chargerDonnees() {
        try {
            tableModel.setRowCount(0);
            for (Etudiant e : dao.listerTous()) {
                tableModel.addRow(new Object[]{
                        e.getId(), e.getNom(), e.getPrenom(),
                        e.getFiliere(), e.getEmail()
                });
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void viderFormulaire() {
        tfNom.setText(""); tfPrenom.setText("");
        tfFiliere.setText(""); tfEmail.setText("");
    }
}