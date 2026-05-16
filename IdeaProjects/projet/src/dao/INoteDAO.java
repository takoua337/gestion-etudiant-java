package dao;

import model.Note;
import java.util.List;

public interface INoteDAO {
    void        ajouter(Note n)                       throws Exception;
    List<Note>  notesParEtudiant(int etudiantId)      throws Exception;
    List<Note>  notesParCours(int coursId)            throws Exception;
    double      moyenneEtudiant(int etudiantId)       throws Exception;
    void        modifier(Note n)                      throws Exception;
    void        supprimer(int id)                     throws Exception;
}
