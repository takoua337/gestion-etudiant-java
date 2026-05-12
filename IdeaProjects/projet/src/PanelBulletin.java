import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class PanelBulletin extends JPanel {

    private EtudiantDAOImpl etudiantDao;
    private NoteDAOImpl     noteDao;

    private JTextField        tfEtudiantId;
    private DefaultTableModel tableModel;
    private JTable            table;
    private JLabel            lblMoyenne;
    private JLabel            lblNomEtudiant;

    public PanelBulletin(EtudiantDAOImpl etudiantDao,
                         NoteDAOImpl noteDao) {
        this.etudiantDao = etudiantDao;
        this.noteDao     = noteDao;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ── HAUT : recherche ─────────────────────────────────
        JPanel top = new JPanel(new BorderLayout(8, 8));
        top.setBorder(BorderFactory.createTitledBorder(
                "Rechercher un étudiant"));

        JPanel searchRow = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 8, 4));
        tfEtudiantId  = new JTextField(10);
        JButton btnOk  = new JButton("Afficher le bulletin");
        JButton btnPdf = new JButton("Exporter PDF");

        searchRow.add(new JLabel("ID Étudiant :"));
        searchRow.add(tfEtudiantId);
        searchRow.add(btnOk);
        searchRow.add(btnPdf);

        lblNomEtudiant = new JLabel(" ");
        lblNomEtudiant.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblNomEtudiant.setBorder(
                BorderFactory.createEmptyBorder(4, 8, 4, 8));

        top.add(searchRow,      BorderLayout.NORTH);
        top.add(lblNomEtudiant, BorderLayout.SOUTH);

        // ── CENTRE : tableau des notes ────────────────────────
        String[] cols = {"ID Note", "Cours (ID)", "Note /20", "Date"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(24);
        table.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 12));
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));

        // Colorier les lignes selon la note
        table.setDefaultRenderer(Object.class,
                new DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(
                            JTable t, Object value, boolean isSelected,
                            boolean hasFocus, int row, int col) {
                        Component c = super.getTableCellRendererComponent(
                                t, value, isSelected, hasFocus, row, col);
                        if (!isSelected) {
                            try {
                                double note = Double.parseDouble(
                                        tableModel.getValueAt(row, 2)
                                                .toString());
                                if (note >= 16)
                                    c.setBackground(new Color(198, 239, 206));
                                else if (note >= 10)
                                    c.setBackground(Color.WHITE);
                                else
                                    c.setBackground(new Color(255, 199, 206));
                            } catch (Exception e) {
                                c.setBackground(Color.WHITE);
                            }
                        }
                        return c;
                    }
                });

        // ── BAS : moyenne ─────────────────────────────────────
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lblMoyenne = new JLabel("Moyenne : —");
        lblMoyenne.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblMoyenne.setForeground(new Color(0, 70, 140));
        bottom.add(lblMoyenne);

        // ── ASSEMBLAGE ────────────────────────────────────────
        add(top,                    BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(bottom,                 BorderLayout.SOUTH);

        // ── ACTIONS ───────────────────────────────────────────
        btnOk.addActionListener(e -> afficherBulletin());
        tfEtudiantId.addActionListener(e -> afficherBulletin());
        btnPdf.addActionListener(e -> exporterPdf());
    }

    private void afficherBulletin() {
        String txt = tfEtudiantId.getText().trim();
        if (txt.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Entrez un ID étudiant.");
            return;
        }
        try {
            int id = Integer.parseInt(txt);

            Etudiant et = etudiantDao.trouverParId(id);
            if (et == null) {
                JOptionPane.showMessageDialog(this,
                        "Aucun étudiant avec l'ID " + id);
                lblNomEtudiant.setText(" ");
                tableModel.setRowCount(0);
                lblMoyenne.setText("Moyenne : —");
                return;
            }

            lblNomEtudiant.setText(
                    "Bulletin de : " + et.getNom()
                            + " " + et.getPrenom()
                            + "  |  Filière : " + et.getFiliere());

            List<Note> notes = noteDao.listerParEtudiant(id);
            tableModel.setRowCount(0);

            if (notes.isEmpty()) {
                lblMoyenne.setText("Moyenne : aucune note");
                return;
            }

            double somme = 0;
            for (Note n : notes) {
                tableModel.addRow(new Object[]{
                        n.getId(),
                        n.getCoursId(),
                        String.format("%.2f", n.getValeur()),
                        n.getDateSaisie()
                });
                somme += n.getValeur();
            }

            double moy = somme / notes.size();
            lblMoyenne.setText(String.format(
                    "Moyenne : %.2f / 20   (%d note%s)",
                    moy, notes.size(),
                    notes.size() > 1 ? "s" : ""));
            lblMoyenne.setForeground(
                    moy >= 10 ? new Color(0, 100, 0) : Color.RED);

        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this,
                    "ID invalide (nombre entier attendu).");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erreur : " + ex.getMessage());
        }
    }

    private void exporterPdf() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Aucune donnée à exporter. "
                            + "Affichez d'abord un bulletin.");
            return;
        }
        JOptionPane.showMessageDialog(this,
                "Export PDF : à connecter avec iText/PDFBox.\n"
                        + "Les données sont prêtes dans le tableau.");
    }
}