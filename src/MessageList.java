import javax.swing.DefaultListModel;

public class MessageList extends DefaultListModel<Message> {

	private static final long serialVersionUID = 6152415102958990076L;

	public MessageList() {
		super();
	}

	public void addMessage(int from, int to, String s) {
		Message m = new Message(from, to, s);
		super.addElement(m);
	}

	public void addMessage(String s) {
		Message m = new Message(s);
		super.addElement(m);
	}

	public void moveUp(int i) {
		Message a = super.get(i);
		Message b = super.get(i - 1);
		super.set(i, b);
		super.set(i - 1, a);
	}

	public void moveDown(int i) {
		Message a = super.get(i);
		Message b = super.get(i + 1);
		super.set(i, b);
		super.set(i + 1, a);
	}

}
