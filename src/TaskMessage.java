import javax.json.Json;
import javax.json.JsonObject;

public class TaskMessage extends DelayedMessage {

    public TaskMessage(Connection c, int delay, LinkList links) {
        super(c, delay);
        JsonObject encoding = Json.createObjectBuilder()
                .add("type", "TASK")
                .add("PercentageError", 100 * links.getCorruptionRate())
                .add("PercentageDrop", 100 * links.getDropRate())
                .add("randDelay", links.getDelay())
                .add("name", c.getHostname())
                .add("task", "TOPOLOGY")
                .build();
        setPayload(encoding);
    }

    public TaskMessage(Connection c, int delay, LinkList links,
            int recipient, String msg) {
        super(c, delay);
        JsonObject encoding = Json.createObjectBuilder()
                .add("type", "TASK")
                .add("PercentageError", 100 * links.getCorruptionRate())
                .add("PercentageDrop", 100 * links.getDropRate())
                .add("randDelay", links.getDelay())
                .add("task", "MESSAGE")
                .add("text", msg)
                .add("recipient", recipient)
                .build();
        setPayload(encoding);

    }

    public TaskMessage(Connection c, int delay, LinkList links,
            String unknown) {
        super(c, delay);
        JsonObject encoding = Json.createObjectBuilder()
                .add("type", "TASK")
                .add("PercentageError", 100 * links.getCorruptionRate())
                .add("PercentageDrop", 100 * links.getDropRate())
                .add("randDelay", links.getDelay())
                .add("task", "WHOIS")
                .add("other", unknown)
                .build();
        setPayload(encoding);

    }
}
