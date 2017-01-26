public class Link {

    private int nodeA;
    private int nodeB;
    private String network;

    public Link(int nodeA, int nodeB, String group) {
        this.nodeA = nodeA;
        this.nodeB = nodeB;
        this.network = group;
    }

    public int getNodeA() {
        return nodeA;
    }

    public void setNodeA(int nodeA) {
        this.nodeA = nodeA;
    }

    public int getNodeB() {
        return nodeB;
    }

    public void setNodeB(int nodeB) {
        this.nodeB = nodeB;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String group) {
        this.network = group;
    }

    @Override
    public String toString() {
        return "[" + network + "] " + nodeA + " <-> " + nodeB;
    }
}
