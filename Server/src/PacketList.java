import java.util.ArrayList;

import javax.swing.AbstractListModel;

public class PacketList extends AbstractListModel<Packet> {

    private static final long serialVersionUID = 6152415102958990076L;
    private int filterNode = 0; // 0 means ALL
    private ArrayList<Packet> items = new ArrayList<Packet>();
    private ArrayList<Packet> filteredList = new ArrayList<Packet>();

    public void addPacket(int from, int to, String s, String net) {
        Packet m = new Packet(from, to, s, net);
        addItem(m);
    }

    public void setFilter(int node) {
        this.filterNode = node;
        filteredList.clear();
        for (int i = 0; i < items.size(); i++) {
            Packet packet = items.get(i);
            if (nMatch(node, packet)) {
                filteredList.add(packet);
            }
        }
        this.fireContentsChanged(this, 0, filteredList.size() - 1);
    }

    public void removeFilter() {
        setFilter(0);
    }

    public int getFilter() {
        return filterNode;
    }

    private static boolean nMatch(int filter, Packet p) {
        return filter == 0 || filter == p.getTo() || filter == p.getFrom();
    }

    public void addItem(Packet packet) {
        items.add(packet);
        if (nMatch(filterNode, packet)) {
            filteredList.add(packet);
            fireIntervalAdded(this, filteredList.size() - 2,
                    filteredList.size() - 1);
        }
    }

    public boolean removeItem(Packet packet) {
        // Do nothing as this cannot happen
        return false;
    }

    public void removeItem(int index) {
        // Do nothing as this cannot happen
    }

    public int getSize() {
        return filterNode == 0 ? items.size() : filteredList.size();
    }

    public int size() {
        return getSize();
    }

    public Packet getElementAt(int index) {
        return filteredList.get(index);
    }
}
