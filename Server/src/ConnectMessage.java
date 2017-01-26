import javax.json.Json;
import javax.json.JsonObject;

public class ConnectMessage extends DelayedMessage {
    public ConnectMessage(Connection c, int delay, boolean keep) {
        super(c, delay);
        JsonObject encoding = Json.createObjectBuilder()
                .add("type", "CONNECTED")
                .add("node", c.getNode())
                .add("session", c.getSession())
                .add("name", c.getHostname())
                .add("keep", keep)
                .build();
        setPayload(encoding);
    }
}
