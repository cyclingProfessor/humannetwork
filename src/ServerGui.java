import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JButton;

import javax.swing.JCheckBox;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SpinnerNumberModel;

public class ServerGui {

	private JFrame frame;

	/**
	 * Create the application.
	 * @param controller 
	 */
	public ServerGui(int port, ConnectionList connections, 
			LinkList links, MessageList messages, ServerController controller) {
		initialize(port, connections, links, messages, controller);
	}
	
	public void setVisible(boolean b){
		frame.setVisible(b);
	}

	/**
	 * Initialise the contents of the frame.
	 */
	private void initialize(int port, 
			final ConnectionList connections, 
			final LinkList links,
			MessageList messages,
			ServerController controller) {
		frame = new JFrame();
		frame.setTitle("HumanNetwork Server");
		frame.setBounds(100, 100, 800, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JScrollPane scrollPaneNodes = new JScrollPane();
		scrollPaneNodes.setBounds(12, 12, 180, 374);
		frame.getContentPane().add(scrollPaneNodes);
		JList<Connection> listNodes = new JList<Connection>(connections);
		scrollPaneNodes.setViewportView(listNodes);
		
		JScrollPane scrollPaneLinks = new JScrollPane();
		scrollPaneLinks.setBounds(204, 12, 180, 374);
		frame.getContentPane().add(scrollPaneLinks);
		JList<Link> listLinks = new JList<Link>(links);
		scrollPaneLinks.setViewportView(listLinks);
		
		JScrollPane scrollPanePackets = new JScrollPane();
		scrollPanePackets.setBounds(396, 12, 386, 549);
		frame.getContentPane().add(scrollPanePackets);
		JList<Message> listPackets = new JList<Message>(messages);
		listPackets.setVisibleRowCount(20);
		scrollPanePackets.setViewportView(listPackets);
		
		JButton btnCircular = new JButton("Create Circular links");
		btnCircular.setBounds(12, 398, 180, 25);
		frame.getContentPane().add(btnCircular);
		
		JButton btnDeleteLink = new JButton("Delete Link");
		btnDeleteLink.setBounds(12, 469, 180, 25);
		frame.getContentPane().add(btnDeleteLink);

		JButton btnNextStage = new JButton("Next Stage");
		btnNextStage.setBounds(12, 519, 180, 25);
		frame.getContentPane().add(btnNextStage);
		
		JLabel lblRandomDelay = new JLabel("Random delay (s):");
		lblRandomDelay.setBounds(204, 480, 118, 15);
		frame.getContentPane().add(lblRandomDelay);
		
		JSpinner spinDelay = new JSpinner();
		spinDelay.setModel(new SpinnerNumberModel(0, 0, 60, 1));
		spinDelay.setBounds(340, 478, 44, 20);
		frame.getContentPane().add(spinDelay);
		
		JLabel lblRecipientOffset = new JLabel("Recipient Offset:");
		lblRecipientOffset.setBounds(204, 508, 118, 15);
		frame.getContentPane().add(lblRecipientOffset);

		JSpinner spinOffset = new JSpinner();
		spinOffset.setModel(new SpinnerNumberModel(0, -3, 3, 1));
		spinOffset.setBounds(340, 506, 44, 20);
		frame.getContentPane().add(spinOffset);
		
		
		JLabel lblDropRate = new JLabel("Drop rate:");
		lblDropRate.setBounds(204, 395, 118, 15);
		frame.getContentPane().add(lblDropRate);

		JLabel labelDrop = new JLabel("0 %");
		labelDrop.setHorizontalAlignment(SwingConstants.RIGHT);
		labelDrop.setBounds(294, 395, 90, 15);
		frame.getContentPane().add(labelDrop);
		
		JSlider sliderFailure = new JSlider();
		sliderFailure.setValue(0);
		sliderFailure.setBounds(204, 408, 184, 25);
		frame.getContentPane().add(sliderFailure);
	
		JButton btnCreateLink = new JButton("Create link");
		btnCreateLink.setBounds(12, 432, 180, 25);
		frame.getContentPane().add(btnCreateLink);
		
		JCheckBox chckbxWhoisOnly = new JCheckBox("Whois only");
		chckbxWhoisOnly.setBounds(230, 534, 180, 23);
		frame.getContentPane().add(chckbxWhoisOnly);
		
		JLabel lblCorruptionRate = new JLabel("Corruption rate:");
		lblCorruptionRate.setBounds(204, 435, 118, 15);
		frame.getContentPane().add(lblCorruptionRate);
		
		JLabel labelCorruption = new JLabel("0 %");
		labelCorruption.setHorizontalAlignment(SwingConstants.RIGHT);
		labelCorruption.setBounds(294, 435, 90, 15);
		frame.getContentPane().add(labelCorruption);
		
		JSlider sliderCorruption = new JSlider();
		sliderCorruption.setValue(0);
		sliderCorruption.setBounds(204, 448, 184, 25);
		frame.getContentPane().add(sliderCorruption);
		
		controller.bind(listNodes, listLinks, btnCircular, btnDeleteLink, labelDrop, sliderFailure, spinDelay, btnCreateLink, 
				chckbxWhoisOnly, labelCorruption, sliderCorruption, btnNextStage, spinOffset);
		
	}
}
