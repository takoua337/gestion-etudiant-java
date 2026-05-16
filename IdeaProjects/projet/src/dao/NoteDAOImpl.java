package dao;

import model.Note;
import java.sql.*;
import java.util.*;

public class NoteDAOImpl implements INoteDAO {

    private final Connection conn;

    public NoteDAOImpl() throws SQLException {
        conn = DatabaseConnection.getConnection();
    }

    @Override
    public void ajouter(Note n) throws SQLException {
        String sql = "INSERT INTO notes (valeur,etudiant_id,cours_id,date_saisie)"
                   + " VALUES (?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDouble(1, n.getValeur());
            ps.setInt   (2, n.getEtudiantId());
            ps.setInt   (3, n.getCoursId());
            ps.setString(4, n.getDateSaisie());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) n.setId(rs.getInt(1));
        }
    }

    @Override
    public List<Note> notesParEtudiant(int etudiantId) throws SQLException {
        List<Note> liste = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM notes WHERE etudiant_id=?")) {
            ps.setInt(1, etudiantId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) liste.add(mapper(rs));
        }
        return liste;
    }

    @Override
    public List<Note> notesParCours(int coursId) throws SQLException {
        List<Note> liste = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM notes WHERE cours_id=?")) {
            ps.setInt(1, coursId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) liste.add(mapper(rs));
        }
        return liste;
    }

    @Override
    public double moyenneEtudiant(int etudiantId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT AVG(valeur) FROM notes WHERE etudiant_id=?")) {
            ps.setInt(1, etudiantId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        }
        return 0.0;
    }

    @Override
    public void modifier(Note n) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE notes SET valeur=? WHERE id=?")) {
            ps.setDouble(1, n.getValeur());
            ps.setInt   (2, n.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM notes WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /** Notes avec noms d'étudiant et cours (JOIN), filtrables. */
    public List<Object[]> listerAvecDetails(Integer etudiantId, Integer coursId)
            throws SQLException {
        List<Object[]> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT n.id, CONCAT(e.prenom,' ',e.nom) AS etudiant, " +
            "c.intitule AS cours, n.valeur, n.date_saisie " +
            "FROM notes n " +
            "JOIN etudiants e ON n.etudiant_id = e.id " +
            "JOIN cours c    ON n.cours_id     = c.id WHERE 1=1");
        List<Integer> params = new ArrayList<>();
        if (etudiantId != null) { sql.append(" AND n.etudiant_id=?"); params.add(etudiantId); }
        if (coursId    != null) { sql.append(" AND n.cours_id=?");    params.add(coursId); }
        sql.append(" ORDER BY n.id DESC");
        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setInt(i + 1, params.get(i));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getInt   ("id"),
                    rs.getString("etudiant"),
                    rs.getString("cours"),
                    String.format("%.2f", rs.getDouble("valeur")),
                    rs.getString("date_saisie")
                });
            }
        }
        return list;
    }

    private Note mapper(ResultSet rs) throws SQLException {
        Note n = new Note();
        n.setId        (rs.getInt   ("id"));
        n.setValeur    (rs.getDouble("valeur"));
        n.setEtudiantId(rs.getInt   ("etudiant_id"));
        n.setCoursId   (rs.getInt   ("cours_id"));
        n.setDateSaisie(rs.getString("date_saisie"));
        return n;
    }
}
