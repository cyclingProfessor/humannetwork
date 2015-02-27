import javax.swing.DefaultListModel;


public class LinkList extends DefaultListModel<Link> {
	
	private static final long serialVersionUID = -7404443000138807591L;
	
	private int dropRate = 0;
	private int corruptionRate = 0;
	private int delay = 0;
	private boolean checkwhois = false;
	private int offset = 0;

	public int getOffset() {
		return offset;
	}

	public LinkList() {
		super();
	}
	
	public boolean isNeighbour(int nodeA, int nodeB){
		for (int i = 0; i< size(); i++) {
			Link l = get(i);
			if((l.getNodeA() == nodeA && l.getNodeB() == nodeB) || 
					(l.getNodeA() == nodeB && l.getNodeB() == nodeA)){
				return true;
			}
		}
		return false;
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

	public void setOffset(int val) {
		this.offset = val;
	}
}