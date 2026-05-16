package ui;

import dao.CoursDAOImpl;
import model.Cours;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class PanelCours extends JPanel {

    private final CoursDAOImpl dao;
    private DefaultTableModel  tableModel;
    private JTable             table;

    private JTextField tfIntitule, tfSemestre, tfCapacite;

    public PanelCours(CoursDAOImpl dao) {
        this.dao = dao;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ── Form ─────────────────────────────────────────────────────────────
        JPanel form = new JPanel(new GridLayout(2, 4, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Informations cours"));

        tfIntitule = new JTextField();
        tfSemestre = new JTextField();
        tfSemestre.setToolTipText("Ex : S1, S2, S3 …");
        tfCapacite = new JTextField();
        tfCapacite.setToolTipText("Capacité maximale (entier)");

        form.add(new JLabel("Intitulé *:"));  form.add(tfIntitule);
        form.add(new JLabel("Semestre *:"));  form.add(tfSemestre);
        form.add(new JLabel("Capacité *:"));  form.add(tfCapacite);
        form.add(new JLabel(""));             form.add(new JLabel(""));

        // ── Buttons ──────────────────────────────────────────────────────────
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        JButton btnAjouter    = new JButton("Ajouter");
        JButton btnModifier   = new JButton("Modifier");
        JButton btnSupprimer  = new JButton("Supprimer");
        JButton btnActualiser = new JButton("Actualiser");
        buttons.add(btnAjouter); buttons.add(btnModifier);
        buttons.add(btnSupprimer); buttons.add(btnActualiser);

        // ── Table ─────────────────────────────────────────────────────────────
        String[] cols = {"ID", "Intitulé", "Semestre", "Capacité", "Inscrits"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(26);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(229, 231, 235));

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                tfIntitule.setText(str(tableModel.getValueAt(row, 1)));
                tfSemestre.setText(str(tableModel.getValueAt(row, 2)));
                tfCapacite.setText(str(tableModel.getValueAt(row, 3)));
            }
        });

        JPanel top = new JPanel(new BorderLayout(0, 6));
        top.add(form, BorderLayout.NORTH);
        top.add(buttons, BorderLayout.SOUTH);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        btnAjouter  .addActionListener(e -> ajouterCours());
        btnModifier .addActionListener(e -> modifierCours());
        btnSupprimer.addActionListener(e -> supprimerCours());
        btnActualiser.addActionListener(e -> chargerDonnees());
        chargerDonnees();
    }

    private void ajouterCours() {
        try {
            Cours c = lireFormulaire(-1);
            dao.ajouter(c); chargerDonnees(); vider();
            JOptionPane.showMessageDialog(this, "Cours ajouté !");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
        }
    }

    private void modifierCours() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Sélectionnez une ligne."); return; }
        try {
            int id = (int) tableModel.getValueAt(row, 0);
            dao.modifier(lireFormulaire(id)); chargerDonnees();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
        }
    }

    private void supprimerCours() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        int id = (int) tableModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this,
                "Supprimer ce cours ? (les notes associées seront supprimées)",
                "Confirmation", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try { dao.supprimer(id); chargerDonnees(); }
            catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage()); }
        }
    }

    private Cours lireFormulaire(int id) {
        String intitule = tfIntitule.getText().trim();
        String semestre = tfSemestre.getText().trim();
        if (intitule.isEmpty()) throw new IllegalArgumentException("L'intitulé est obligatoire.");
        if (semestre.isEmpty()) throw new IllegalArgumentException("Le semestre est obligatoire.");
        int cap;
        try { cap = Integer.parseInt(tfCapacite.getText().trim()); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("La capacité doit être un entier."); }
        if (cap <= 0) throw new IllegalArgumentException("La capacité doit être > 0.");
        Cours c = new Cours(intitule, semestre, cap);
        c.setId(id);
        return c;
    }

    private void chargerDonnees() {
        try {
            tableModel.setRowCount(0);
            for (Cours c : dao.listerTous()) {
                tableModel.addRow(new Object[]{
                    c.getId(), c.getIntitule(), c.getSemestre(),
                    c.getCapacite(), c.getNbInscrits()
                });
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void vider() { tfIntitule.setText(""); tfSemestre.setText(""); tfCapacite.setText(""); }
    private String str(Object o) { return o == null ? "" : o.toString(); }
}
