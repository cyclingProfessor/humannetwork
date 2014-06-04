import javax.swing.DefaultListModel;


public class LinkList extends DefaultListModel<String> {
	
	private static final long serialVersionUID = -7404443000138807591L;
	
	private int dropRate = 0;
	private int corruptionRate = 0;
	private int delay = 0;
	private boolean checkwhois = false;

	public LinkList() {
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

	public int getDropRate() {
		return dropRate;
	}

	public void setDropRate(int dropRate) {
		this.dropRate = dropRate;
	}

	public int getCorruptionRate() {
		return corruptionRate;
	}

	public void setCorruptionRate(int corruptionRate) {
		this.corruptionRate = corruptionRate;
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public boolean isCheckwhois() {
		return checkwhois;
	}

	public void setCheckwhois(boolean checkwhois) {
		this.checkwhois = checkwhois;
	}
}