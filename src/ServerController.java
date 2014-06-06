import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class ServerController {

	MessageList messages;
	LinkList links;
	ConnectionList connections;
	
	public ServerController(MessageList messages, LinkList links, ConnectionList connections){
		this.messages = messages;
		this.connections = connections;
		this.links = links;
	}
	
	public void bind(final JList<Connection> listNodes, final JList<Link> listLinks,
			JButton btnCircular, JButton btnDeleteLink, final JLabel labelDrop,
			final JSlider sliderFailure, final JSpinner spinner, JButton btnCreateLink,
			final JCheckBox chckbxWhoisOnly, final JLabel labelCorruption, final JSlider sliderCorruption){
		
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
					int nodeA = connections.get(selected[0]).node;
					int nodeB = connections.get(selected[selected.length-1]).node;
					System.out.println("Create link between "+nodeA+" and "+nodeB);
					String group = connections.get(selected[0]).group;
					if(group.equals(connections.get(selected[selected.length-1]).group)){
						links.addElement(new Link(nodeA, nodeB, group));
					}
					for(int i = 0; i < selected.length - 1; i++){
						int node1 = connections.get(selected[i]).node;
						int node2 = connections.get(selected[i+1]).node;
						System.out.println("Create link between "+node1+" and "+node2);
						String group1 = connections.get(selected[i]).group;
						if(group1.equals(connections.get(selected[i+1]).group)){
							links.addElement(new Link(node1, node2, group1));
						}
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
		
		spinner.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent arg0) {
				int val = (Integer) spinner.getValue();
				links.setDelay(val);
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
		
	}
	
	
}
