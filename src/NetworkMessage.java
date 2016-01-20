import javax.json.Json;
import javax.json.JsonObject;

public class NetworkMessage extends DelayedMessage {
    private int from = -1;
    private int to = -1;
    private String text = null;
    
    public NetworkMessage(Connection c, int delay, int from, int to, String text) {
        super(c, delay);
        this.from = from;
        this.to = to;
        this.text = text;
        JsonObject representation = Json.createObjectBuilder()
                .add("type", "MESSAGE")
                .add("from", from)
                .add("to", to)
                .add("text", text)
                .build();
        setPayload(representation);
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String toString() {
        String fromS = "" + from;
        String toS = (to == 0) ? "all" : "" + to;
        return ("From " + fromS + " to " + toS + ": " + text);
    }
}
