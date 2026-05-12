import java.util.List;

public class BulletinService {

    private EtudiantDAOImpl etudiantDao;
    private NoteDAOImpl     noteDao;

    public BulletinService(EtudiantDAOImpl etudiantDao,
                           NoteDAOImpl noteDao) {
        this.etudiantDao = etudiantDao;
        this.noteDao     = noteDao;
    }

    public EtudiantDAOImpl getEtudiantDao() { return etudiantDao; }
    public NoteDAOImpl     getNoteDao()     { return noteDao; }

    public Etudiant getEtudiant(int id) throws Exception {
        return etudiantDao.trouverParId(id);
    }

    public List<Note> getNotesEtudiant(int etudiantId)
            throws Exception {
        return noteDao.listerParEtudiant(etudiantId);
    }
}