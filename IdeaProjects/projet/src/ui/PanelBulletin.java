package ui;

import dao.EtudiantDAOImpl;
import dao.NoteDAOImpl;
import model.Etudiant;
import model.Note;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;
import java.util.regex.Pattern;

public class PanelBulletin extends JPanel {

    private final EtudiantDAOImpl etudiantDao;
    private final NoteDAOImpl     noteDao;

    // Left — student list
    private DefaultTableModel                 studentModel;
    private JTable                            studentTable;
    private TableRowSorter<DefaultTableModel> studentSorter;
    private JTextField                        tfSearch;
    private JComboBox<String>                 cbCritere;

    // Right — bulletin
    private DefaultTableModel noteModel;
    private JTable            noteTable;
    private JLabel            lblNomEtudiant, lblMoyenne;

    public PanelBulletin(EtudiantDAOImpl etudiantDao, NoteDAOImpl noteDao) {
        this.etudiantDao = etudiantDao;
        this.noteDao     = noteDao;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                buildLeftPanel(), buildRightPanel());
        split.setDividerLocation(320);
        split.setResizeWeight(0.30);
        split.setBorder(null);
        split.setDividerSize(8);

        add(split, BorderLayout.CENTER);
        chargerEtudiants();
    }

    // ── Left: student list + search ───────────────────────────────────────────

    private JPanel buildLeftPanel() {
        // Search criteria combo
        cbCritere = new JComboBox<>(new String[]{"Tous", "Nom", "Prénom", "Filière", "ID"});
        cbCritere.setFont(new Font("SansSerif", Font.PLAIN, 11));

        tfSearch = new JTextField(14);
        tfSearch.putClientProperty("JTextField.placeholderText", "Rechercher…");

        JButton btnReset = new JButton("✕");
        btnReset.setFont(new Font("SansSerif", Font.PLAIN, 11));
        btnReset.setToolTipText("Effacer la recherche");
        btnReset.addActionListener(e -> { tfSearch.setText(""); applyFilter(); });

        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
        searchRow.add(new JLabel("Critère :"));
        searchRow.add(cbCritere);
        searchRow.add(tfSearch);
        searchRow.add(btnReset);

        // Student table
        studentModel = new DefaultTableModel(
                new String[]{"ID", "Nom Prénom", "Filière"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        studentTable  = new JTable(studentModel);
        studentSorter = new TableRowSorter<>(studentModel);
        studentTable.setRowSorter(studentSorter);
        studentTable.setRowHeight(26);
        studentTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
        studentTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.setShowVerticalLines(false);
        studentTable.setGridColor(new Color(229, 231, 235));
        studentTable.getColumnModel().getColumn(0).setPreferredWidth(36);
        studentTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        studentTable.getColumnModel().getColumn(2).setPreferredWidth(100);

        // Live filter
        tfSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate (javax.swing.event.DocumentEvent e) { applyFilter(); }
            public void removeUpdate (javax.swing.event.DocumentEvent e) { applyFilter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
        });
        cbCritere.addActionListener(e -> applyFilter());

        // Click → load bulletin
        studentTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int v = studentTable.getSelectedRow();
            if (v < 0) return;
            int m = studentTable.convertRowIndexToModel(v);
            afficherBulletin(
                (int) studentModel.getValueAt(m, 0),
                studentModel.getValueAt(m, 1).toString(),
                studentModel.getValueAt(m, 2).toString()
            );
        });

        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setBorder(BorderFactory.createTitledBorder("Liste des étudiants"));
        p.add(searchRow, BorderLayout.NORTH);
        p.add(new JScrollPane(studentTable), BorderLayout.CENTER);
        return p;
    }

    private void applyFilter() {
        String text    = tfSearch.getText().trim();
        String critere = (String) cbCritere.getSelectedItem();
        if (text.isEmpty()) { studentSorter.setRowFilter(null); return; }
        String quoted = Pattern.quote(text);
        RowFilter<DefaultTableModel, Object> rf;
        switch (critere) {
            case "ID":      rf = RowFilter.regexFilter("(?i)" + quoted, 0); break;
            case "Nom":
            case "Prénom":  rf = RowFilter.regexFilter("(?i)" + quoted, 1); break;
            case "Filière": rf = RowFilter.regexFilter("(?i)" + quoted, 2); break;
            default:        rf = RowFilter.regexFilter("(?i)" + quoted);    break;
        }
        studentSorter.setRowFilter(rf);
    }

    // ── Right: bulletin display ───────────────────────────────────────────────

    private JPanel buildRightPanel() {
        lblNomEtudiant = new JLabel("← Sélectionnez un étudiant");
        lblNomEtudiant.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblNomEtudiant.setForeground(new Color(30, 80, 160));
        lblNomEtudiant.setBorder(BorderFactory.createEmptyBorder(4, 8, 6, 8));

        noteModel = new DefaultTableModel(
                new String[]{"ID Note", "Cours", "Note /20", "Date"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        noteTable = new JTable(noteModel);
        noteTable.setRowHeight(26);
        noteTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
        noteTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        noteTable.setShowVerticalLines(false);
        noteTable.setGridColor(new Color(229, 231, 235));
        noteTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Color rows by grade
        noteTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object value, boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, sel, foc, row, col);
                if (!sel) {
                    try {
                        double note = Double.parseDouble(noteModel.getValueAt(row, 2).toString());
                        if      (note >= 16) c.setBackground(new Color(198, 239, 206));
                        else if (note >= 10) c.setBackground(Color.WHITE);
                        else                 c.setBackground(new Color(255, 199, 206));
                    } catch (Exception e) { c.setBackground(Color.WHITE); }
                }
                return c;
            }
        });

        lblMoyenne = new JLabel("Moyenne : —");
        lblMoyenne.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblMoyenne.setForeground(new Color(0, 70, 140));

        JButton btnPdf = new JButton("Exporter PDF");
        btnPdf.addActionListener(e -> exporterPdf());

        JPanel bottom = new JPanel(new BorderLayout(8, 0));
        bottom.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
        bottom.add(lblMoyenne, BorderLayout.WEST);
        bottom.add(btnPdf,     BorderLayout.EAST);

        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setBorder(BorderFactory.createTitledBorder("Bulletin de notes"));
        p.add(lblNomEtudiant,             BorderLayout.NORTH);
        p.add(new JScrollPane(noteTable), BorderLayout.CENTER);
        p.add(bottom,                     BorderLayout.SOUTH);
        return p;
    }

    // ── Data loading ──────────────────────────────────────────────────────────

    private void chargerEtudiants() {
        try {
            studentModel.setRowCount(0);
            for (Etudiant e : etudiantDao.listerTous())
                studentModel.addRow(new Object[]{
                    e.getId(),
                    e.getPrenom() + " " + e.getNom(),
                    e.getFiliere()
                });
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
        }
    }

    private void afficherBulletin(int id, String nomPrenom, String filiere) {
        lblNomEtudiant.setText("Bulletin de : " + nomPrenom + "   |   Filière : " + filiere);
        noteModel.setRowCount(0);
        try {
            List<Note> notes = noteDao.notesParEtudiant(id);
            if (notes.isEmpty()) {
                lblMoyenne.setText("Moyenne : aucune note");
                lblMoyenne.setForeground(new Color(0, 70, 140));
                return;
            }
            double somme = 0;
            for (Note n : notes) {
                noteModel.addRow(new Object[]{
                    n.getId(), n.getCoursId(),
                    String.format("%.2f", n.getValeur()), n.getDateSaisie()
                });
                somme += n.getValeur();
            }
            double moy = somme / notes.size();
            lblMoyenne.setText(String.format("Moyenne : %.2f / 20   (%d note%s)",
                    moy, notes.size(), notes.size() > 1 ? "s" : ""));
            lblMoyenne.setForeground(moy >= 10 ? new Color(0, 100, 0) : Color.RED);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
        }
    }

    private void exporterPdf() {
        if (noteModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Affichez d'abord un bulletin."); return;
        }
        try {
            boolean ok = noteTable.print(
                    JTable.PrintMode.FIT_WIDTH,
                    new java.text.MessageFormat(lblNomEtudiant.getText()),
                    new java.text.MessageFormat("Page {0}")
            );
            if (ok)
                JOptionPane.showMessageDialog(this,
                        "Impression lancée.\n"
                        + "Sélectionnez « Microsoft Print to PDF » pour enregistrer en PDF.");
        } catch (java.awt.print.PrinterException ex) {
            JOptionPane.showMessageDialog(this, "Erreur d'impression : " + ex.getMessage());
        }
    }
}
