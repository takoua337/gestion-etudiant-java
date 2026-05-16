package util;

/** Generic wrapper for JComboBox items — displays a label but carries an int id. */
public class ComboItem {

    private final int    id;
    private final String label;

    public ComboItem(int id, String label) {
        this.id    = id;
        this.label = label;
    }

    public int getId() { return id; }

    @Override
    public String toString() { return label; }
}
