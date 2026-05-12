import java.util.List;

public interface ICoursDAO {

    void ajouter(Cours c) throws Exception;

    Cours trouverParId(int id) throws Exception;

    List<Cours> listerTous() throws Exception;

    List<Cours> rechercherParSemestre(String semestre)
            throws Exception;

    void modifier(Cours c) throws Exception;

    void supprimer(int id) throws Exception;
}