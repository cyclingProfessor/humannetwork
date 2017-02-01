import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Network {
    private static Map<String,String> colors = new HashMap<String,String>();
    // Begin with connections and links
    // 1.Build graph
    // 2.Find Hamiltonian for each network
    private ArrayList<Integer> groupNodes;
    private LinkList links;
    private int nodeCount;
    private boolean isHamiltonian;
    private int[] next, previous;
    private static final String[] COLORS = {"black", "red", "green", "blue"};
    private static int netNum = 0;

    public Network(LinkList links, String group, ConnectionList connections) {
        this.links = links;

        groupNodes = new ArrayList<Integer>();
        for (int index = 0; index < connections.size(); index++) {
            Connection conn = connections.get(index);
            if (group.equals(conn.getNetwork())) {
                groupNodes.add(conn.getNode());
            }
        }
        nodeCount = groupNodes.size();
        next = new int[nodeCount];
        previous = new int[nodeCount];
        isHamiltonian = build_hamiltonian_cycle();
        if (isHamiltonian) {
            for (int index = 0; index < nodeCount; index++) {
                previous[next[index]] = index;
            }
        }
        addColour(group, COLORS[netNum++]);
    }

    public static void addColour(String net, String htmlColor){
        colors.put(net, htmlColor);
    }
    public static String getColor(String net) {
        return colors.getOrDefault(net, "black");
    }
    private boolean build_hamiltonian_cycle() {
        if (nodeCount < 3) {
            return false;
        }
        Arrays.fill(next, -1);
        for (int lastIndex = 1; lastIndex < nodeCount; lastIndex++) {
            if (links.isNeighbour(groupNodes.get(0), groupNodes.get(lastIndex))) {
                next[lastIndex] = 0;
                if (hamFromTo(nodeCount, 0, lastIndex)) {
                    return true;
                }
                next[lastIndex] = -1;
            }
        }
        return false;
    }

    private boolean hamFromTo(int nodesLeft, int u, int v) {
        if (nodesLeft == 2) {
            if (links.isNeighbour(groupNodes.get(u), groupNodes.get(v))) {
                next[u] = v;
                return true;
            }
            return false;
        }

        for (int i = 1; i < nodeCount; i++) {
            if (i != v && next[i] == -1) {
                if (links.isNeighbour(groupNodes.get(u), groupNodes.get(i))) {
                    next[u] = i;
                    if (hamFromTo(nodesLeft - 1, i, v)) {
                        return (true);
                    }
                    next[u] = -1;
                }
            }
        }
        return (false);
    }

    public boolean hamiltonian() {
        return isHamiltonian;
    }

    public int offsetNode(int node, int count) {
        // step around Hamiltonian circuit (skipping self)
        int[] stepper;
        int start = groupNodes.indexOf(node);
        int index = start;
        if (start == -1 || count == 0) {
            return node;
        } else if (count > 0) {
            stepper = next;
        } else {
            stepper = previous;
            count = -count;
        }
        while (count > 0) {
            index = stepper[index];
            if (index != start) {
                count = count - 1;
            }
        }
        return groupNodes.get(index);
    }
}