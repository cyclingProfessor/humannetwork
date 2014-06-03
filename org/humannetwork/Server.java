package humannetwork;

import java.net.*;
import java.io.*;

public class Server {

	 static Route route;

	 static Socket socket;
	 static ServerSocket ssocket;
	 static int port = 19999;
	 
	 /**
	  * Listen to incoming connections
	  * @param port server port
	  */
	 public static void listen(final int port){
		 Thread listen = new Thread(){
			 public void run(){
				 try{
					 int backlog = 0;
					 InetAddress a = InetAddress.getLocalHost();
					 ssocket = new ServerSocket(port,backlog,a);
					 System.out.println("DarkNet Initialised on " + a + ":" + port);
					 while(true){
						 socket = ssocket.accept();
						 String id = socket.getInetAddress().toString() + ":" + socket.getPort();
						 System.out.println("Accepted Connection from " + id);
						 BufferedInputStream is = new BufferedInputStream(socket.getInputStream());
						 InputStreamReader isr = new InputStreamReader(is);
						 BufferedOutputStream os = new BufferedOutputStream(socket.getOutputStream());
						 OutputStreamWriter osw = new OutputStreamWriter(os, "US-ASCII");
						 //int node = route.newNode();
						 //Connection c = new Connection(osw, isr, node);
						 route.addConnection(osw,isr);
						 //Thread.sleep(1000L);
					 }
			//	 } catch (InterruptedException e){
			//		 System.out.println("Closing server!");
				 } catch (IOException e) {
					 System.out.println("Closing server ...");
					 e.printStackTrace();
				 }
			 }
		 };
		 listen.start();
	 }
	 
	 /*
	  * Routing thread
	  */
	 public static void route(){
		 route.start();
	 }
	 
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length > 0){
			port = Integer.parseInt(args[0]);
		}
		route = new Route();
		route.start();
		listen(port);
		
		// TODO read network topology
		// TODO listen to network connections
		// TODO setup routing
		// TODO interface to see messages

	}

}
