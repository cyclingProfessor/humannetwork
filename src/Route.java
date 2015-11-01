import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.SwingUtilities;

public class Route extends Thread {

  private long startTime = -1;
  private ConnectionList connections;
  private MessageList messages;
  private LinkList links;
  private Random rand = new Random();
  private List<DelayedMessage> queue = new ArrayList<DelayedMessage>();
  List<DelayedMessage> statusMessages = new ArrayList<DelayedMessage>();

  public Route(ConnectionList connections, LinkList links,
      MessageList messages) {
    this.connections = connections;
    this.messages = messages;
    this.links = links;
  }

  public void addConnection(OutputStreamWriter osw, InputStreamReader isr,
      InetAddress a) {
    final Connection c = new Connection(osw, isr);
    String id = c.read();
    int setnode = Integer.parseInt(c.read());
    if (setnode == 0) {
      byte[] address = a.getAddress();
      setnode = address[2] + 257 + address[3];
    }
    boolean found = false;
    synchronized (connections) {
      for (int i = 0; i < connections.size(); i++) {
        Connection cc = connections.get(i);
        System.out.println("Checking " + cc.getGroup() + "/" + cc.getNode());
        if (cc.getNode() == setnode) {
          c.setNode(cc.getNode());
          System.out.println(id + " is playing existing node " + c.getNode());
          c.write("" + c.getNode());
          // Keep old node Name
          c.setHostname(cc.getHostname());
          final int actualNode = i;
          SwingUtilities.invokeLater(() -> {
            synchronized (connections) {
              connections.set(actualNode, c);
            }
          });
          found = true;
          break;
        }
      }
    }
    c.setGroup(id);
    if (!found) {
      int node = setnode;
      c.setNode(node);
      c.write("" + node);
      c.pickName();
      System.out.println(id + " is playing new node " + node + " with name: "
          + c.getHostname());
      SwingUtilities.invokeLater(() -> {
        synchronized (connections) {
          connections.addElement(c);
        }
      });
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

  private void route(String message, int fromNode) {
    String[] pieces = message.split(String.valueOf((char) 13));
    if (pieces.length != 2) {
      // Silently drop the message
      SwingUtilities.invokeLater(() -> {
        messages.addMessage(
            "From " + fromNode + ": " + message + "dropped(ill-formed)");
      });
      System.out.println("Ill-formed message");
    } else {
      int toNode = Integer.parseInt(pieces[0]);
      SwingUtilities.invokeLater(() -> {
        messages.addMessage(fromNode, toNode, message);
      });
      if (toNode == 0) {
        for (int i = 0; i < connections.size(); i++) {
          Connection c = connections.get(i);
          if (links.isNeighbour(fromNode, c.getNode())) {
            send(c, pieces[1], fromNode, toNode);
          }
        }
      } else {
        Connection c = getByNode(toNode);
        if (c != null && links.isNeighbour(fromNode, toNode)) {
          send(c, pieces[1], fromNode, toNode);
        }
      }
    }
  }

  private void send(Connection c, String message, int fromNode, int toNode) {
    int drop = rand.nextInt(100);
    if (drop >= links.getDropRate() || links.getDropRate() == 0) {
      String content = message;
      // TODO Change to only allowed matched responses.
      boolean shouldSend = (links.getOffset() == 0) || (!links.isCheckwhois())
          || ((content.length() > 12)
              && (content.substring(0, 12).equals("WHOIS(Query,")
                  || content.subSequence(0, 13).equals("WHOIS(Answer,")));
      if (shouldSend) {
        // Corruption
        int corr = rand.nextInt(100);
        if (corr < links.getCorruptionRate()) {
          content = Texts.corrupt(content);
        }
        String toSend = "M:" + fromNode + (char) 13 + toNode + (char) 13
            + content;
        // Network delay
        int delay = (links.getDelay() > 0) ? rand.nextInt(links.getDelay()) : 0;
        synchronized (queue) {
          queue.add(new DelayedMessage(c, toSend, delay));
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
        synchronized (statusMessages) {
          sendNow(statusMessages);
        }
        // Connections can be written to by the EDT while this method is running
        synchronized (connections) {
          int l = connections.size();
          for (int i = 0; i < l; i++) {
            Connection c = connections.get(i);
            if (c != null && c.ready()) {
              String message = c.read();
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
  public void updateStatus(Map<String, Network> cycles) {
    int l = connections.size();
    List<String> texts = null;
    StringBuilder str = new StringBuilder();
    str.append("<html><head><style type='text/css'>\n");
    str.append("body { color: #4444ff; font-weight: normal;}\n");
    str.append("div { width: 100%; text-align: center;}\n");
    str.append(".message { font-weight: bold; color: black;}\n");
    str.append("</style></head>\n<body><div>");
    if (links.getOffset() != 0) {
      texts = new ArrayList<String>();
      Texts.choose_messages(texts, l, links.getCorruptionRate() > 0);
    }
    // Check delay, drop rate, corruption rate and whois
    if (links.getDelay() > 0 || links.getCorruptionRate() > 0
        || links.getDropRate() > 0) {
      str.append("Packets (fragments) may be");
      int badCount = 0;
      if (links.getDelay() > 0) {
        badCount++;
        str.append(" delivered out of order");
      }
      if (links.getCorruptionRate() > 0) {
        str.append(
            ((badCount++ > 0) ? " or " : " ") + "altered by the network");
      }
      if (links.getDropRate() > 0) {
        str.append(((badCount++ > 0) ? " or " : " ") + "lost by the network");
      }
      str.append(".<br>");
    }
    // mark any current messages out of date
    startTime = System.currentTimeMillis();

    synchronized (statusMessages) {
      StringBuilder connectionMessage;
      for (int i = 0; i < l; i++) {
        connectionMessage = new StringBuilder(str);
        Connection c = connections.get(i);
        if (c != null) {
          if (links.getOffset() == 0) {
            connectionMessage.append(
                "<div class='message'>Your task is to find the network topology.</div>"
                    + "You have to find all of the connections in your network.<br>"
                    + "You should also <span class='message'>decide the first step for any recipient.</span>");
          } else {
            int recipient = cycles.get(c.getGroup()).offsetNode(c.getNode(),
                links.getOffset());
            if (links.isCheckwhois()) {
              connectionMessage
                  .append("Your node name is: <span class='message'>"
                      + c.getHostname() + "<br>");
              connectionMessage.append(
                  "</span> Find the  number of the node with name: <span class='message'>"
                      + connections.get(nodeToIndex(recipient)).getHostname()
                      + "</span>");
              connectionMessage.append(
                  "You can only send WHOIS(Query,...) and WHOIS(Answer, ...) messages");
            } else {
              connectionMessage
                  .append("You are to send the message: <div class='message'>");
              connectionMessage.append(texts.get(i));
              connectionMessage.append("</div> to node: " + recipient);
            }
          }

          // Set delay to -1 so that the message is sent out straight away.
          statusMessages.add(new DelayedMessage(c,
              "S:" + connectionMessage + "</div></body></html>", -1));
        }
      }
    }
  }
}
