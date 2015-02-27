import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class ServerController {

//	private MessageList messages;
	private LinkList links;
	private ConnectionList connections;
	private Random rand = new Random();
	private Route route;
	
	public ServerController(MessageList messages, LinkList links, ConnectionList connections, Route route){
//		this.messages = messages;
		this.connections = connections;
		this.links = links;
		this.route = route;
	}
	
	public void bind(final JList<Connection> listNodes, final JList<Link> listLinks,
			JButton btnCircular, JButton btnDeleteLink, final JLabel labelDrop,
			final JSlider sliderFailure, final JSpinner spinDelay, JButton btnCreateLink,
			final JCheckBox chckbxWhoisOnly, final JLabel labelCorruption, final JSlider sliderCorruption,
			final JButton btnNextStage, final JSpinner spinOffset){
		
		btnCreateLink.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int[] selected = listNodes.getSelectedIndices();
				if(selected.length == 2){
					int nodeA = connections.get(selected[0]).node;
					int nodeB = connections.get(selected[1]).node;
					System.out.println("Create link between "+nodeA+" and "+nodeB);
					String group = connections.get(selected[0]).group;
					if(group.equals(connections.get(selected[0]).group)){
						links.addElement(new Link(nodeA, nodeB, group));
					}
				}
			}
		});

		btnCircular.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int[] selected = listNodes.getSelectedIndices();
				if(selected.length > 2){
					String group = connections.get(selected[0]).group;
					for(int i = 1; i < selected.length; i++){
						if(!group.equals(connections.get(selected[i]).group)){
							System.out.println("No cycle created - more than one group selected.");
						}
					}
					for (int index = selected.length - 1 ; index > 0  ; index--) {
						int other = rand.nextInt(index);
						int temp = selected[other];
						selected[other] = selected[index];
						selected[index] = temp;
					}
					int nodeA = connections.get(selected[0]).node;
					int nodeB = connections.get(selected[selected.length-1]).node;
					System.out.println("Create link between "+nodeA+" and "+nodeB);
					links.addElement(new Link(nodeA, nodeB, group));
					for(int i = 0; i < selected.length - 1; i++){
						int node1 = connections.get(selected[i]).node;
						int node2 = connections.get(selected[i+1]).node;
						System.out.println("Create link between "+node1+" and "+node2);
						links.addElement(new Link(node1, node2, group));
					}
					
				}
			}
		});

		btnDeleteLink.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Delete link");
				int[] selected = listLinks.getSelectedIndices();
				for(int i = selected.length -1; i >=0; i--) {
					links.remove(selected[i]);
				}
			}
		});
		
		sliderFailure.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent arg0) {
				int val = sliderFailure.getValue();
				labelDrop.setText(val + "%");
				links.setDropRate(val);
			}
		});
		
		spinDelay.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent arg0) {
				int val = (Integer) spinDelay.getValue();
				links.setDelay(val);
			}
		});
		
		
		spinOffset.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent arg0) {
				int val = (Integer) spinDelay.getValue();
				links.setOffset(val);
			}
		});

		
		chckbxWhoisOnly.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent arg0) {
				boolean b = chckbxWhoisOnly.isSelected();
				links.setCheckwhois(b);
			}
		});

		sliderCorruption.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent arg0) {
				int val = (Integer) sliderCorruption.getValue();
				links.setCorruptionRate(val);
				labelCorruption.setText(val + "%");
			}
		});
		

		btnNextStage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Send Status Out");
				// clear message queue and send new status messages
				route.updateStatus();
			}
		});
		
	}
	
	
}
