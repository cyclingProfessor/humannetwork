public class Notification {
	String text;
	MSG_TYPE type;

	public Notification(String read) {
		System.out.println("Received: " + read);
		switch (read.charAt(0)) {
			case 'S':
				type = MSG_TYPE.STATUS;
				text = read.substring(2);
				break;
			case 'M':
				type = MSG_TYPE.MESSAGE;
				text = read.substring(2);
				break;
			default:
				// MUST not occur
				type = MSG_TYPE.STATUS;
				;
				text = "BAD Notification type from server";
				break;
		}
	}

	public MSG_TYPE getType() {
		return type;
	}

	public String getText() {
		return text;
	}

}
