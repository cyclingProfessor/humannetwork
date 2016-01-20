import javax.json.Json;
import javax.json.JsonObject;

public class Packet {
    private int from;
    private int to;
    private String text;
    public Packet(int from, int to, String text) {
        super();
        this.from = from;
        this.to = to;
        this.text = text;
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
  
}
