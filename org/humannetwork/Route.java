package humannetwork;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Random;

public class Route extends Thread {

	ArrayList<Connection> connections = new ArrayList<Connection>();
	Random rand = new Random();
	
	// ConnectionList connections;

	public int newNode(){
		while(true) {
			int node = rand.nextInt(900) + 100; // node number is between 100 and 999
			if (getByNode(node) == null) {
				return node;
			}
		}
	}

	public void addConnection(OutputStreamWriter osw,	InputStreamReader isr){
		Connection c = new Connection(osw,isr);
		String id = c.read();
		boolean found = false;
		for (int i = 0; i < connections.size(); i++) {
			Connection cc = connections.get(i);
			System.out.println("Checking " + cc.id);
			if(cc.id.equals(id)){
				c.setNode(cc.node);
				System.out.println(id + " is playing existing node " + c.node);
				c.write("" + c.node);
				connections.set(i, c);
				found = true;
				break;
			}
		}
		if (!found){
			int node = newNode();
			c.node = node;
			c.id = id;
			c.write("" + node);
			System.out.println(id + " is playing new node " + node);
			connections.add(c);
		}
	}
	
	private Connection getByNode(int node){
		for(Connection c : connections){
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
	
	public Route(){
		connections = new ArrayList<Connection>();
	}
	
	public boolean isNeighbour(int nodeA, int nodeB){
		// For now :-)
		// Circle
		int iA = nodeToIndex(nodeA);
		int iB = nodeToIndex(nodeB);
		return iA % 10 == (iB + 1) % 10 || iA % 10 == (iB - 1) % 10;
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
				for(Connection c : connections){
					if(isNeighbour(fromNode,c.node)) {
						c.write(fromNode + "" + (char) 13 + message);
					}
				}
			} else {
				Connection c = getByNode(toNode);
				if (c == null || !isNeighbour(fromNode, toNode)){
					// node does not exist or is not connected
					System.out.println("Unreacheable node");
				} else {
					// send to toNode the original message
					c.write(fromNode + "" + (char) 13 + message);
				}
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
