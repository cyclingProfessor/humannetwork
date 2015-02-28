
public class Message {
	
	private int from;
	private int to;
	private static int myNode;
	private String content;

	public Message(int from, int to, String content) {
		this.from = from;
		this.to = to;
		this.content = content;
	}
	
	public Message(String raw){
		String[] parts = raw.split("" + (char) 13);
		if(parts.length > 2){
			this.to = Integer.parseInt(parts[1]);
			this.from = Integer.parseInt(parts[0]);
			this.content = parts[2];
		} else { // Should not happen
			this.from = 0;
			this.to = 0;
			this.content = raw;
		}
	}
	
	public int getFrom() {
		return from;
	}
	public void setFrom(int from) {
		this.from = from;
	}
	public int getTo() {
		return to;
	}
	public void setTo(int to) {
		this.to = to;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	public String toString(){
		if (from == 0) {
			return content;
		}
		String fromS = (from == myNode) ? "me" : ("" + from);
		String toS = (to == 0) ? "all" : ((to == myNode) ? "me" : ("" + to));
		return ("From " + fromS + " to " + toS + ": " + content);
	}
	
}
