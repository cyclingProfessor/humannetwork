import javax.swing.DefaultListModel;


class ConnectionList extends DefaultListModel<Connection> {


	private static final long serialVersionUID = -1425241047160975471L;

	public ConnectionList(){
		super();
	}
	

}

class LinkList extends DefaultListModel<String> {

	private static final long serialVersionUID = 8598993602003120199L;

	public LinkList(){
		super();
	}

	public String get(int index){
		String link = super.get(index);
		String[] parts = link.split("" + (char) 13);
		if(parts.length > 3){
		return (parts[1] + (char) 13 + parts[3]);
		} else {
			return link;
		}
	}

	public void addElement(String link){
		String[] parts = link.split("" + (char) 13);
		if(parts.length > 2){
			super.addElement("[" + parts[0] + "] " + (char) 13 + parts[1] + (char) 13 + " -> " + (char) 13 + parts[2]);
		} else {
			super.addElement(link);
		}
		
	}

}
