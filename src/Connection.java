import java.io.*;
import java.net.*;


public class Connection {

	Socket connection;
	String hostname = "localhost";

	OutputStreamWriter osw;
	InputStreamReader isr;
	String group = "server";
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
	
	public String getHostname() {
		return hostname;
	}

	public void setName() {
		this.hostname = Texts.choose_name(node);
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
	public Connection(String hostname, int port, String id, int setnode){

		//System.out.println("Darknet Client initialised");
		//System.out.println("Connecting to " + hostname + ":" + port);
		try {
			/** Obtain an address object of the server */
			InetAddress address = InetAddress.getByName(hostname);
			/** Establish a socket connection */
			connection = new Socket(address, port);
			//System.out.println("Client connected");
			BufferedOutputStream bos = new BufferedOutputStream(connection.
					getOutputStream());
			osw = new OutputStreamWriter(bos, "US-ASCII");
			BufferedInputStream bis = new BufferedInputStream(connection.
					getInputStream());
			isr = new InputStreamReader(bis, "US-ASCII");
			
			this.group = id;
			write(id);
			write("" + setnode);
			String nodeMessage = read();
			node = Integer.parseInt(nodeMessage);
			//System.out.println("node: " + node);
			//System.out.println("id: " + id);
		}
		catch (IOException f) {
			System.err.println("IOException: " + f);
			System.exit(0);
		}
		catch (Exception g) {
			System.err.println("Exception: " + g);
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
			System.err.println("IOException: " + f);
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
			System.err.println("IOException: " + f);
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
			System.err.println("IOException: " + e);
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
			System.err.println("IOException: " + e);
		}
	}

	public int getInet() {
		
		return 0;
	}
	
	public String toString(){
		return (" [" + group + "] " + node );
	}

}
