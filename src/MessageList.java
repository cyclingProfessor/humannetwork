import javax.swing.DefaultListModel;


public class MessageList extends DefaultListModel<String> {

	private static final long serialVersionUID = 6152415102958990076L;

	public MessageList(){
		super();
	}
	
	public void addElement(String message){
		String[] parts = message.split("" + (char) 13);
		if(parts.length > 2){
			String to = (parts[1].equals("0")) ? "all" : parts[1];
			super.addElement("From "+ parts[0] +" to "+ to + ": " + (char) 13 + parts[2]);
		} else {
			super.addElement(message);
		}
		
	}
	
	public String get(int index){
		String message = super.get(index);
		String[] parts = message.split("" + (char) 13);
		if(parts.length > 1){
		return parts[1];
		} else {
			return message;
		}
	}

	public void moveUp(int i) {
		// TODO Auto-generated method stub
		
	}

	public void moveDown(int i) {
		// TODO Auto-generated method stub
		
	}
	
	
}
