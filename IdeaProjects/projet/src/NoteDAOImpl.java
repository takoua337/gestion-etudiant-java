import java.sql.*;
import java.util.*;

public class NoteDAOImpl implements INoteDAO {

    private Connection conn;

    public NoteDAOImpl() throws SQLException {
        this.conn = DatabaseConnection.getConnection();
    }

    @Override
    public void ajouter(Note n) throws SQLException {
        String sql = "INSERT INTO notes "
                + "(valeur, etudiant_id, cours_id, date_saisie)"
                + " VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps =
                     conn.prepareStatement(sql,
                             Statement.RETURN_GENERATED_KEYS)) {
            ps.setDouble(1, n.getValeur());
            ps.setInt(2, n.getEtudiantId());
            ps.setInt(3, n.getCoursId());
            ps.setString(4, n.getDateSaisie());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) n.setId(rs.getInt(1));
        }
    }

    @Override
    public List<Note> notesParEtudiant(int etudiantId)
            throws SQLException {
        List<Note> liste = new ArrayList<>();
        String sql = "SELECT * FROM notes WHERE etudiant_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, etudiantId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) liste.add(mapper(rs));
        }
        return liste;
    }

    @Override
    public double moyenneEtudiant(int etudiantId)
            throws SQLException {
        String sql = "SELECT AVG(valeur) FROM notes WHERE etudiant_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, etudiantId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        }
        return 0.0;
    }

    @Override
    public void modifier(Note n) throws SQLException {
        String sql = "UPDATE notes SET valeur=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, n.getValeur());
            ps.setInt(2, n.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM notes WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public List<Note> notesParCours(int coursId)
            throws SQLException {
        List<Note> liste = new ArrayList<>();
        String sql = "SELECT * FROM notes WHERE cours_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, coursId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) liste.add(mapper(rs));
        }
        return liste;
    }

    // ✅ AJOUT ICI — à l'intérieur de la classe
    public List<Note> listerParEtudiant(int etudiantId)
            throws SQLException {
        List<Note> liste = new ArrayList<>();
        String sql = "SELECT * FROM notes WHERE etudiant_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, etudiantId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Note n = new Note(
                        rs.getDouble("valeur"),
                        rs.getInt("etudiant_id"),
                        rs.getInt("cours_id"),
                        rs.getString("date_saisie")  // ✅ corrigé : date_saisie
                );
                n.setId(rs.getInt("id"));
                liste.add(n);
            }
        }
        return liste;
    }

    private Note mapper(ResultSet rs) throws SQLException {
        Note n = new Note();
        n.setId(rs.getInt("id"));
        n.setValeur(rs.getDouble("valeur"));
        n.setEtudiantId(rs.getInt("etudiant_id"));
        n.setCoursId(rs.getInt("cours_id"));
        return n;
    }
}  // ← accolade fermante de la classe