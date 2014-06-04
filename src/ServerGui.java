import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JCheckBox;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class ServerGui {

	protected JFrame frmDarknetServer;

	/**
	 * Create the application.
	 */
	public ServerGui(int port, Route route, ConnectionList connections, 
			LinkList links, MessageList messages) {
		initialize(port, route, connections, links, messages);
	}

	/**
	 * Initialise the contents of the frame.
	 */
	private void initialize(int port, Route route, 
			final ConnectionList connections, 
			final LinkList links,
			MessageList messages) {
		frmDarknetServer = new JFrame();
		frmDarknetServer.setTitle("DarkNet Server");
		frmDarknetServer.setBounds(100, 100, 800, 600);
		frmDarknetServer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmDarknetServer.getContentPane().setLayout(null);
		
		JScrollPane scrollPaneNodes = new JScrollPane();
		scrollPaneNodes.setBounds(12, 12, 180, 374);
		frmDarknetServer.getContentPane().add(scrollPaneNodes);
		
		final JList<Connection> listNodes = new JList<Connection>(connections);
		scrollPaneNodes.setViewportView(listNodes);
		
		JScrollPane scrollPaneLinks = new JScrollPane();
		scrollPaneLinks.setBounds(204, 12, 180, 374);
		frmDarknetServer.getContentPane().add(scrollPaneLinks);
		
		final JList<String> listLinks = new JList<String>(links);
		scrollPaneLinks.setViewportView(listLinks);
		
		JScrollPane scrollPanePackets = new JScrollPane();
		scrollPanePackets.setBounds(396, 12, 386, 549);
		frmDarknetServer.getContentPane().add(scrollPanePackets);
		
		JList<String> listPackets = new JList<String>(messages);
		listPackets.setVisibleRowCount(20);
		scrollPanePackets.setViewportView(listPackets);
		
		JButton btnCircular = new JButton("Create Circular links");
		btnCircular.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int[] selected = listNodes.getSelectedIndices();
				if(selected.length > 2){
					int nodeA = connections.get(selected[0]).node;
					int nodeB = connections.get(selected[selected.length-1]).node;
					System.out.println("Create link between "+nodeA+" and "+nodeB);
					String id = connections.get(selected[0]).group;
					if(id.equals(connections.get(selected[selected.length-1]).group)){
						links.addElement(id + (char) 13 + nodeA + (char) 13 + nodeB);
					}
					for(int i = 0; i < selected.length - 1; i++){
						int node1 = connections.get(selected[i]).node;
						int node2 = connections.get(selected[i+1]).node;
						System.out.println("Create link between "+node1+" and "+node2);
						String idl = connections.get(selected[i]).group;
						if(id.equals(connections.get(selected[i]).group)){
							links.addElement(idl + (char) 13 + node1 + (char) 13 + node2);
						}
					}
					
				}
			}
		});
		btnCircular.setBounds(12, 398, 180, 25);
		frmDarknetServer.getContentPane().add(btnCircular);
		
		JButton btnDeleteLink = new JButton("Delete Link");
		btnDeleteLink.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Delete link");
				int[] selected = listLinks.getSelectedIndices();
				for(int i = selected.length -1; i >=0; i--) {
					links.remove(selected[i]);
				}
			}
		});
		btnDeleteLink.setBounds(12, 469, 180, 25);
		frmDarknetServer.getContentPane().add(btnDeleteLink);

		
		final JLabel lblRandomDelay = new JLabel("Random delay (s):");
		lblRandomDelay.setBounds(204, 506, 118, 15);
		frmDarknetServer.getContentPane().add(lblRandomDelay);
		
		JLabel lblDropRate = new JLabel("Drop rate:");
		lblDropRate.setBounds(204, 403, 118, 15);
		frmDarknetServer.getContentPane().add(lblDropRate);

		final JLabel labelDrop = new JLabel("0 %");
		labelDrop.setHorizontalAlignment(SwingConstants.RIGHT);
		labelDrop.setBounds(294, 403, 90, 15);
		frmDarknetServer.getContentPane().add(labelDrop);
		
		final JSlider sliderFailure = new JSlider();
		sliderFailure.setValue(0);
		sliderFailure.setBounds(204, 415, 184, 25);
		sliderFailure.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent arg0) {
				int val = sliderFailure.getValue();
				labelDrop.setText(val + "%");
				links.setDropRate(val);
			}
		});
		frmDarknetServer.getContentPane().add(sliderFailure);
		
		final JSpinner spinner = new JSpinner();
		spinner.setModel(new SpinnerNumberModel(0, 0, 60, 1));
		spinner.setBounds(340, 504, 44, 20);
		frmDarknetServer.getContentPane().add(spinner);
		
		spinner.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent arg0) {
				int val = (Integer) spinner.getValue();
				links.setDelay(val);
			}
		});
		
		JButton btnCreateLink = new JButton("Create link");
		btnCreateLink.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int[] selected = listNodes.getSelectedIndices();
				if(selected.length == 2){
					int nodeA = connections.get(selected[0]).node;
					int nodeB = connections.get(selected[1]).node;
					System.out.println("Create link between "+nodeA+" and "+nodeB);
					String id = connections.get(selected[1]).group;
					if(id.equals(connections.get(selected[0]).group)){
						links.addElement(id + (char) 13 + nodeA + (char) 13 + nodeB);
					}
				}
			}
		});
		
		btnCreateLink.setBounds(12, 432, 180, 25);
		frmDarknetServer.getContentPane().add(btnCreateLink);
		
		final JCheckBox chckbxWhoisOnly = new JCheckBox("Whois only");
		chckbxWhoisOnly.setBounds(204, 529, 180, 23);
		frmDarknetServer.getContentPane().add(chckbxWhoisOnly);
		
		chckbxWhoisOnly.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent arg0) {
				boolean b = chckbxWhoisOnly.isSelected();
				links.setCheckwhois(b);
			}
		});
		
		JLabel lblCorruptionRate = new JLabel("Corruption rate:");
		lblCorruptionRate.setBounds(204, 457, 118, 15);
		frmDarknetServer.getContentPane().add(lblCorruptionRate);
		
		final JLabel labelCorruption = new JLabel("0 %");
		labelCorruption.setHorizontalAlignment(SwingConstants.RIGHT);
		labelCorruption.setBounds(294, 457, 90, 15);
		frmDarknetServer.getContentPane().add(labelCorruption);
		
		final JSlider sliderCorruption = new JSlider();
		sliderCorruption.setValue(0);
		sliderCorruption.setBounds(204, 469, 184, 25);
		frmDarknetServer.getContentPane().add(sliderCorruption);

		sliderCorruption.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent arg0) {
				int val = (Integer) sliderCorruption.getValue();
				links.setCorruptionRate(val);
				labelCorruption.setText(val + "%");
			}
		});
		
		
	}
}
