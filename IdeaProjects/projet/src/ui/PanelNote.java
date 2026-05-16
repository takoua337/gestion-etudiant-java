package ui;

import dao.CoursDAOImpl;
import dao.EtudiantDAOImpl;
import dao.NoteDAOImpl;
import model.Cours;
import model.Etudiant;
import model.Note;
import util.ComboItem;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class PanelNote extends JPanel {

    private final NoteDAOImpl     noteDAO;
    private final EtudiantDAOImpl etudiantDAO;
    private final CoursDAOImpl    coursDAO;

    private JComboBox<ComboItem> filterEtudiant, filterCours;
    private JComboBox<ComboItem> formEtudiant,   formCours;
    private JTextField           tfValeur;
    private DefaultTableModel    tableModel;
    private JTable               table;
    private boolean              loading = false;

    public PanelNote(NoteDAOImpl noteDAO, EtudiantDAOImpl etudiantDAO, CoursDAOImpl coursDAO) {
        this.noteDAO     = noteDAO;
        this.etudiantDAO = etudiantDAO;
        this.coursDAO    = coursDAO;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(buildFilterBar(), BorderLayout.NORTH);
        add(new JScrollPane(buildTable()), BorderLayout.CENTER);
        add(buildForm(), BorderLayout.SOUTH);

        chargerCombos();
        filterEtudiant.addActionListener(e -> { if (!loading) chargerNotes(); });
        filterCours   .addActionListener(e -> { if (!loading) chargerNotes(); });
        chargerNotes();
    }

    private JPanel buildFilterBar() {
        filterEtudiant = new JComboBox<>(); filterEtudiant.setPreferredSize(new Dimension(210,26));
        filterCours    = new JComboBox<>(); filterCours   .setPreferredSize(new Dimension(210,26));
        JButton btnReset = new JButton("Réinitialiser");
        btnReset.addActionListener(e -> {
            loading = true;
            filterEtudiant.setSelectedIndex(0);
            filterCours   .setSelectedIndex(0);
            loading = false;
            chargerNotes();
        });
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        p.setBorder(BorderFactory.createTitledBorder("Filtrer les notes"));
        p.add(new JLabel("Étudiant :")); p.add(filterEtudiant);
        p.add(new JLabel("Cours :"));    p.add(filterCours);
        p.add(btnReset);
        return p;
    }

    private JTable buildTable() {
        tableModel = new DefaultTableModel(
                new String[]{"ID","Étudiant","Cours","Note /20","Date"}, 0) {
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
            if (row >= 0) tfValeur.setText(
                    tableModel.getValueAt(row, 3).toString().replace(",", "."));
        });
        return table;
    }

    private JPanel buildForm() {
        formEtudiant = new JComboBox<>(); formEtudiant.setPreferredSize(new Dimension(210,26));
        formCours    = new JComboBox<>(); formCours   .setPreferredSize(new Dimension(210,26));
        tfValeur     = new JTextField(7);
        JButton btnAjouter   = new JButton("Ajouter");
        JButton btnModifier  = new JButton("Modifier");
        JButton btnSupprimer = new JButton("Supprimer");
        JButton btnRefresh   = new JButton("Actualiser combos");
        btnAjouter  .addActionListener(e -> ajouterNote());
        btnModifier .addActionListener(e -> modifierNote());
        btnSupprimer.addActionListener(e -> supprimerNote());
        btnRefresh  .addActionListener(e -> { chargerCombos(); chargerNotes(); });

        JPanel fields = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        fields.add(new JLabel("Étudiant :"));    fields.add(formEtudiant);
        fields.add(new JLabel("Cours :"));        fields.add(formCours);
        fields.add(new JLabel("Note (0‑20) :")); fields.add(tfValeur);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        btns.add(btnAjouter); btns.add(btnModifier); btns.add(btnSupprimer);
        btns.add(Box.createHorizontalStrut(20)); btns.add(btnRefresh);

        JPanel p = new JPanel(new BorderLayout(0, 2));
        p.setBorder(BorderFactory.createTitledBorder("Saisir / Modifier une note"));
        p.add(fields, BorderLayout.CENTER);
        p.add(btns,   BorderLayout.SOUTH);
        return p;
    }

    private void chargerCombos() {
        loading = true;
        try {
            List<Etudiant> etudiants = etudiantDAO.listerTous();
            List<Cours>    cours     = coursDAO.listerTous();
            filterEtudiant.removeAllItems(); filterCours.removeAllItems();
            formEtudiant  .removeAllItems(); formCours  .removeAllItems();
            filterEtudiant.addItem(new ComboItem(-1, "— Tous —"));
            filterCours   .addItem(new ComboItem(-1, "— Tous —"));
            for (Etudiant e : etudiants) {
                String lbl = e.getPrenom() + " " + e.getNom();
                filterEtudiant.addItem(new ComboItem(e.getId(), lbl));
                formEtudiant  .addItem(new ComboItem(e.getId(), lbl));
            }
            for (Cours c : cours) {
                String lbl = c.getIntitule() + " [" + c.getSemestre() + "]";
                filterCours.addItem(new ComboItem(c.getId(), lbl));
                formCours  .addItem(new ComboItem(c.getId(), lbl));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur chargement combos : " + ex.getMessage());
        } finally { loading = false; }
    }

    private void chargerNotes() {
        try {
            ComboItem se = (ComboItem) filterEtudiant.getSelectedItem();
            ComboItem sc = (ComboItem) filterCours   .getSelectedItem();
            Integer eid = (se == null || se.getId() == -1) ? null : se.getId();
            Integer cid = (sc == null || sc.getId() == -1) ? null : sc.getId();
            tableModel.setRowCount(0);
            for (Object[] row : noteDAO.listerAvecDetails(eid, cid)) tableModel.addRow(row);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
        }
    }

    private void ajouterNote() {
        try {
            ComboItem etu = (ComboItem) formEtudiant.getSelectedItem();
            ComboItem crs = (ComboItem) formCours   .getSelectedItem();
            if (etu == null || crs == null) {
                JOptionPane.showMessageDialog(this, "Sélectionnez un étudiant et un cours."); return;
            }
            double val = parseNote(tfValeur.getText().trim());
            Note n = new Note(val, etu.getId(), crs.getId(),
                    java.time.LocalDate.now().toString());
            noteDAO.ajouter(n);
            tfValeur.setText(""); chargerNotes();
            JOptionPane.showMessageDialog(this, "Note ajoutée (id=" + n.getId() + ")");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
        }
    }

    private void modifierNote() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Sélectionnez une note."); return; }
        try {
            double val = parseNote(tfValeur.getText().trim());
            Note n = new Note(); n.setId((int) tableModel.getValueAt(row, 0)); n.setValeur(val);
            noteDAO.modifier(n); chargerNotes();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
        }
    }

    private void supprimerNote() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        int id = (int) tableModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Supprimer cette note ?",
                "Confirmation", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try { noteDAO.supprimer(id); chargerNotes(); }
        catch (Exception ex) { JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage()); }
    }

    private double parseNote(String s) {
        double v;
        try { v = Double.parseDouble(s.replace(",", ".")); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("La note doit être un nombre."); }
        if (v < 0 || v > 20) throw new IllegalArgumentException("La note doit être entre 0 et 20.");
        return v;
    }
}
