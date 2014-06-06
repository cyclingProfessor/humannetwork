import java.awt.EventQueue;
import java.util.Scanner;

import javax.swing.JOptionPane;

public class Client {

	// Client connection to the server
	static Connection c;
	static String id = "toto";
	
	/**
	 * Thread listening for incoming messages
	 */
	public static void listen(final MessageList messages){
		 Thread listen = new Thread(){
			 public void run(){
				 try{
					 while(true){
						 if (c != null && c.ready()){
							 String message = c.read();
							 System.out.println(message);
							 messages.addElement(message);
							 }
						 Thread.sleep(1000L);
						 //System.out.print('.');
					 }
				 }
				 catch (InterruptedException e){
					 
				 }
			 }
		 };
		 listen.start();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/** Define a host server */
		String host = "stafflinux.cs.rhul.ac.uk";
		/** Define a port */
		int port = 10000;
		int node = 0;
		
		/** Each has an group id */
		try{
			if (args.length > 1){
				host = args[0];
				port = Integer.parseInt(args[1]);
			}
			if (args.length > 2){
				node = Integer.parseInt(args[2]);
			}
		} catch (Exception e){
			e.printStackTrace();
			System.exit(0);
		}

		/** Connect */
		String group = (String) JOptionPane.showInputDialog("What is your group?");
		if (group == null){ group = ""; }
		c = new Connection(host, port, group, node);
		
		/** Receive messages */
		final MessageList messages = new MessageList();
		listen(messages);
		final ClientController controller = new ClientController(c, messages);
		
		final Scanner scanner = new Scanner(System.in);

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientGui window = new ClientGui(c, messages, controller);
					window.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
					scanner.close();
				}
			}
		});
		
		while(true){
			String message = scanner.nextLine();
			System.out.print("To whom: ");
			String to = scanner.nextLine();
			c.write(to + (char) 13 + message);
		}
		
		// TODO GUI for building message

	}	

}
