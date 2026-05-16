package dao;

import model.Cours;
import java.sql.*;
import java.util.*;

public class CoursDAOImpl implements ICoursDAO {

    private final Connection conn;

    public CoursDAOImpl() throws SQLException {
        conn = DatabaseConnection.getConnection();
    }

    @Override
    public void ajouter(Cours c) throws SQLException {
        String sql = "INSERT INTO cours (intitule,semestre,capacite,nb_inscrits) VALUES (?,?,?,0)";
        try (PreparedStatement ps = conn.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getIntitule());
            ps.setString(2, c.getSemestre());
            ps.setInt   (3, c.getCapacite());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) c.setId(rs.getInt(1));
        }
    }

    @Override
    public Cours trouverParId(int id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM cours WHERE id=?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapper(rs);
        }
        return null;
    }

    @Override
    public List<Cours> listerTous() throws SQLException {
        List<Cours> liste = new ArrayList<>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT * FROM cours ORDER BY semestre, intitule")) {
            while (rs.next()) liste.add(mapper(rs));
        }
        return liste;
    }

    @Override
    public List<Cours> rechercherParSemestre(String semestre) throws SQLException {
        List<Cours> liste = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM cours WHERE semestre=?")) {
            ps.setString(1, semestre);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) liste.add(mapper(rs));
        }
        return liste;
    }

    @Override
    public void modifier(Cours c) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE cours SET intitule=?,semestre=?,capacite=? WHERE id=?")) {
            ps.setString(1, c.getIntitule());
            ps.setString(2, c.getSemestre());
            ps.setInt   (3, c.getCapacite());
            ps.setInt   (4, c.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM cours WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Cours mapper(ResultSet rs) throws SQLException {
        Cours c = new Cours();
        c.setId        (rs.getInt   ("id"));
        c.setIntitule  (rs.getString("intitule"));
        c.setSemestre  (rs.getString("semestre"));
        c.setCapacite  (rs.getInt   ("capacite"));
        c.setNbInscrits(rs.getInt   ("nb_inscrits"));
        return c;
    }
}
