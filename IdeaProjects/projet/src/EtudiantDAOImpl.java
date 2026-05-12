import java.sql.*;
import java.util.*;

public class EtudiantDAOImpl implements IEtudiantDAO {

    private Connection conn;

    public EtudiantDAOImpl() throws SQLException {
        this.conn = DatabaseConnection.getConnection();
    }

    @Override
    public void ajouter(Etudiant e) throws SQLException {
        if (emailExiste(e.getEmail()))
            throw new IllegalArgumentException(
                    "Email deja utilise : " + e.getEmail());

        String sql = "INSERT INTO etudiants "
                + "(nom, prenom, filiere, email, date_naissance)"
                + " VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getNom());
            ps.setString(2, e.getPrenom());
            ps.setString(3, e.getFiliere());
            ps.setString(4, e.getEmail());
            ps.setDate(5, e.getDateNaissance());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) e.setId(rs.getInt(1));
        }
    }

    @Override
    public Etudiant trouverParId(int id) throws SQLException {
        String sql = "SELECT * FROM etudiants WHERE id=?";
        try (PreparedStatement ps =
                     conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapper(rs);
        }
        return null;
    }

    @Override
    public List<Etudiant> listerTous() throws SQLException {
        List<Etudiant> liste = new ArrayList<>();
        String sql = "SELECT * FROM etudiants WHERE actif=1";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) liste.add(mapper(rs));
        }
        return liste;
    }

    @Override
    public List<Etudiant> rechercherParFiliere(
            String filiere) throws SQLException {
        List<Etudiant> liste = new ArrayList<>();
        String sql = "SELECT * FROM etudiants "
                + "WHERE filiere=? AND actif=1";
        try (PreparedStatement ps =
                     conn.prepareStatement(sql)) {
            ps.setString(1, filiere);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) liste.add(mapper(rs));
        }
        return liste;
    }

    @Override
    public void modifier(Etudiant e) throws SQLException {
        String sql = "UPDATE etudiants SET nom=?, "
                + "prenom=?, filiere=?, email=? WHERE id=?";
        try (PreparedStatement ps =
                     conn.prepareStatement(sql)) {
            ps.setString(1, e.getNom());
            ps.setString(2, e.getPrenom());
            ps.setString(3, e.getFiliere());
            ps.setString(4, e.getEmail());
            ps.setInt(5, e.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void supprimerLogique(int id)
            throws SQLException {
        String sql = "UPDATE etudiants "
                + "SET actif=0 WHERE id=?";
        try (PreparedStatement ps =
                     conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public void supprimerPhysique(int id)
            throws SQLException {
        String sql = "DELETE FROM etudiants WHERE id=?";
        try (PreparedStatement ps =
                     conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public boolean emailExiste(String email)
            throws SQLException {
        String sql = "SELECT COUNT(*) FROM etudiants "
                + "WHERE email=?";
        try (PreparedStatement ps =
                     conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    private Etudiant mapper(ResultSet rs)
            throws SQLException {
        Etudiant e = new Etudiant();
        e.setId(rs.getInt("id"));
        e.setNom(rs.getString("nom"));
        e.setPrenom(rs.getString("prenom"));
        e.setFiliere(rs.getString("filiere"));
        e.setEmail(rs.getString("email"));
        e.setActif(rs.getBoolean("actif"));
        return e;
    }
}