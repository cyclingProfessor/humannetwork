import javax.swing.DefaultListModel;

public class LinkList extends DefaultListModel<Link> {

	private static final long serialVersionUID = -7404443000138807591L;

	// Each value has a current rate and the rate for the next stage.
	private int[] dropRate = { 0, 0 };
	private int[] corruptionRate = { 0, 0 };
	private int[] delay = { 0, 0 };
	private boolean[] checkwhois = { false, false };
	private int[] offset = { 0, 0 };

	public LinkList() {
		super();
	}

	public void nextStage() {
		dropRate[0] = dropRate[1];
		corruptionRate[0] = corruptionRate[1];
		delay[0] = delay[1];
		checkwhois[0] = checkwhois[1];
		offset[0] = offset[1];
	}

	public boolean isNeighbour(int nodeA, int nodeB) {
		for (int i = 0; i < size(); i++) {
			Link l = get(i);
			if ((l.getNodeA() == nodeA && l.getNodeB() == nodeB)
					|| (l.getNodeA() == nodeB && l.getNodeB() == nodeA)) {
				return true;
			}
		}
		return false;
	}

	public int getDropRate() {
		return dropRate[0];
	}

	public void setDropRate(int dropRate) {
		this.dropRate[1] = dropRate;
	}

	public int getCorruptionRate() {
		return corruptionRate[0];
	}

	public void setCorruptionRate(int corruptionRate) {
		this.corruptionRate[1] = corruptionRate;
	}

	public int getDelay() {
		return delay[0];
	}

	public void setDelay(int delay) {
		this.delay[1] = delay;
	}

	public boolean isCheckwhois() {
		return checkwhois[0];
	}

	public void setCheckwhois(boolean checkwhois) {
		this.checkwhois[1] = checkwhois;
	}

	public void setOffset(int val) {
		this.offset[1] = val;
	}

	public int getOffset() {
		return offset[0];
	}

	public boolean nextHasMessages() {
		return offset[1] != 0;
	}
}