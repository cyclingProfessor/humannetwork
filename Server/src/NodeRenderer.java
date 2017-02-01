import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;


public class NodeRenderer extends JLabel implements ListCellRenderer<Connection> {
    /**
     * Required field for any serializable class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor makes the background of each returned Jlabel opaque.
     */
    public NodeRenderer() {
        setOpaque(true);
    }

    /**
     * Renders the current item in a Jlist of Items.
     *
     * @param list the displayed list of items
     * @param item the current item to render
     * @param index the index of the item in the list
     * @param isSelected whether the item is currently selected
     * @param cellHasFocus whether the item has the mouse focus
     * @return the JLabel used to render this item
     */
    @Override
    public Component getListCellRendererComponent(
            JList<? extends Connection> list, Connection node, int index,
            boolean isSelected, boolean cellHasFocus) {

        setOpaque(true);
        if (isSelected) {
            setBackground(Color.YELLOW);
        } else {
            setBackground(Color.WHITE);
        }
        setText("<html> <font color="
                + Network.getColor(node.getNetwork())
                + "> <strong>"
                + node.getNode()
                + "</strong> ("
                + node.getHostname()
                + ") </font></html>");
        return this;
    }

}
