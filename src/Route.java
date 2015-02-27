import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Route extends Thread {

	long startTime = -1; 
	ConnectionList connections;
	MessageList messages;
	LinkList links;
	Random rand = new Random();
	List<DelayedMessage> queue = new ArrayList<DelayedMessage>();
	List<DelayedMessage> statusMessages = new ArrayList<DelayedMessage>();
	private Object lock = new Object();
	
	public Route(ConnectionList connections, LinkList links, MessageList messages){
		this.connections = connections;
		this.messages = messages;
		this.links = links;
	}

	public void addConnection(OutputStreamWriter osw,	InputStreamReader isr, InetAddress a){
		Connection c = new Connection(osw,isr);
		String id = c.read();
		int setnode = Integer.parseInt(c.read());
		if(setnode == 0){
			byte[] address = a.getAddress();
			setnode = address[2] + 257 + address[3];
		}
		boolean found = false;
		for (int i = 0; i < connections.size(); i++) {
			Connection cc = connections.get(i);
			System.out.println("Checking " + cc.group + "/" + cc.node);
			if(cc.node == setnode){
				c.setNode(cc.node);
				System.out.println(id + " is playing existing node " + c.node);
				c.write("" + c.node);
				connections.set(i, c);
				found = true;
				break;
			}
		}
		c.group = id;
		if (!found){
			int node = setnode;
			c.node = node;
			c.write("" + node);
			System.out.println(id + " is playing new node " + node);
			connections.addElement(c);
		}
	}
	
	private Connection getByNode(int node){
		for(int i = 0; i < connections.size(); i++) {
			Connection c = connections.get(i);
			if (c.node == node) {
				return c;
			}
		}
		return null;
	}
	
	public int nodeToIndex(int node){
		int l = connections.size();
		for(int j = 0 ; j < l ; j++){
			 if (connections.get(j).node == node){
				 return j;
			 }
		}
		return 0; // Should not happen
	}
	
	public void route(String message, int fromNode){
		String[] pieces = message.split(String.valueOf((char)13));
		if(pieces.length != 2){
			// Silently drop the message
			messages.addMessage("From " + fromNode + ": " + message + "dropped(ill-formed)");
			System.out.println("Ill-formed message");
		} else {
			int toNode = Integer.parseInt(pieces[0]);
			messages.addMessage(fromNode, toNode, 0, message);
			if (toNode == 0){
				// Broadcast
				System.out.println("Broadcasting");
				for(int i = 0; i < connections.size(); i++) {
					Connection c = connections.get(i);
					if(links.isNeighbour(fromNode,c.node)) {
						send(c,pieces[1],fromNode,toNode);
					}
				}
			} else {
				Connection c = getByNode(toNode);
				if (c == null || !links.isNeighbour(fromNode, toNode)){
					// node does not exist or is not connected
					//messages.addElement("" + fromNode + (char) 13 + message + "dropped(unreachable)");
					System.out.println("Unreacheable node");
				} else {
					// send to toNode the original message
					send(c,pieces[1],fromNode,toNode);
				}
			}
		}
	}
	
	public void send(Connection c, String message, int fromNode, int toNode){
		System.out.println("Attempting to send "+message);
		int drop = rand.nextInt(100);
		if (drop >= links.getDropRate() || links.getDropRate() == 0){
			String content = message;
			boolean shouldSend = !links.isCheckwhois() || ((content.length() > 12) && 
					(content.substring(0,12).equals("WHOIS(Query,") ||
					 content.subSequence(0, 13).equals("WHOIS(Answer,")));
			if(shouldSend){
				// Corruption
				// TODO Change so that Names and Times are replaced.
				int corr = rand.nextInt(100);
				if(corr < links.getCorruptionRate()){
					StringBuilder s = new StringBuilder();
          // DO SOME NASTY CORRUPTION
					content = s.toString();
				}
				String toSend = "M:" + fromNode + (char) 13 + toNode + (char) 13 + content;
				// Network delay
				int delay = (links.getDelay() > 0) ? rand.nextInt(links.getDelay()) : 0;
				System.out.println("Adding delay "+delay);
				queue.add(new DelayedMessage(c,toSend,delay));
				//String delayed = (delay != 0) ? ("delayed " + delay) : "";
				//messages.addElement(toSend + delayed);
			} else {
				System.out.println("dropped (no WHOIS) ");
				//messages.addElement(content + " dropped(no WHOIS)");
			}
		}
	}
	
	public void sendNow(List<DelayedMessage> messageList){
		for(int i = messageList.size()-1; i >= 0; i--){
			DelayedMessage m = messageList.get(i);
			if(m.ready()){
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
		 try{
			 while(true){
				 sendNow(queue);
				 synchronized(statusMessages) {
					 sendNow(statusMessages);
				 }
				 int l = connections.size();
				 for (int i = 0 ; i < l ; i++){
					 Connection c = connections.get(i);
					 if(c!= null && c.ready()){
						 System.out.println("Received message from "+i);
						 String message = c.read();
						 System.out.println(message);
  					 route(message,c.node);
					 }
				 }
				 Thread.sleep(1000L);
				 //System.out.print('+');
			 }
		 } catch (InterruptedException e){
			 System.out.println("Closing server!");
		 }
	}

	public void updateStatus() {
		int l = connections.size();
		List<String> texts = null;
		// Link to running Thread by using an indicator variable
    StringBuilder str = new StringBuilder();
    str.append("<html><head><style type='text/css'>");
		str.append("body { color: #4444ff; font-weight: normal;}");
		str.append("div { width: 100%; text-align: center}");
		if (links.getOffset() == 0) {
			str.append("<div>Your task is to find the network topology.  <br>" +
			    "You have to find all of the connections in your network.<br>" + 
					"You should also decide the first step for any recipient.</div>");
		} else {
			// Now we will be sending a message
			texts = new ArrayList<String>();
			Texts.choose_messages(texts, l, links.getCorruptionRate() > 0);
			
			// Check delay, drop rate, corruption rate and whois
			if (links.getDelay() > 0 || links.getCorruptionRate() > 0 || links.getDropRate() > 0) {
				str.append("Packets (fragments) may be");
				int badCount = 0;
			  if (links.getDelay() > 0) {
			  	badCount++;
					str.append(" delivered out of order");
			  }
			  if (links.getCorruptionRate() > 0) {
					str.append(((badCount++ > 0) ? "or " : " ") + "altered by the network");
			  }
			  if (links.getDropRate() > 0) {
					str.append(((badCount++ > 0) ? "or " : " ") + "lost by the network");
			  }
			  str.append(".<br>");
			}
		}
		startTime = System.currentTimeMillis();
		synchronized (statusMessages) {
			for (int i = 0; i < l; i++) {
				Connection c = connections.get(i);
				if (c != null) {
					if (links.getOffset() > 0) {
						// TODO
						int recipient = 1;// Find an appropriate node in either dirction -2 and 2 should get different answers.
						if (links.isCheckwhois()) {
							str.append("Your node name is: " + c.getHostname() + "<br>");
							str.append("You must find out the node number of the node with name: " + connections.get(recipient).getHostname());
						} else {
							str.append("You are to send the message: <br>");
							str.append(texts.get(i));
							str.append("to node: " + recipient);							
						}
					}

					DelayedMessage status = new DelayedMessage(c, "S:Start Now!", 0);
					// Check whether messages are needed
					statusMessages.add(status);
				}
			}
		}
		
	}

}
