public class Link {

	private int nodeA;
	private int nodeB;
	private String group;

	public Link(int nodeA, int nodeB, String group) {
		this.nodeA = nodeA;
		this.nodeB = nodeB;
		this.group = group;
	}

	public int getNodeA() {
		return nodeA;
	}

	public void setNodeA(int nodeA) {
		this.nodeA = nodeA;
	}

	public int getNodeB() {
		return nodeB;
	}

	public void setNodeB(int nodeB) {
		this.nodeB = nodeB;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	@Override
	public String toString() {
		return "[" + group + "] " + nodeA + " <-> " + nodeB;
	}
}
