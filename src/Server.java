import java.net.*;
import java.awt.EventQueue;
import java.io.*;

public class Server {

	 static Route route;

	 static Socket socket;
	 static ServerSocket ssocket;
	 static int port = 10000;
	 
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
						 route.addConnection(osw,isr,socket.getInetAddress());
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
	 
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length > 0){
			port = Integer.parseInt(args[0]);
		}
		final ConnectionList connections = new ConnectionList();
		final LinkList links = new LinkList();
		final MessageList messages = new MessageList();
		route = new Route(connections, links, messages);
		listen(port);
		final ServerController controller = new ServerController(messages, links, connections, route);
		route.start();
		final ServerGui window = new ServerGui();


		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					window.initialize(port, connections, links, messages, controller);
					window.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

}
