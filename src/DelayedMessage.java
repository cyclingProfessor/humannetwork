
public class DelayedMessage {
	
	private int delay = 0;
	private String message = "";
	private Connection connection;
	
	public DelayedMessage(Connection connection, String message, int delay){
		this.delay = delay;
		this.message = message;
		this.connection = connection;
	}
	
	public void decr(){
		delay--;
	}
	
	public boolean ready(){
		return delay < 0;
	}

	public void send(){
		connection.write(message);
	}
}
