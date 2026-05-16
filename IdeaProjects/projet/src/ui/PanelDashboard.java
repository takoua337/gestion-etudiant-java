package ui;

import service.DashboardService;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;

public class PanelDashboard extends JPanel {

    private final DashboardService svc;

    private final JLabel valEtudiants = kpiVal();
    private final JLabel valNotes     = kpiVal();
    private final JLabel valMoyenne   = kpiVal();
    private final JLabel valReussite  = kpiVal();
    private final JLabel valMax       = kpiVal();
    private final JLabel valMin       = kpiVal();

    private final DefaultTableModel classementModel;
    private final DefaultTableModel filiereModel;
    private final DefaultTableModel semestreModel;
    private final BarChartPanel     barChart  = new BarChartPanel();
    private final PieChartPanel     pieChart  = new PieChartPanel();
    private final LineChartPanel    lineChart = new LineChartPanel();

    public PanelDashboard(DashboardService svc) {
        this.svc = svc;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        setBackground(new Color(248, 250, 252));

        // ── Title bar ─────────────────────────────────────────────────────────
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setOpaque(false);
        JLabel title = new JLabel("  Tableau de bord");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(new Color(30, 80, 160));
        JButton btnRefresh = new JButton("⟳  Rafraîchir");
        btnRefresh.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnRefresh.setFocusPainted(false);
        titleBar.add(title, BorderLayout.WEST);
        titleBar.add(btnRefresh, BorderLayout.EAST);

        // ── KPI cards ─────────────────────────────────────────────────────────
        JPanel kpiRow = new JPanel(new GridLayout(1, 6, 10, 0));
        kpiRow.setOpaque(false);
        kpiRow.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        kpiRow.add(kpiCard("Étudiants actifs",  valEtudiants, new Color(219,234,254), new Color(30,64,175)));
        kpiRow.add(kpiCard("Notes saisies",     valNotes,     new Color(209,250,229), new Color(6,95,70)));
        kpiRow.add(kpiCard("Moyenne générale",  valMoyenne,   new Color(237,233,254), new Color(91,33,182)));
        kpiRow.add(kpiCard("Taux de réussite",  valReussite,  new Color(254,249,195), new Color(133,77,14)));
        kpiRow.add(kpiCard("Note maximale",     valMax,       new Color(220,252,231), new Color(22,101,52)));
        kpiRow.add(kpiCard("Note minimale",     valMin,       new Color(254,226,226), new Color(153,27,27)));

        JPanel top = new JPanel(new BorderLayout(0, 4));
        top.setOpaque(false);
        top.add(titleBar, BorderLayout.NORTH);
        top.add(kpiRow,   BorderLayout.CENTER);

        // ── Classement ────────────────────────────────────────────────────────
        classementModel = new DefaultTableModel(
                new String[]{"Rang","Étudiant","Filière","Moyenne /20","Mention"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tableC = buildTable(classementModel);
        tableC.setDefaultRenderer(Object.class, new MentionRenderer(classementModel));
        int[] wC = {45, 190, 110, 90, 100};
        for (int i = 0; i < wC.length; i++)
            tableC.getColumnModel().getColumn(i).setPreferredWidth(wC[i]);

        // ── Filières ──────────────────────────────────────────────────────────
        filiereModel = new DefaultTableModel(
                new String[]{"Filière","Étudiants","Moyenne /20","Admis ✓","Échec ✗"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        // ── Semestres ─────────────────────────────────────────────────────────
        semestreModel = new DefaultTableModel(
                new String[]{"Semestre","Nb notes","Moyenne /20"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        // ── Right panel (tabbed) ──────────────────────────────────────────────
        JTabbedPane rightTabs = new JTabbedPane();
        rightTabs.setFont(new Font("SansSerif", Font.BOLD, 12));
        rightTabs.addTab("Filières",       scrollOf(buildTable(filiereModel)));
        rightTabs.addTab("Semestres",      scrollOf(buildTable(semestreModel)));
        rightTabs.addTab("Barres",         barChart);
        rightTabs.addTab("Camembert",      pieChart);
        rightTabs.addTab("Courbe",         lineChart);

        // ── Center split ──────────────────────────────────────────────────────
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                sectionPanel("Classement de la promotion", scrollOf(tableC)),
                sectionPanel("Statistiques", rightTabs));
        split.setResizeWeight(0.60);
        split.setBorder(null);
        split.setDividerSize(8);

        add(top,   BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);
        btnRefresh.addActionListener(e -> charger());
        charger();
    }

    private void charger() {
        new SwingWorker<Void, Void>() {
            int    nbEtu, nbNotes;
            Double moy, taux, max, min;
            List<Object[]> classement, filieres, semestres;

            @Override protected Void doInBackground() throws Exception {
                nbEtu      = svc.totalEtudiants();
                nbNotes    = svc.totalNotes();
                moy        = svc.moyenneGenerale();
                taux       = svc.tauxReussite();
                max        = svc.noteMax();
                min        = svc.noteMin();
                classement = svc.classement(15);
                filieres   = svc.statsParFiliere();
                semestres  = svc.statsParSemestre();
                return null;
            }

            @Override protected void done() {
                try {
                    get();
                    valEtudiants.setText(String.valueOf(nbEtu));
                    valNotes    .setText(String.valueOf(nbNotes));
                    valMoyenne  .setText(moy  == null ? "N/A" : String.format("%.2f", moy));
                    valReussite .setText(taux == null ? "N/A" : String.format("%.1f %%", taux));
                    valMax      .setText(max  == null ? "N/A" : String.format("%.2f", max));
                    valMin      .setText(min  == null ? "N/A" : String.format("%.2f", min));
                    fill(classementModel, classement);
                    fill(filiereModel,   filieres);
                    fill(semestreModel,  semestres);
                    barChart .setData(filieres,  "Moyenne par filière",          2);
                    pieChart .setData(filieres,  "Étudiants par filière",         1);
                    lineChart.setData(semestres, "Évolution moyenne / semestre",  2);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(PanelDashboard.this,
                            "Erreur : " + ex.getMessage());
                }
            }
        }.execute();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static void fill(DefaultTableModel m, List<Object[]> rows) {
        m.setRowCount(0);
        if (rows != null) for (Object[] r : rows) m.addRow(r);
    }

    private static JLabel kpiVal() {
        JLabel l = new JLabel("—", SwingConstants.CENTER);
        l.setFont(new Font("SansSerif", Font.BOLD, 26));
        return l;
    }

    private static JPanel kpiCard(String titre, JLabel val, Color bg, Color fg) {
        val.setForeground(fg);
        JLabel lbl = new JLabel(titre, SwingConstants.CENTER);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lbl.setForeground(fg.darker());
        JPanel p = new JPanel(new BorderLayout(2, 8));
        p.setBackground(bg);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bg.darker(), 1, true),
                BorderFactory.createEmptyBorder(12, 10, 12, 10)));
        p.add(lbl, BorderLayout.NORTH);
        p.add(val, BorderLayout.CENTER);
        return p;
    }

    private static JTable buildTable(DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setRowHeight(26);
        t.setFont(new Font("SansSerif", Font.PLAIN, 12));
        t.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        t.getTableHeader().setBackground(new Color(243, 244, 246));
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        t.setShowVerticalLines(false);
        t.setGridColor(new Color(229, 231, 235));
        t.setIntercellSpacing(new Dimension(8, 1));
        return t;
    }

    private static JScrollPane scrollOf(JComponent c) {
        JScrollPane sp = new JScrollPane(c);
        sp.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235)));
        return sp;
    }

    private static JPanel sectionPanel(String titre, JComponent content) {
        JLabel lbl = new JLabel("  " + titre);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        lbl.setForeground(new Color(55, 65, 81));
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        p.add(lbl, BorderLayout.NORTH);
        p.add(content, BorderLayout.CENTER);
        return p;
    }

    // ── Mention row renderer ──────────────────────────────────────────────────

    private static class MentionRenderer extends DefaultTableCellRenderer {
        private final DefaultTableModel model;
        MentionRenderer(DefaultTableModel m) { this.model = m; }

        @Override
        public Component getTableCellRendererComponent(
                JTable t, Object value, boolean sel, boolean foc, int row, int col) {
            Component c = super.getTableCellRendererComponent(t, value, sel, foc, row, col);
            if (!sel) {
                try {
                    double moy = Double.parseDouble(
                            model.getValueAt(row, 3).toString().replace(",", "."));
                    if      (moy >= 16) c.setBackground(new Color(220, 252, 231));
                    else if (moy >= 14) c.setBackground(new Color(219, 234, 254));
                    else if (moy >= 12) c.setBackground(new Color(254, 249, 195));
                    else if (moy >= 10) c.setBackground(Color.WHITE);
                    else                c.setBackground(new Color(254, 226, 226));
                } catch (Exception e) { c.setBackground(Color.WHITE); }
            }
            setHorizontalAlignment(col == 0 || col == 3 ? CENTER : LEFT);
            return c;
        }
    }

    // ── Bar chart (pure Java2D, no external library) ──────────────────────────

    static class BarChartPanel extends JPanel {
        private List<Object[]> data       = new ArrayList<>();
        private String         chartTitle = "";
        private int            valCol     = 2;

        void setData(List<Object[]> rows, String title, int valueColumnIndex) {
            this.data       = rows == null ? new ArrayList<>() : rows;
            this.chartTitle = title;
            this.valCol     = valueColumnIndex;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int W = getWidth(), H = getHeight();
            int mL = 50, mR = 15, mT = 35, mB = 50;
            int cW = W - mL - mR, cH = H - mT - mB;

            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, W, H);

            g2.setFont(new Font("SansSerif", Font.BOLD, 13));
            g2.setColor(new Color(30, 80, 160));
            FontMetrics fmT = g2.getFontMetrics();
            g2.drawString(chartTitle, (W - fmT.stringWidth(chartTitle)) / 2, 22);

            if (data.isEmpty()) {
                g2.setFont(new Font("SansSerif", Font.ITALIC, 12));
                g2.setColor(Color.GRAY);
                g2.drawString("Aucune donnée", (W - 80) / 2, H / 2);
                g2.dispose(); return;
            }

            double maxVal = data.stream().mapToDouble(r -> parseVal(r[valCol])).max().orElse(20);
            if (maxVal < 1) maxVal = 20;

            g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
            FontMetrics fmS = g2.getFontMetrics();
            for (int i = 0; i <= 5; i++) {
                int y = mT + cH * i / 5;
                g2.setColor(new Color(229, 231, 235));
                g2.drawLine(mL, y, mL + cW, y);
                String lbl = String.format("%.0f", maxVal * (5 - i) / 5);
                g2.setColor(new Color(107, 114, 128));
                g2.drawString(lbl, mL - fmS.stringWidth(lbl) - 4, y + 4);
            }

            int n  = data.size();
            int bW = Math.max(12, Math.min(60, (cW - 10) / n - 8));
            int gap = n > 1 ? (cW - bW * n) / (n + 1) : cW / 2;
            Color[] pal = {
                new Color(96,165,250), new Color(52,211,153), new Color(251,146,60),
                new Color(167,139,250), new Color(251,191,36), new Color(248,113,113),
                new Color(34,211,238),  new Color(163,230,53)
            };

            for (int i = 0; i < n; i++) {
                double val = parseVal(data.get(i)[valCol]);
                int x  = mL + gap + i * (bW + gap);
                int bH = maxVal > 0 ? (int)(val / maxVal * cH) : 0;
                int y  = mT + cH - bH;

                g2.setColor(pal[i % pal.length]);
                g2.fillRoundRect(x, y, bW, Math.max(bH, 1), 6, 6);

                g2.setFont(new Font("SansSerif", Font.BOLD, 9));
                g2.setColor(new Color(55, 65, 81));
                String vs = val > 0 ? String.format("%.1f", val) : "";
                FontMetrics fmV = g2.getFontMetrics();
                g2.drawString(vs, x + (bW - fmV.stringWidth(vs)) / 2, y - 3);

                g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
                g2.setColor(new Color(75, 85, 99));
                String lbl = data.get(i)[0].toString();
                if (lbl.length() > 9) lbl = lbl.substring(0, 9);
                FontMetrics fmL = g2.getFontMetrics();
                g2.drawString(lbl, x + (bW - fmL.stringWidth(lbl)) / 2, mT + cH + 14);
            }

            g2.setColor(new Color(156, 163, 175));
            g2.drawLine(mL, mT, mL, mT + cH);
            g2.drawLine(mL, mT + cH, mL + cW, mT + cH);
            g2.dispose();
        }

        private double parseVal(Object o) {
            if (o == null) return 0;
            try { return Double.parseDouble(o.toString().replace(",",".").replace("—","0")); }
            catch (Exception e) { return 0; }
        }

        @Override public Dimension getPreferredSize() { return new Dimension(300, 220); }
    }

    // ── Pie chart ─────────────────────────────────────────────────────────────

    static class PieChartPanel extends JPanel {
        private List<Object[]> data      = new ArrayList<>();
        private String         chartTitle = "";
        private int            valCol     = 1;

        void setData(List<Object[]> rows, String title, int valueColumnIndex) {
            this.data       = rows == null ? new ArrayList<>() : rows;
            this.chartTitle = title;
            this.valCol     = valueColumnIndex;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int W = getWidth(), H = getHeight();
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, W, H);

            g2.setFont(new Font("SansSerif", Font.BOLD, 13));
            g2.setColor(new Color(30, 80, 160));
            FontMetrics fmT = g2.getFontMetrics();
            g2.drawString(chartTitle, (W - fmT.stringWidth(chartTitle)) / 2, 22);

            if (data.isEmpty()) {
                g2.setFont(new Font("SansSerif", Font.ITALIC, 12));
                g2.setColor(Color.GRAY);
                g2.drawString("Aucune donnée", (W - 80) / 2, H / 2);
                g2.dispose(); return;
            }

            double total = data.stream().mapToDouble(r -> parseVal(r[valCol])).sum();
            if (total <= 0) { g2.dispose(); return; }

            Color[] pal = {
                new Color(96,165,250), new Color(52,211,153), new Color(251,146,60),
                new Color(167,139,250), new Color(251,191,36), new Color(248,113,113),
                new Color(34,211,238),  new Color(163,230,53)
            };

            int legendH   = data.size() * 18 + 10;
            int pieSize   = Math.min(W - 20, H - 40 - legendH);
            if (pieSize < 40) pieSize = 40;
            int px = (W - pieSize) / 2;
            int py = 32;

            double startAngle = 0;
            int n = data.size();
            for (int i = 0; i < n; i++) {
                double val  = parseVal(data.get(i)[valCol]);
                double sweep = val / total * 360.0;
                g2.setColor(pal[i % pal.length]);
                g2.fillArc(px, py, pieSize, pieSize, (int) startAngle, (int) Math.ceil(sweep));
                g2.setColor(Color.WHITE);
                g2.drawArc(px, py, pieSize, pieSize, (int) startAngle, (int) Math.ceil(sweep));

                // percentage label inside slice
                double midAngle = Math.toRadians(startAngle + sweep / 2);
                int lx = (int) (px + pieSize / 2.0 + pieSize * 0.30 * Math.cos(midAngle));
                int ly = (int) (py + pieSize / 2.0 - pieSize * 0.30 * Math.sin(midAngle));
                if (sweep > 15) {
                    g2.setFont(new Font("SansSerif", Font.BOLD, 9));
                    g2.setColor(Color.WHITE);
                    String pct = String.format("%.0f%%", val / total * 100);
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(pct, lx - fm.stringWidth(pct) / 2, ly + 4);
                }
                startAngle += sweep;
            }

            // Draw legend below the pie
            int legendY = py + pieSize + 12;
            g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
            for (int i = 0; i < n; i++) {
                int lx = 12, ly = legendY + i * 18;
                g2.setColor(pal[i % pal.length]);
                g2.fillRoundRect(lx, ly, 12, 12, 4, 4);
                g2.setColor(new Color(55, 65, 81));
                String lbl = data.get(i)[0].toString();
                if (lbl.length() > 16) lbl = lbl.substring(0, 16);
                double val = parseVal(data.get(i)[valCol]);
                g2.drawString(lbl + "  (" + (int) val + ")", lx + 16, ly + 10);
            }
            g2.dispose();
        }

        private double parseVal(Object o) {
            if (o == null) return 0;
            try { return Double.parseDouble(o.toString().replace(",", ".").replace("—", "0")); }
            catch (Exception e) { return 0; }
        }

        @Override public Dimension getPreferredSize() { return new Dimension(300, 280); }
    }

    // ── Line chart ────────────────────────────────────────────────────────────

    static class LineChartPanel extends JPanel {
        private List<Object[]> data       = new ArrayList<>();
        private String         chartTitle = "";
        private int            valCol     = 2;

        void setData(List<Object[]> rows, String title, int valueColumnIndex) {
            this.data       = rows == null ? new ArrayList<>() : rows;
            this.chartTitle = title;
            this.valCol     = valueColumnIndex;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int W = getWidth(), H = getHeight();
            int mL = 50, mR = 20, mT = 35, mB = 45;
            int cW = W - mL - mR, cH = H - mT - mB;

            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, W, H);

            g2.setFont(new Font("SansSerif", Font.BOLD, 13));
            g2.setColor(new Color(30, 80, 160));
            FontMetrics fmT = g2.getFontMetrics();
            g2.drawString(chartTitle, (W - fmT.stringWidth(chartTitle)) / 2, 22);

            if (data.isEmpty()) {
                g2.setFont(new Font("SansSerif", Font.ITALIC, 12));
                g2.setColor(Color.GRAY);
                g2.drawString("Aucune donnée", (W - 80) / 2, H / 2);
                g2.dispose(); return;
            }

            double maxVal = data.stream().mapToDouble(r -> parseVal(r[valCol])).max().orElse(20);
            if (maxVal < 1) maxVal = 20;

            // Grid + Y axis labels
            g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
            FontMetrics fmS = g2.getFontMetrics();
            for (int i = 0; i <= 5; i++) {
                int y = mT + cH * i / 5;
                g2.setColor(new Color(229, 231, 235));
                g2.drawLine(mL, y, mL + cW, y);
                String lbl = String.format("%.0f", maxVal * (5 - i) / 5);
                g2.setColor(new Color(107, 114, 128));
                g2.drawString(lbl, mL - fmS.stringWidth(lbl) - 4, y + 4);
            }

            // Axes
            g2.setColor(new Color(156, 163, 175));
            g2.drawLine(mL, mT, mL, mT + cH);
            g2.drawLine(mL, mT + cH, mL + cW, mT + cH);

            int n = data.size();
            if (n < 2) {
                g2.dispose(); return;
            }

            int[] xs = new int[n];
            int[] ys = new int[n];
            for (int i = 0; i < n; i++) {
                xs[i] = mL + i * cW / (n - 1);
                double val = parseVal(data.get(i)[valCol]);
                ys[i] = mT + cH - (maxVal > 0 ? (int)(val / maxVal * cH) : 0);
            }

            // Shaded area under line
            int[] polyX = new int[n + 2];
            int[] polyY = new int[n + 2];
            polyX[0] = xs[0]; polyY[0] = mT + cH;
            for (int i = 0; i < n; i++) { polyX[i+1] = xs[i]; polyY[i+1] = ys[i]; }
            polyX[n+1] = xs[n-1]; polyY[n+1] = mT + cH;
            g2.setColor(new Color(96, 165, 250, 50));
            g2.fillPolygon(polyX, polyY, n + 2);

            // Line
            g2.setColor(new Color(59, 130, 246));
            g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            for (int i = 0; i < n - 1; i++) g2.drawLine(xs[i], ys[i], xs[i+1], ys[i+1]);
            g2.setStroke(new BasicStroke(1f));

            // Points + labels
            for (int i = 0; i < n; i++) {
                // dot
                g2.setColor(new Color(30, 80, 160));
                g2.fillOval(xs[i] - 4, ys[i] - 4, 8, 8);
                g2.setColor(Color.WHITE);
                g2.fillOval(xs[i] - 2, ys[i] - 2, 4, 4);

                // value above point
                double val = parseVal(data.get(i)[valCol]);
                g2.setFont(new Font("SansSerif", Font.BOLD, 9));
                g2.setColor(new Color(30, 80, 160));
                String vs = String.format("%.1f", val);
                FontMetrics fmV = g2.getFontMetrics();
                g2.drawString(vs, xs[i] - fmV.stringWidth(vs) / 2, ys[i] - 7);

                // X label below axis
                String lbl = data.get(i)[0].toString();
                g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
                g2.setColor(new Color(75, 85, 99));
                FontMetrics fmL = g2.getFontMetrics();
                g2.drawString(lbl, xs[i] - fmL.stringWidth(lbl) / 2, mT + cH + 14);
            }
            g2.dispose();
        }

        private double parseVal(Object o) {
            if (o == null) return 0;
            try { return Double.parseDouble(o.toString().replace(",", ".").replace("—", "0")); }
            catch (Exception e) { return 0; }
        }

        @Override public Dimension getPreferredSize() { return new Dimension(300, 220); }
    }
}
