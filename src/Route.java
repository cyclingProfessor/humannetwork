import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.json.JsonObject;
import javax.swing.SwingUtilities;

public class Route extends Thread {

    private long startTime = -1;
    private boolean started = false;
    private ConnectionList connections;
    private PacketList messages;
    private LinkList links;
    private Random rand = new Random();
    List<DelayedMessage> queue = new ArrayList<DelayedMessage>();
    private int session;
    private static final int MIN_PER_NET = 8;
    private static final int MAX_PER_NET = 10;
    private Map<String, Network> cycles = new HashMap<String, Network>();

    public Route(ConnectionList connections, LinkList links,
            PacketList messages, int session) {
        this.connections = connections;
        this.messages = messages;
        this.links = links;
        this.session = session;
    }

    public void addConnection(BufferedOutputStream os, InputStream isr,
            InetAddress a) throws HandshakeException {
        final Connection c = new Connection(os, isr);

        int setNode = c.getNode();
        boolean keep = false;
        if (session == c.getSession() && setNode != 0) {
            // Loop through the connections and find the node number
            for (int i = 0; i < connections.size(); i++) {
                Connection cc = connections.get(i);
                if (cc.getNode() == setNode) {
                    // Keep old node Name
                    c.setHostname(cc.getHostname());
                    c.setNetwork(cc.getNetwork());
                    final int actualNode = i;
                    SwingUtilities.invokeLater(() -> {
                        // This is not a structural modification so will not
                        // cause Thread problems
                        connections.set(actualNode, c);
                    });
                    keep = true;
                    synchronized (queue) {
                        // add the appropriate status reply
                        queue.add(new ConnectMessage(c, -1, true));
                    }
                    break;
                }
            }
        }
        if (!keep) {
            if (started) {
                throw new HandshakeException(
                        "Cannot add new connections after the game has started");
            }
            c.setup(session);
            try {
                SwingUtilities.invokeAndWait(() -> {
                    connections.addElement(c);
                });
            } catch (InvocationTargetException | InterruptedException e) {
                throw new HandshakeException("Failed to add new connection");
            }
            (new ConnectMessage(c, -1, true)).send();
        }
    }

    private Connection getByNode(int node) {
        for (int i = 0; i < connections.size(); i++) {
            Connection c = connections.get(i);
            if (c.getNode() == node) {
                return c;
            }
        }
        return null;
    }

    private int nodeToIndex(int node) {
        int l = connections.size();
        for (int j = 0; j < l; j++) {
            if (connections.get(j).getNode() == node) {
                return j;
            }
        }
        return 0; // Should not happen
    }

    private void route(JsonObject message, int fromNode) {
        String text = message.getString("text", null);
        int from = message.getInt("from", -1);
        int to = message.getInt("to", -1);

        if (fromNode == from && to >= 0 && text != null) {
            SwingUtilities.invokeLater(() -> {
                messages.addPacket(from, to, text,
                        getByNode(from).getNetwork());
            });
            if (to == 0) {
                for (int i = 0; i < connections.size(); i++) {
                    Connection c = connections.get(i);
                    if (c != null && links.isNeighbour(fromNode, c.getNode())) {
                        send(c, text, from, to);
                    }
                }
            } else {
                Connection c = getByNode(to);
                if (c != null && links.isNeighbour(from, to)) {
                    send(c, text, from, to);
                }
            }
        }
    }

    private void send(Connection c, String message, int fromNode, int toNode) {
        int drop = rand.nextInt(100);
        if (drop >= links.getDropRate() || links.getDropRate() == 0) {
            String content = message;
            // TODO Change to only allowed matched responses.
            boolean shouldSend = (links.getOffset() == 0)
                    || (!links.isCheckwhois())
                    || ((content.length() > 12)
                            && (content.substring(0, 12).equals("WHOIS(Query,")
                                    || content.subSequence(0, 13)
                                            .equals("WHOIS(Answer,")));
            if (shouldSend) {
                // Corruption
                int corr = rand.nextInt(100);
                if (corr < links.getCorruptionRate()) {
                    content = Texts.corrupt(content);
                }
                // Network delay
                int delay = (links.getDelay() > 0)
                        ? rand.nextInt(links.getDelay()) : 0;
                synchronized (queue) {
                    queue.add(new PacketMessage(c, delay, fromNode, toNode,
                            content));
                }
            }
        }
    }

    public void sendNow(List<DelayedMessage> messageList) {
        for (int i = messageList.size() - 1; i >= 0; i--) {
            DelayedMessage m = messageList.get(i);
            if (m.ready()) {
                if (m.current(startTime)) {
                    m.send();
                }
                messageList.remove(i);
            } else {
                m.decr();
            }
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                synchronized (queue) {
                    sendNow(queue);
                }
                int l = connections.size();
                for (int i = 0; i < l; i++) {
                    Connection c = connections.get(i);
                    if (c != null && c.ready()) {
                        JsonObject message = c.read();
                        if (message != null) {
                            route(message, c.getNode());
                        }
                    }
                }
                Thread.sleep(1000L);
                // System.out.print('+');
            }
        } catch (InterruptedException e) {
            System.out.println("Closing server!");
        }
    }

    // This method can only be executed in the EDT so is safe from Connections
    // updates
    public void updateStatus() {
        // The first update triggers network building.
        if (!started) {
            started = true;
            int maxNets = Math.max(1, connections.size() / MIN_PER_NET);
            int numNets = Math.min(
                    (connections.size() + MAX_PER_NET - 1) / MAX_PER_NET,
                    maxNets);
            createCycles(numNets);
            // Begin to check for messages.
            synchronized (queue) {
                this.start();
            }
        }
        int l = connections.size();
        List<String> texts = null;
        if (links.getOffset() != 0 && !links.isCheckwhois()) {
            texts = new ArrayList<String>();
            Texts.choose_messages(texts, l, links.getCorruptionRate() > 0);
        }
        // mark any current messages out of date
        startTime = System.currentTimeMillis();

        synchronized (queue) {
            for (int i = 0; i < l; i++) {
                Connection c = connections.get(i);
                if (c != null) {
                    if (links.getOffset() == 0) {
                        queue.add(new TaskMessage(c, -1, links));
                    } else {
                        int recipient = cycles.get(c.getNetwork())
                                .offsetNode(c.getNode(), links.getOffset());
                        System.out.println("Node:" + c.getNode()
                                + "sending to: " + recipient);
                        if (links.isCheckwhois()) {
                            String unknown = connections
                                    .get(nodeToIndex(recipient)).getHostname();
                            queue.add(new TaskMessage(c, -1, links, unknown));
                        } else {
                            queue.add(new TaskMessage(c, -1, links, recipient,
                                    texts.get(i)));
                        }
                    }
                }
            }
        }
    }

    private void addLink(int from, int to, String network) {
        int nodeA = connections.get(from).getNode();
        int nodeB = connections.get(to).getNode();
        System.out.println("Create link between " + nodeA + " and " + nodeB);
        if (!links.isNeighbour(nodeA, nodeB)) {
            links.addElement(new Link(nodeA, nodeB, network));
        }
    }

    private void createCycles(int count) {
        int[] nodeIndexes = new int[connections.size()];
        for (int index = 0; index < connections.size(); index++) {
            nodeIndexes[index] = index;
        }
        // Shuffle the list of nodes.
        for (int index = connections.size() - 1; index > 0; index--) {
            int other = rand.nextInt(index);
            int temp = nodeIndexes[other];
            nodeIndexes[other] = nodeIndexes[index];
            nodeIndexes[index] = temp;
        }
        double netSize = connections.size() / count;
        int network = 1;
        int firstIndex = 0;
        double nextNetwork = netSize;
        String netName = "Network" + network;
        for (int index = 1; index <= netSize; index++) {
            connections.get(nodeIndexes[index - 1])
                    .setNetwork("Network" + network);
            if (index > nextNetwork - 0.5) {
                // We have finished all the nodes of a network.
                // Add link between previous node and "firstNode"
                // and store the network in "cycles"
                addLink(nodeIndexes[index - 1], nodeIndexes[firstIndex],
                        "Network" + network);
                firstIndex = index;
                cycles.put(netName, new Network(links, netName, connections));
                network++;
                netName = "Network" + network;
                nextNetwork = Math.ceil(network * netSize);
            } else {
                addLink(nodeIndexes[index - 1], nodeIndexes[index], netName);
            }
        }
    }
}
