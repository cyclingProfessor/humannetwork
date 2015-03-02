import java.awt.Dimension;
import java.util.Formatter;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class ServerGui extends JFrame {

	/**
	 * Initialise the contents of the contents.
	 */
	public void initialize(int port, final ConnectionList connections,
			final LinkList links, MessageList messages, ServerController controller) {
		setTitle("HumanNetwork Server");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel contents = new JPanel();
		contents.setSize(800, 600);
		setLayout(null);
		contents.setLayout(null);

		JScrollPane scrollPaneNodes = new JScrollPane();
		scrollPaneNodes.setBounds(12, 12, 180, 374);
		contents.add(scrollPaneNodes);
		JList<Connection> listNodes = new JList<Connection>(connections);
		scrollPaneNodes.setViewportView(listNodes);

		JScrollPane scrollPaneLinks = new JScrollPane();
		scrollPaneLinks.setBounds(204, 12, 180, 374);
		contents.add(scrollPaneLinks);
		JList<Link> listLinks = new JList<Link>(links);
		scrollPaneLinks.setViewportView(listLinks);

		JScrollPane scrollPanePackets = new JScrollPane();
		scrollPanePackets.setBounds(396, 12, 386, 549);
		contents.add(scrollPanePackets);
		JList<Message> listPackets = new JList<Message>(messages);
		listPackets.setVisibleRowCount(20);
		scrollPanePackets.setViewportView(listPackets);

		JButton btnCircular = new JButton("Create Node Cycle");
		btnCircular.setBounds(12, 398, 180, 25);
		contents.add(btnCircular);

		JButton btnDeleteLink = new JButton("Delete Link");
		btnDeleteLink.setBounds(12, 469, 180, 25);
		contents.add(btnDeleteLink);

		JButton btnNextStage = new JButton(
				"<html><font color='green'>Start Next Task</font>");
		btnNextStage.setBounds(12, 519, 180, 25);
		contents.add(btnNextStage);

		JLabel lblRandomDelay = new JLabel("Random delay (s):");
		lblRandomDelay.setBounds(204, 480, 140, 15);
		contents.add(lblRandomDelay);

		JSpinner spinDelay = new JSpinner();
		spinDelay.setModel(new SpinnerNumberModel(0, 0, 60, 1));
		spinDelay.setBounds(340, 478, 44, 20);
		contents.add(spinDelay);

		JLabel lblRecipientOffset = new JLabel("Recipient Offset:");
		lblRecipientOffset.setBounds(204, 508, 140, 15);
		contents.add(lblRecipientOffset);

		JSpinner spinOffset = new JSpinner();
		spinOffset.setModel(new SpinnerNumberModel(0, -3, 3, 1));
		spinOffset.setBounds(340, 506, 44, 20);
		contents.add(spinOffset);

		JLabel lblDropRate = new JLabel("Drop rate:");
		lblDropRate.setBounds(204, 395, 118, 15);
		contents.add(lblDropRate);

		JLabel labelDrop = new JLabel("0 %");
		labelDrop.setHorizontalAlignment(SwingConstants.RIGHT);
		labelDrop.setBounds(294, 395, 90, 15);
		contents.add(labelDrop);

		JSlider sliderFailure = new JSlider();
		sliderFailure.setValue(0);
		sliderFailure.setBounds(204, 408, 184, 25);
		contents.add(sliderFailure);

		JButton btnCreateLink = new JButton("Connect Two Nodes");
		btnCreateLink.setBounds(12, 432, 180, 25);
		contents.add(btnCreateLink);

		JCheckBox chckbxWhoisOnly = new JCheckBox("Whois only");
		chckbxWhoisOnly.setBounds(230, 534, 140, 23);
		contents.add(chckbxWhoisOnly);

		JLabel lblCorruptionRate = new JLabel("Corruption rate:");
		lblCorruptionRate.setBounds(204, 435, 118, 15);
		contents.add(lblCorruptionRate);

		JLabel labelCorruption = new JLabel("0 %");
		labelCorruption.setHorizontalAlignment(SwingConstants.RIGHT);
		labelCorruption.setBounds(294, 435, 90, 15);
		contents.add(labelCorruption);

		JSlider sliderCorruption = new JSlider();
		sliderCorruption.setValue(0);
		sliderCorruption.setBounds(204, 448, 184, 25);
		contents.add(sliderCorruption);

		Formatter f = new Formatter();
		f.format("<html><head><style type='text/css'>");
		f.format("body { color: #4444ff; font-weight: bold;}");
		f.format("table { border-collapse: collapse;}");
		f.format("span.value {color: black; }");
		f.format("table td { padding-left:6mm; padding-right: 6mm; border: 1px solid black; text-align: center}");
		String serverStatusLike = "<table><tr><td>#msgs( <span class='value'>%04d</span> )</td><td>delay( <span class='value'>%02d</span> )</td><td> drop( <span class='value'>%02d%%</span> ) </td><td> corruption( <span class='value'>%02d%%</span> )</td><td>offset( <span class='value'>%02d</span> ) </td><td> whois( <span class='value'>%.1B</span> )</td></tr></table></html>";
		f.format(serverStatusLike, messages.size(), links.getDelay(),
				links.getDropRate(), links.getCorruptionRate(), links.getOffset(),
				links.isCheckwhois());
		JLabel serverStatus = new JLabel(f.toString(), SwingConstants.CENTER);
		f.close();

		serverStatus.setBounds(12, 572, 800 - 24, 25);
		contents.add(serverStatus);

		controller.bind(listNodes, listLinks, btnCircular, btnDeleteLink,
				labelDrop, sliderFailure, spinDelay, btnCreateLink, chckbxWhoisOnly,
				labelCorruption, sliderCorruption, btnNextStage, spinOffset,
				serverStatus);
		setSize(800, 620);
		setResizable(false);
		setContentPane(contents);
	}

	public Dimension getMinimumSize() {
		return new Dimension(800, 620);
	}

	public Dimension getPreferredSize() {
		return new Dimension(800, 620);
	}

}
