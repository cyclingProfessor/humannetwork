import java.awt.EventQueue;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

public class Server {

    private static Route route;

    static Socket socket;
    static ServerSocket ssocket;
    static int port = 10000;
    final static int MAX_SESSION = 1000000;
    final static int HANDSHAKE_WAIT_TIME = 200; // Number of milliseconds to wait when
                                          // accepting a connection

    /**
     * Listen to incoming connections
     * 
     * @param port
     *            server port
     */
    public static void listen(final InetAddress address, final int port) {
        Thread listen = new Thread() {
            public void run() {
                try {
                    int backlog = 0;
                    ssocket = new ServerSocket(port, backlog, address);
                    System.out.println("DarkNet Initialised on " + address
                            + ":" + port);
                    while (true) {
                        socket = ssocket.accept();
                        String id = socket.getInetAddress().toString() + ":"
                                + socket.getPort();
                        System.out.println("Accepted Connection from " + id);
                        BufferedOutputStream os = new BufferedOutputStream(
                                socket.getOutputStream());
                        socket.setSoTimeout(HANDSHAKE_WAIT_TIME);
                        try {
                        route.addConnection(os, socket.getInputStream(),
                                socket.getInetAddress());
                        } catch (HandshakeException e) {
                            System.out.println("Failed to start connection for "+ id + ": " + e.getMessage());
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Failed to get Server Socket.  Closing server ... ");
                    System.exit(-1);
                }
            }
        };
        listen.start();
    }

    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        OptionParser parser = new OptionParser();
        parser.accepts("?", "Print this help message").forHelp();
        parser.accepts("h").withRequiredArg().defaultsTo("127.0.0.1")
                .describedAs("The name of this WebSocket host");
        OptionSpec<Integer> portNumber = parser.accepts("p").withRequiredArg()
                .ofType(Integer.class).defaultsTo(10000)
                .describedAs("Port to Listen on");
        boolean helpMessage = false;
        OptionSet options = null;
        InetAddress address = null;
        int port = 0;
        try {
            options = parser.parse(args);
            address = InetAddress.getByName((String) options.valueOf("h"));
            port = options.valueOf(portNumber);
        } catch (Exception e) {
            helpMessage = true;
        }
        if (helpMessage || options.has("?")) {
            parser.printHelpOn(System.out);
            System.exit(1);
        }
        final ConnectionList connections = new ConnectionList();
        final LinkList links = new LinkList();
        final PacketList messages = new PacketList();
        // TODO make the session work and be recoverable
        Random rand = new Random(); int session = rand.nextInt(MAX_SESSION);
        route = new Route(connections, links, messages, session);
        listen(address, port);
        final ServerController controller = new ServerController(messages,
                links, connections, route);
        final ServerGui window = new ServerGui();

        final int actualPort = port;
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    window.initialize(actualPort, connections, links, messages,
                            controller);
                    window.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

}
