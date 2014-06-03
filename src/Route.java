import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.util.Random;

public class Route extends Thread {

	ConnectionList connections;
	MessageList messages;
	LinkList links;
	Random rand = new Random();
	
	public Route(ConnectionList connections, LinkList links, MessageList messages){
		this.connections = connections;
		this.messages = messages;
		this.links = links;
	}
	
	public int newNode(){
		while(true) {
			int node = rand.nextInt(900) + 100; // node number is between 100 and 999
			if (getByNode(node) == null) {
				return node;
			}
		}
	}

	public void addConnection(OutputStreamWriter osw,	InputStreamReader isr, InetAddress a){
		Connection c = new Connection(osw,isr);
		String id = c.read();
		int setnode = Integer.parseInt(c.read());
		if(setnode == 0){
			byte[] address = a.getAddress();
			setnode = address[3];
		}
		boolean found = false;
		for (int i = 0; i < connections.size(); i++) {
			Connection cc = connections.get(i);
			System.out.println("Checking " + cc.id + "/" + cc.node);
			if(cc.node == setnode){
				c.setNode(cc.node);
				System.out.println(id + " is playing existing node " + c.node);
				c.write("" + c.node);
				connections.set(i, c);
				found = true;
				break;
			}
		}
		if (!found){
			int node = setnode;
			c.node = node;
			c.id = id;
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
	
	
	public boolean isNeighbour(int nodeA, int nodeB){
		// Check in links
		String link1 = nodeA + "" + (char) 13 + nodeB;
		String link2 = nodeB + "" + (char) 13 + nodeA;
		for (int i = 0; i< links.size(); i++) {
			if(link1.equals(links.get(i)) || link2.equals(links.get(i))){
				return true;
			}
		}
		return false;
	}
	
	public void route(String message, int fromNode){
		String[] pieces = message.split(String.valueOf((char)13));
		if(pieces.length != 2){
			// Silently drop the message
			System.out.println("Ill-formed message");
		} else {
			int toNode = Integer.parseInt(pieces[0]);
			if (toNode == 0){
				// Broadcast
				for(int i = 0; i < connections.size(); i++) {
					Connection c = connections.get(i);
					if(isNeighbour(fromNode,c.node)) {
						send(c,pieces[1],fromNode,toNode);
					}
				}
			} else {
				Connection c = getByNode(toNode);
				if (c == null || !isNeighbour(fromNode, toNode)){
					// node does not exist or is not connected
					System.out.println("Unreacheable node");
				} else {
					// send to toNode the original message
					send(c,pieces[1],fromNode,toNode);
				}
			}
		}
	}
	
	public void send(Connection c, String message, int fromNode, int toNode){
		int drop = rand.nextInt(100);
		if (drop >= links.dropRate){
			String content = message;
			boolean whois = links.checkwhois; // Only whois allowed
			whois = !whois || ((content.length() > 12) && 
					(content.substring(0,12).equals("WHOIS(Query,") ||
					 content.subSequence(0, 13).equals("WHOIS(Answer,")));
			if(whois){
				c.write(fromNode + "" + (char) 13 + toNode + (char) 13 + content);
			}
		}
	}
	
	
	@Override
	public void run() {
		 try{
			 while(true){
				 int l = connections.size();
				 for (int i = 0 ; i < l ; i++){
					 Connection c = connections.get(i);
					 if(c!= null && c.ready()){
						 System.out.println("Received message from "+i);
						 String message = c.read();
						 System.out.println(message);
						 messages.addElement("" + c.node + (char) 13 + message);
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

}
