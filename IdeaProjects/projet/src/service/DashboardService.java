package service;

import dao.DatabaseConnection;
import java.sql.*;
import java.util.*;

public class DashboardService {

    private final Connection conn;

    public DashboardService() throws SQLException {
        conn = DatabaseConnection.getConnection();
    }

    public int totalEtudiants() throws SQLException {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT COUNT(*) FROM etudiants WHERE actif=1")) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public int totalNotes() throws SQLException {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM notes")) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public Double moyenneGenerale() throws SQLException {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT AVG(valeur) FROM notes")) {
            if (rs.next()) { double v = rs.getDouble(1); return rs.wasNull() ? null : v; }
        }
        return null;
    }

    public Double tauxReussite() throws SQLException {
        String sql = "SELECT SUM(CASE WHEN valeur>=10 THEN 1 ELSE 0 END)"
                   + " * 100.0 / NULLIF(COUNT(*),0) FROM notes";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) { double v = rs.getDouble(1); return rs.wasNull() ? null : v; }
        }
        return null;
    }

    public Double noteMax() throws SQLException {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT MAX(valeur) FROM notes")) {
            if (rs.next()) { double v = rs.getDouble(1); return rs.wasNull() ? null : v; }
        }
        return null;
    }

    public Double noteMin() throws SQLException {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT MIN(valeur) FROM notes")) {
            if (rs.next()) { double v = rs.getDouble(1); return rs.wasNull() ? null : v; }
        }
        return null;
    }

    /** Rang, Prénom+Nom, Filière, Moyenne, Mention — top `limit` étudiants. */
    public List<Object[]> classement(int limit) throws SQLException {
        List<Object[]> list = new ArrayList<>();
        String sql =
            "SELECT e.prenom, e.nom, e.filiere, AVG(n.valeur) AS moy " +
            "FROM etudiants e JOIN notes n ON e.id = n.etudiant_id " +
            "WHERE e.actif=1 GROUP BY e.id,e.prenom,e.nom,e.filiere " +
            "ORDER BY moy DESC LIMIT ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            int rang = 1;
            while (rs.next()) {
                double moy = rs.getDouble("moy");
                list.add(new Object[]{
                    rang++,
                    rs.getString("prenom") + " " + rs.getString("nom"),
                    rs.getString("filiere"),
                    String.format("%.2f", moy),
                    mention(moy)
                });
            }
        }
        return list;
    }

    /** Filière | Étudiants | Moyenne | Admis | Échec */
    public List<Object[]> statsParFiliere() throws SQLException {
        List<Object[]> list = new ArrayList<>();
        String sql =
            "SELECT e.filiere, COUNT(DISTINCT e.id) AS nb_total, " +
            "  AVG(n.valeur) AS moy_filiere, " +
            "  COUNT(DISTINCT CASE WHEN sub.moy_etu >= 10 THEN e.id END) AS admis, " +
            "  COUNT(DISTINCT CASE WHEN sub.moy_etu <  10 THEN e.id END) AS echec " +
            "FROM etudiants e " +
            "LEFT JOIN notes n ON e.id = n.etudiant_id " +
            "LEFT JOIN (SELECT etudiant_id, AVG(valeur) AS moy_etu " +
            "           FROM notes GROUP BY etudiant_id) sub ON e.id = sub.etudiant_id " +
            "WHERE e.actif=1 GROUP BY e.filiere ORDER BY moy_filiere DESC";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                double moy = rs.getDouble("moy_filiere");
                list.add(new Object[]{
                    rs.getString("filiere"),
                    rs.getInt("nb_total"),
                    rs.wasNull() ? "—" : String.format("%.2f", moy),
                    rs.getInt("admis"),
                    rs.getInt("echec")
                });
            }
        }
        return list;
    }

    /** Semestre | Nb notes | Moyenne */
    public List<Object[]> statsParSemestre() throws SQLException {
        List<Object[]> list = new ArrayList<>();
        String sql =
            "SELECT c.semestre, COUNT(n.id) AS nb, AVG(n.valeur) AS moy " +
            "FROM cours c JOIN notes n ON c.id = n.cours_id " +
            "GROUP BY c.semestre ORDER BY c.semestre";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                double moy = rs.getDouble("moy");
                list.add(new Object[]{
                    rs.getString("semestre"),
                    rs.getInt("nb"),
                    rs.wasNull() ? "—" : String.format("%.2f", moy)
                });
            }
        }
        return list;
    }

    public static String mention(double moy) {
        if (moy >= 16) return "Très Bien";
        if (moy >= 14) return "Bien";
        if (moy >= 12) return "Assez Bien";
        if (moy >= 10) return "Passable";
        return "Insuffisant";
    }
}
