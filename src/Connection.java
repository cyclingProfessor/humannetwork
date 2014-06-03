import java.io.*;
import java.net.*;


public class Connection {

	ServerSocket socket1;
	protected int port = 19999;
	Socket connection;
	StringBuffer process;
	String hostname = "localhost";
	OutputStreamWriter osw;
	InputStreamReader isr;
	String id = "server";
	int node = 1000;

	/**
	 * Constructor for the Server
	 * @param osw
	 * @param isr
	 */
	public Connection(OutputStreamWriter osw,	InputStreamReader isr){
		this.isr = isr;
		this.osw = osw;
	}
	
	public void setNode (int node){
		this.node = node;
	}

	/**
	 * Constructor for the client
	 * @param hostname
	 * @param port
	 * @param id
	 */
	public Connection(String hostname, int port, String id){

		System.out.println("Darknet Client initialised");
		System.out.println("Connecting to " + hostname + ":" + port);
		try {
			/** Obtain an address object of the server */
			InetAddress address = InetAddress.getByName(hostname);
			/** Establish a socket connection */
			connection = new Socket(address, port);
			System.out.println("Client connected");
			BufferedOutputStream bos = new BufferedOutputStream(connection.
					getOutputStream());
			osw = new OutputStreamWriter(bos, "US-ASCII");
			BufferedInputStream bis = new BufferedInputStream(connection.
					getInputStream());
			isr = new InputStreamReader(bis, "US-ASCII");
			
			this.id = id;
			write(id);
			String nodeMessage = read();
			node = Integer.parseInt(nodeMessage);
			System.out.println("node: " + node);
			System.out.println("id: " + id);
		}
		catch (IOException f) {
			System.out.println("IOException: " + f);
			System.exit(0);
		}
		catch (Exception g) {
			System.out.println("Exception: " + g);
		}
	}
	
	
	/**
	 * Sends a message through that connection
	 * @param s the message
	 * @return true if the write is successful
	 */
	public boolean write (String s){
		boolean result = true;
		try {
			osw.write(s + (char) 0);
			osw.flush();
		}
		catch (IOException f) {
			System.out.println("IOException: " + f);
			result = false;
		}
		return result;
	}
	
	/**
	 * Reads a message from that connection
	 * @return the message read (empty if something fails)
	 */
	public String read(){
		StringBuffer instr = new StringBuffer();
		int c;
		try {
			while ( (c = isr.read()) != 0)
				instr.append( (char) c);
		}
		catch (IOException f) {
			System.out.println("IOException: " + f);
		}
		return instr.toString();
	}

	/**
	 * Checks if there is an incoming message.
	 * @return true if there is.
	 */
	public boolean ready() {
		try {
			return isr.ready();
		} catch (IOException e) {
			System.out.println("IOException: " + e);
			return false;
		}
	}
	
	/**
	 * Close the connection
	 */
	public void close(){
		try {
			connection.close();
		} catch (IOException e) {
			System.out.println("IOException: " + e);
		}
	}

}