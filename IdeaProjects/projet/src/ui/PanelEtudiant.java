package ui;

import dao.EtudiantDAOImpl;
import model.Etudiant;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.*;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

public class PanelEtudiant extends JPanel {

    private static final int PAGE_SIZE = 20;

    private final EtudiantDAOImpl dao;
    private DefaultTableModel     tableModel;
    private JTable                table;
    private TableRowSorter<DefaultTableModel> sorter;

    private JTextField tfNom, tfPrenom, tfFiliere, tfEmail, tfDate, tfSearch;
    private List<Etudiant> allData;
    private int currentPage = 0;
    private JLabel lblPage;

    public PanelEtudiant(EtudiantDAOImpl dao) {
        this.dao = dao;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ── Form ─────────────────────────────────────────────────────────────
        JPanel form = new JPanel(new GridLayout(3, 4, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Informations étudiant"));
        tfNom     = new JTextField();
        tfPrenom  = new JTextField();
        tfFiliere = new JTextField();
        tfEmail   = new JTextField();
        tfDate = new JTextField();
        tfDate.setEditable(false);
        tfDate.setBackground(Color.WHITE);
        tfDate.setToolTipText("Cliquez sur 📅 pour choisir une date");

        JButton btnCal = new JButton("📅");
        btnCal.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btnCal.setFocusPainted(false);
        btnCal.setToolTipText("Ouvrir le calendrier");
        btnCal.addActionListener(e -> {
            CalendarDialog dlg = new CalendarDialog(this, tfDate.getText().trim());
            dlg.setVisible(true);
            String chosen = dlg.getSelectedDate();
            if (chosen != null) tfDate.setText(chosen);
        });

        JPanel datePanel = new JPanel(new BorderLayout(3, 0));
        datePanel.setOpaque(false);
        datePanel.add(tfDate, BorderLayout.CENTER);
        datePanel.add(btnCal, BorderLayout.EAST);

        form.add(new JLabel("Nom *:"));        form.add(tfNom);
        form.add(new JLabel("Prénom *:"));     form.add(tfPrenom);
        form.add(new JLabel("Filière *:"));    form.add(tfFiliere);
        form.add(new JLabel("Email *:"));      form.add(tfEmail);
        form.add(new JLabel("Date naiss. :")); form.add(datePanel);
        form.add(new JLabel(""));              form.add(new JLabel(""));

        // ── Buttons ──────────────────────────────────────────────────────────
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        JButton btnAjouter    = new JButton("Ajouter");
        JButton btnModifier   = new JButton("Modifier");
        JButton btnSupprimer  = new JButton("Supprimer");
        JButton btnActualiser = new JButton("Actualiser");
        JButton btnExportCSV  = new JButton("Exporter CSV");
        buttons.add(btnAjouter); buttons.add(btnModifier);
        buttons.add(btnSupprimer); buttons.add(btnActualiser);
        buttons.add(Box.createHorizontalStrut(20));
        buttons.add(btnExportCSV);

        // ── Search ───────────────────────────────────────────────────────────
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        tfSearch = new JTextField(24);
        searchPanel.add(new JLabel("Rechercher :"));
        searchPanel.add(tfSearch);

        // ── Table ─────────────────────────────────────────────────────────────
        String[] cols = {"ID","Nom","Prénom","Filière","Email","Date naiss."};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table  = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        table.setRowHeight(26);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.getSelectionModel().addListSelectionListener(e -> {
            int v = table.getSelectedRow();
            if (v < 0) return;
            int m = table.convertRowIndexToModel(v);
            tfNom    .setText(s(tableModel.getValueAt(m, 1)));
            tfPrenom .setText(s(tableModel.getValueAt(m, 2)));
            tfFiliere.setText(s(tableModel.getValueAt(m, 3)));
            tfEmail  .setText(s(tableModel.getValueAt(m, 4)));
            tfDate   .setText(s(tableModel.getValueAt(m, 5)));
        });

        tfSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            void update() {
                String t = tfSearch.getText().trim();
                sorter.setRowFilter(t.isEmpty() ? null :
                        RowFilter.regexFilter("(?i)" + Pattern.quote(t)));
            }
            public void insertUpdate (javax.swing.event.DocumentEvent e) { update(); }
            public void removeUpdate (javax.swing.event.DocumentEvent e) { update(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }
        });

        // ── Pagination ───────────────────────────────────────────────────────
        JPanel pageBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 4));
        JButton btnPrev = new JButton("◀ Préc.");
        JButton btnNext = new JButton("Suiv. ▶");
        lblPage = new JLabel("Page 1");
        lblPage.setFont(new Font("SansSerif", Font.BOLD, 12));
        pageBar.add(btnPrev); pageBar.add(lblPage); pageBar.add(btnNext);
        btnPrev.addActionListener(e -> { if (currentPage > 0) { currentPage--; afficherPage(); } });
        btnNext.addActionListener(e -> {
            int max = (allData == null ? 0 : (allData.size() - 1) / PAGE_SIZE);
            if (currentPage < max) { currentPage++; afficherPage(); }
        });

        // ── Layout ───────────────────────────────────────────────────────────
        JPanel top = new JPanel(new BorderLayout(0, 6));
        top.add(form, BorderLayout.NORTH);
        top.add(buttons, BorderLayout.CENTER);
        top.add(searchPanel, BorderLayout.SOUTH);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(pageBar, BorderLayout.SOUTH);

        // ── Actions ──────────────────────────────────────────────────────────
        btnAjouter   .addActionListener(e -> ajouterEtudiant());
        btnModifier  .addActionListener(e -> modifierEtudiant());
        btnSupprimer .addActionListener(e -> supprimerEtudiant());
        btnActualiser.addActionListener(e -> chargerDonnees());
        btnExportCSV .addActionListener(e -> exporterCSV());
        chargerDonnees();
    }

    private void ajouterEtudiant() {
        try {
            dao.ajouter(lireFormulaire(-1));
            chargerDonnees(); vider();
            JOptionPane.showMessageDialog(this, "Étudiant ajouté !");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
        }
    }

    private void modifierEtudiant() {
        int v = table.getSelectedRow();
        if (v < 0) { JOptionPane.showMessageDialog(this, "Sélectionnez une ligne."); return; }
        try {
            int id = (int) tableModel.getValueAt(table.convertRowIndexToModel(v), 0);
            dao.modifier(lireFormulaire(id)); chargerDonnees();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
        }
    }

    private void supprimerEtudiant() {
        int v = table.getSelectedRow();
        if (v < 0) return;
        int id = (int) tableModel.getValueAt(table.convertRowIndexToModel(v), 0);
        if (JOptionPane.showConfirmDialog(this, "Confirmer la suppression ?",
                "Confirmation", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try { dao.supprimerLogique(id); chargerDonnees(); }
            catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage()); }
        }
    }

    private Etudiant lireFormulaire(int id) {
        String nom     = tfNom    .getText().trim();
        String prenom  = tfPrenom .getText().trim();
        String filiere = tfFiliere.getText().trim();
        String email   = tfEmail  .getText().trim();
        String dateStr = tfDate   .getText().trim();
        if (nom.isEmpty())     throw new IllegalArgumentException("Le nom est obligatoire.");
        if (prenom.isEmpty())  throw new IllegalArgumentException("Le prénom est obligatoire.");
        if (filiere.isEmpty()) throw new IllegalArgumentException("La filière est obligatoire.");
        if (!email.matches("^[\\w.+\\-]+@[\\w\\-]+\\.[a-zA-Z]{2,}$"))
            throw new IllegalArgumentException("Format d'email invalide.");
        java.sql.Date date = null;
        if (!dateStr.isEmpty()) {
            try { date = java.sql.Date.valueOf(dateStr); }
            catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Date invalide — format : yyyy-MM-dd");
            }
        }
        Etudiant et = new Etudiant(nom, prenom, filiere, email, date);
        et.setId(id);
        return et;
    }

    private void chargerDonnees() {
        try { allData = dao.listerTous(); currentPage = 0; afficherPage(); }
        catch (Exception ex) { ex.printStackTrace(); }
    }

    private void afficherPage() {
        tableModel.setRowCount(0);
        if (allData == null) return;
        int from = currentPage * PAGE_SIZE;
        int to   = Math.min(from + PAGE_SIZE, allData.size());
        for (int i = from; i < to; i++) {
            Etudiant e = allData.get(i);
            tableModel.addRow(new Object[]{
                e.getId(), e.getNom(), e.getPrenom(), e.getFiliere(), e.getEmail(),
                e.getDateNaissance() != null ? e.getDateNaissance().toString() : ""
            });
        }
        int total = allData.isEmpty() ? 1 : (int) Math.ceil((double) allData.size() / PAGE_SIZE);
        lblPage.setText("Page " + (currentPage + 1) + " / " + total);
    }

    private void exporterCSV() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("etudiants.csv"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        try (PrintWriter pw = new PrintWriter(new FileWriter(fc.getSelectedFile()))) {
            pw.println("id,nom,prenom,filiere,email,date_naissance");
            if (allData != null) for (Etudiant e : allData)
                pw.printf("%d,%s,%s,%s,%s,%s%n",
                    e.getId(), csv(e.getNom()), csv(e.getPrenom()),
                    csv(e.getFiliere()), csv(e.getEmail()),
                    e.getDateNaissance() != null ? e.getDateNaissance() : "");
            JOptionPane.showMessageDialog(this, "Export CSV réussi !");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Erreur export : " + ex.getMessage());
        }
    }

    private void vider() {
        tfNom.setText(""); tfPrenom.setText("");
        tfFiliere.setText(""); tfEmail.setText(""); tfDate.setText("");
    }
    private String s(Object o)   { return o == null ? "" : o.toString(); }
    private String csv(String v) { return v != null && v.contains(",") ? "\"" + v + "\"" : (v == null ? "" : v); }

    // ── Calendar date-picker dialog ───────────────────────────────────────────

    static class CalendarDialog extends javax.swing.JDialog {

        private final Calendar cal = Calendar.getInstance();
        private String selectedDate = null;

        private JLabel lblMonthYear;
        private JPanel dayGrid;

        CalendarDialog(java.awt.Component parent, String initialDate) {
            super(SwingUtilities.getWindowAncestor(parent),
                  "Choisir une date",
                  java.awt.Dialog.ModalityType.APPLICATION_MODAL);

            if (initialDate != null && !initialDate.isEmpty()) {
                try { cal.setTime(java.sql.Date.valueOf(initialDate)); }
                catch (Exception ignored) {}
            }

            setLayout(new BorderLayout(6, 6));
            setResizable(false);

            // Month navigation header
            JButton btnPrev = new JButton("◀");
            JButton btnNext = new JButton("▶");
            btnPrev.setFocusPainted(false);
            btnNext.setFocusPainted(false);
            lblMonthYear = new JLabel("", SwingConstants.CENTER);
            lblMonthYear.setFont(new Font("SansSerif", Font.BOLD, 14));

            JPanel header = new JPanel(new BorderLayout(4, 0));
            header.setBorder(BorderFactory.createEmptyBorder(8, 8, 4, 8));
            header.add(btnPrev,       BorderLayout.WEST);
            header.add(lblMonthYear,  BorderLayout.CENTER);
            header.add(btnNext,       BorderLayout.EAST);

            dayGrid = new JPanel(new GridLayout(0, 7, 3, 3));
            dayGrid.setBorder(BorderFactory.createEmptyBorder(4, 8, 8, 8));

            add(header,  BorderLayout.NORTH);
            add(dayGrid, BorderLayout.CENTER);

            btnPrev.addActionListener(e -> { cal.add(Calendar.MONTH, -1); rebuildGrid(); });
            btnNext.addActionListener(e -> { cal.add(Calendar.MONTH,  1); rebuildGrid(); });

            rebuildGrid();
            pack();
            setLocationRelativeTo(parent);
        }

        private void rebuildGrid() {
            dayGrid.removeAll();

            String[] months = {
                "Janvier","Février","Mars","Avril","Mai","Juin",
                "Juillet","Août","Septembre","Octobre","Novembre","Décembre"
            };
            lblMonthYear.setText(
                months[cal.get(Calendar.MONTH)] + "  " + cal.get(Calendar.YEAR));

            // Day-of-week header row (Monday first)
            String[] dayNames = {"Lu","Ma","Me","Je","Ve","Sa","Di"};
            for (String d : dayNames) {
                JLabel lbl = new JLabel(d, SwingConstants.CENTER);
                lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
                lbl.setForeground(new Color(100, 100, 120));
                dayGrid.add(lbl);
            }

            // Compute offset so week starts on Monday
            Calendar tmp = (Calendar) cal.clone();
            tmp.set(Calendar.DAY_OF_MONTH, 1);
            int dow    = tmp.get(Calendar.DAY_OF_WEEK); // Sun=1 … Sat=7
            int offset = (dow == Calendar.SUNDAY) ? 6 : dow - Calendar.MONDAY;

            int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            Calendar today  = Calendar.getInstance();

            // Empty cells before the 1st
            for (int i = 0; i < offset; i++) dayGrid.add(new JLabel(""));

            for (int day = 1; day <= daysInMonth; day++) {
                final int d = day;
                JButton btn = new JButton(String.valueOf(d));
                btn.setFont(new Font("SansSerif", Font.PLAIN, 11));
                btn.setFocusPainted(false);
                btn.setMargin(new Insets(2, 2, 2, 2));

                boolean isToday = d == today.get(Calendar.DAY_OF_MONTH)
                        && cal.get(Calendar.MONTH) == today.get(Calendar.MONTH)
                        && cal.get(Calendar.YEAR)  == today.get(Calendar.YEAR);
                if (isToday) {
                    btn.setBackground(new Color(219, 234, 254));
                    btn.setFont(new Font("SansSerif", Font.BOLD, 11));
                }

                btn.addActionListener(e -> {
                    int y = cal.get(Calendar.YEAR);
                    int m = cal.get(Calendar.MONTH) + 1;
                    selectedDate = String.format("%04d-%02d-%02d", y, m, d);
                    dispose();
                });
                dayGrid.add(btn);
            }

            // Pad remaining cells to complete last row
            int total     = offset + daysInMonth;
            int remainder = (7 - total % 7) % 7;
            for (int i = 0; i < remainder; i++) dayGrid.add(new JLabel(""));

            dayGrid.revalidate();
            dayGrid.repaint();
            pack();
        }

        String getSelectedDate() { return selectedDate; }
    }
}
