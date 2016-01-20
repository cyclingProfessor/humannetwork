import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
// The WebSocket code is modified from Anders (Andak Stackexchange, akre.it, - Christopher Price
// Modified by Dave Cohen (cyclingProfessor on GitHub)

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonWriter;

// The WebSocket used here can only accept text data and never longer than 125 bytes of payload
// Possible future modification to accept fragmented frmes, PING, PONG and long payload data.

public class Connection {

    private Socket connection;
    private String hostname = null;
    private String network = null;
    private int node = 0;

    private BufferedOutputStream osw;
    private InputStream isr;
    private static final int SINGLE_FRAME_UNMASKED = 0x81;
    private static final int MASK_SIZE = 4;

    /**
     * Constructor for the Server
     *  
     * @param osw
     * @param isr
     */
    public Connection(BufferedOutputStream osw, InputStream isr)
            throws HandshakeException {
        this.isr = isr;
        this.osw = osw;
        doHandshake();
    }

    public String getHostname() {
        return hostname;
    }

    public void pickName() {
        this.hostname = Texts.choose_name(node);
    }

    public void setNode(int node) {
        this.node = node;
    }

    /**
     * Sends a message through the connection
     * 
     * @param msg
     *            the message
     * @return true if the write is successful
     */

    public boolean write(JsonObject msg) {
        boolean result = true;
        StringWriter sw = new StringWriter();
        JsonWriter writer = Json.createWriter(sw);
        writer.writeObject(msg);
        byte[] msgBytes = sw.toString().getBytes();
        try {
            osw.write(SINGLE_FRAME_UNMASKED);
            osw.write(msgBytes.length);
            osw.write(msgBytes);
            osw.flush();
        } catch (IOException f) {
            System.err.println("IOException: " + f);
            result = false;
        }
        return result;
    }

    /**
     * Reads a message from that connection
     * 
     * @return the message read (empty if something fails)
     */

    public JsonObject read() {
        JsonObject retval = null;
        try {
            byte[] buf = readBytes(2);
            if ((buf[0] & 0x0F) != 8) { // Client does not want to close
                                        // connection!
                buf = readBytes(MASK_SIZE + ((buf[1] & 0xFF) - 0x80));
                String message = unMask(Arrays.copyOfRange(buf, 0, 4),
                        Arrays.copyOfRange(buf, 4, buf.length));
                JsonReader jsonReader = Json.createReader(new StringReader(
                        message));
                retval = jsonReader.readObject();
                jsonReader.close();
            }
        } catch (IOException e) {
        }
        return retval;
    }

    private byte[] readBytes(int numOfBytes) throws IOException {
        byte[] b = new byte[numOfBytes];
        isr.read(b);
        return b;
    }

    private static String unMask(byte[] mask, byte[] data) {
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) (data[i] ^ mask[i % mask.length]);
        }
        return new String(data);
    }

    /**
     * Checks if there is an incoming message.
     * 
     * @return true if there is.
     */
    public boolean ready() {
        try {
            return isr.available() > 0;
        } catch (IOException e) {
            System.err.println("IOException: " + e);
            return false;
        }
    }

    /**
     * Close the connection
     */
    public void close() {
        try {
            connection.close();
        } catch (IOException e) {
            System.err.println("IOException: " + e);
        }
    }

    public String toString() {
        return ("[" + network + "] " + node);
    }

    public void setNetwork(String id) {
        network = id;
    }

    public int getNode() {
        return node;
    }

    public String getNetwork() {
        return network;
    }

    public void setHostname(String name) {
        hostname = name;
    }

    private void doHandshake() throws HandshakeException {
        BufferedReader in = new BufferedReader(new InputStreamReader(isr));
        HashMap<String, String> keys = new HashMap<>();
        String str;
        // Reading client handshake

        try {
            while (!(str = in.readLine()).equals("")) {
                String[] s = str.split(": ");
                if (s.length == 2) {
                    keys.put(s[0], s[1]);
                }
            }
        } catch (IOException e) {
            throw new HandshakeException("Read failed during handshake");
        }

        String hash = "";
        try {
            hash = Base64
                    .getEncoder()
                    .encodeToString(
                            MessageDigest
                                    .getInstance("SHA-1")
                                    .digest((keys.get("Sec-WebSocket-Key") + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11")
                                            .getBytes()));
        } catch (NoSuchAlgorithmException ex) {
            throw new HandshakeException("No Such Algorithm");
        }

        // Write handshake response
        PrintWriter out = new PrintWriter(osw);

        out.write("HTTP/1.1 101 Switching Protocols\r\n"
                + "Upgrade: websocket\r\n" + "Connection: Upgrade\r\n"
                + "Sec-WebSocket-Accept: " + hash + "\r\n"
                + "Origin: http://HumanNetwork.com\r\n" + "\r\n");

        out.flush();

        // Check that the first message is a HELLO message
        JsonObject message = read();
        String type = message.getString("type");
        if (!type.equals("HELLO")) {
            close();
            throw new HandshakeException("Invalid client - no session message");
        }
        node = message.getInt("node");
    }
}
