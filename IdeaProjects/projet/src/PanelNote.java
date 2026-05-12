import javax.swing.*;
import java.awt.*;

public class PanelNote extends JPanel {

    private NoteDAOImpl dao;
    private JTextField tfEtudiantId, tfCoursId, tfValeur;

    public PanelNote(NoteDAOImpl dao) {
        this.dao = dao;
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));

        JPanel form = new JPanel(new GridLayout(4, 2, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Ajouter une Note"));
        form.setPreferredSize(new Dimension(350, 160));

        tfEtudiantId = new JTextField();
        tfCoursId    = new JTextField();
        tfValeur     = new JTextField();
        JButton btnAjouter = new JButton("Ajouter la note");

        form.add(new JLabel("ID Etudiant :")); form.add(tfEtudiantId);
        form.add(new JLabel("ID Cours :"));    form.add(tfCoursId);
        form.add(new JLabel("Note /20 :"));    form.add(tfValeur);
        form.add(new JLabel(""));               form.add(btnAjouter);

        btnAjouter.addActionListener(e -> {
            try {
                int    eid = Integer.parseInt(tfEtudiantId.getText());
                int    cid = Integer.parseInt(tfCoursId.getText());
                double val = Double.parseDouble(tfValeur.getText());
                Note n = new Note(val, eid, cid,
                        java.time.LocalDate.now().toString());
                dao.ajouter(n);
                JOptionPane.showMessageDialog(this,
                        "Note ajoutée (id=" + n.getId() + ")");
                tfEtudiantId.setText("");
                tfCoursId.setText("");
                tfValeur.setText("");
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Valeurs invalides !");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
            }
        });

        add(form);
    }
}