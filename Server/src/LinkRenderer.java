import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;


public class LinkRenderer extends JLabel implements ListCellRenderer<Link> {

    /**
     * Required field for any serializable class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor makes the background of each returned Jlabel opaque.
     */
    public LinkRenderer() {
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
    // each network has a color - obtained from the Network  class

    public Component getListCellRendererComponent(JList<? extends Link> list,
            Link link, int index, boolean isSelected, boolean cellHasFocus) {
        String net = link.getNetwork();
        setText("<html> <font color="
                + Network.getColor(net)
                + ">"
                + link.getNodeA()
                + " &lt;-"
                + net
                + "-&gt; "
                + link.getNodeB()
                + "</font></html>");
        return this;
    }

}
