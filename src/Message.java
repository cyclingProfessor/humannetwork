
public class Message {
	
	private int from;
	private int to;
	private int pov;
	private String content;

	public Message(int from, int to, int pov, String content) {
		super();
		this.from = from;
		this.to = to;
		this.pov = pov;
		this.content = content;
	}

	public Message(String message){
		this.from = 0;
		this.to = 0;
		this.content = message;
		this.pov = 0;
	}
	public Message(int fromNode, String raw, int pov){
		this.pov = pov;
		this.from = fromNode;
		String[] parts = raw.split("" + (char) 13);
		if(parts.length > 1){
			this.to = Integer.parseInt(parts[0]);
			this.content = parts[1];
		} else { // should not happen
			this.to = 0;
			this.content = raw;
		}
	}
	public Message(String raw, int pov){
		this.pov = pov;
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
	public int getPov() {
		return pov;
	}
	public void setPov(int pov) {
		this.pov = pov;
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
		String fromS = (from == pov) ? "me" : ("" + from);
		String toS = (to == 0) ? "all" : ((to == pov) ? "me" : ("" + to));
		return ("From " + fromS + " to " + toS + ": " + content);
	}
	
}
