import javax.json.JsonObject;

public class DelayedMessage {
    private int delay = 0;
    private JsonObject payload;
    private Connection connection;
    private long time;

    public DelayedMessage(Connection connection, JsonObject payload, int delay) {
        this.delay = delay;
        this.connection = connection;
        time = System.currentTimeMillis();
        this.payload = payload;
    }

    protected DelayedMessage(Connection connection, int delay) {
        this.delay = delay;
        this.connection = connection;
        time = System.currentTimeMillis();
    }
    
    public void setPayload(JsonObject payload) {
        this.payload = payload;
    }

    public JsonObject getPayload() {
        return payload;
    }

    public void decr() {
        delay--;
    }

    public boolean ready() {
        return delay < 0;
    }

    public void send() {
        connection.write(payload);
    }

    public boolean current(long startTime) {
        return time >= startTime;
    }

    public String toString() {
      return 
          "Delay:" + delay +
          "\nPayload:" +  payload +
          "Connection:" + connection;
    }
    public void setConnection(Connection cc) {
        connection = cc;
    }
}
