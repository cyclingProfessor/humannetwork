import javax.swing.DefaultListModel;

public class PacketList extends DefaultListModel<Packet> {

    private static final long serialVersionUID = 6152415102958990076L;

    public PacketList() {
        super();
    }

    public void addPacket(int from, int to, String s, String net) {
        Packet m = new Packet(from, to, s, net);
        super.addElement(m);
    }
}
