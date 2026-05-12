import java.util.List;

public interface IEtudiantDAO {

    void ajouter(Etudiant e) throws Exception;

    Etudiant trouverParId(int id) throws Exception;

    List<Etudiant> listerTous() throws Exception;

    List<Etudiant> rechercherParFiliere(String filiere)
            throws Exception;

    void modifier(Etudiant e) throws Exception;

    void supprimerLogique(int id) throws Exception;

    void supprimerPhysique(int id) throws Exception;

    boolean emailExiste(String email) throws Exception;
}