import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class PacketRenderer extends JLabel implements ListCellRenderer<Packet> {
    /**
     * Required field for any serializable class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor makes the background of each returned Jlabel opaque.
     */
    public PacketRenderer() {
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
            final JList<? extends Packet> list,
            final Packet item,
            final int index,
            final boolean isSelected,
            final boolean cellHasFocus) {

        if (item.getTo() == 0) {
            setBackground(Color.PINK);
        } else {
            setBackground(Color.GRAY);
        }
        setText("<html> <font color="
                + Network.getColor(item.getNetwork())
                + ">"
                + item.getFrom()
                + "-&gt;"
                + item.getTo()
                + "<strong> ("
                + item.getText()
                + ") </strong></font></html>");
        return this;
    }
}
