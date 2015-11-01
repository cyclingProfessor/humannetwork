public class DelayedMessage {

  @Override
  public String toString() {
    return "DelayedMessage [delay=" + delay + ", message=" + message
        + ", connection=" + connection + ", time=" + time + "]";
  }

  private int delay = 0;
  private String message = "";
  private Connection connection;
  private long time;

  public DelayedMessage(Connection connection, String message, int delay) {
    this.delay = delay;
    this.message = message;
    this.connection = connection;
    time = System.currentTimeMillis();
  }

  public void decr() {
    delay--;
  }

  public boolean ready() {
    return delay < 0;
  }

  public void send() {
    // System.out.println("About to send the message: " + message);
    connection.write(message);
  }

  public boolean current(long startTime) {
    return time >= startTime;
  }
}
