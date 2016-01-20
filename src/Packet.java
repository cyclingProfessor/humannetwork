import javax.json.Json;
import javax.json.JsonObject;

public class Packet {
    private int from;
    private int to;
    private String text;
    private String network;
    public Packet(int from, int to, String text, String network) {
        super();
        this.from = from;
        this.to = to;
        this.text = text;
        this.network = network;
    }
    public int getFrom() {
        return from;
    }
    public int getTo() {
        return to;
    }
    public String getText() {
        return text;
    }
    public String getNetwork() {
        return network;
    }
  
}
