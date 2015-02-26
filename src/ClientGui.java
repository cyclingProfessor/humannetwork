import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

public class ClientGui extends JFrame {

	private JTextField txtMessage;
	private JScrollPane scrollPane = new JScrollPane();

	private JButton btnAdd = new JButton("Add");
	private JButton btnSend = new JButton("Send");
	private JButton btnSplit = new JButton("Split");
	private JButton btnDelete = new JButton("Delete");
	private JLabel lblMessage = new JLabel("Message: ");
	private JButton btnVerifyChecksum = new JButton("Verify Checksum");
	private JButton btnMoveUp = new JButton("Move Up");
	private JButton btnEncrypt = new JButton("Encrypt");
	private JButton btnNonce = new JButton("Add Random number");
	private JLabel lblGroup;
	private JLabel lblNode_1;
	private JButton btnAddCheck = new JButton("Add Checksum");

	private JButton btnMerge = new JButton("Merge");

	private JButton btnMoveDown = new JButton("Move Down");
	private JButton btnDecrypt = new JButton("Decrypt");
	private JList<Message> list;

	/**
	 * Initialise the contents of the 
	 */
	public void initialize(Connection c, MessageList messages, ClientController control) {
		setTitle("[" + c.group + "] Node " + c.node + " @ HumanNetwork");
		setBounds(100, 100, 800, 600);
		final int SCREEN_HEIGHT = (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight());
		final int SCREEN_WIDTH = (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth());
                final int MAX_WIDTH = (4 * SCREEN_HEIGHT) / 3;
		final Rectangle maxRect = new Rectangle((SCREEN_WIDTH - MAX_WIDTH) / 2, 0, MAX_WIDTH, SCREEN_HEIGHT);
		final Dimension maxSize = new Dimension(MAX_WIDTH, SCREEN_HEIGHT);
		setMaximumSize(maxSize);
		setMaximizedBounds(maxRect);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);
		
		txtMessage = new JTextField();
		txtMessage.setToolTipText("Enter your new message here");
		txtMessage.setBounds(111, 45, 447, 22);
		getContentPane().add(txtMessage);
		txtMessage.setColumns(10);
		
		lblGroup = new JLabel("Group: " + c.group);
		lblNode_1 = new JLabel("Node: " + c.node);
		
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(12, 79, 546, 482);
		getContentPane().add(scrollPane);
		
		//MessageList listModel = new MessageList();
		list = new JList<Message>(messages);
		list.setToolTipText("Messages built, sent and received");
		scrollPane.setViewportView(list);

		btnAdd.setToolTipText("Press to add the message content to the list of messages");
		btnAdd.setBounds(570, 43, 212, 25);
		getContentPane().add(btnAdd);
		
		btnSend.setToolTipText("Press to send the current message to someone on the network");
		btnSend.setBounds(570, 9, 212, 25);
		getContentPane().add(btnSend);
		
		btnSplit.setToolTipText("Press to split the message into small chunks");
		btnSplit.setBounds(570, 264, 212, 25);
		getContentPane().add(btnSplit);
		
		btnMerge.setToolTipText("Press to merge the selected messages");
		btnMerge.setBounds(570, 227, 212, 25);
		getContentPane().add(btnMerge);
		
		btnAddCheck.setToolTipText("Press to add a checksum at the end of the current messsage");
		btnAddCheck.setBounds(570, 339, 212, 25);
		getContentPane().add(btnAddCheck);
		
		btnDelete.setToolTipText("Press to delete the selected messages");
		btnDelete.setBounds(570, 536, 212, 25);
		getContentPane().add(btnDelete);
		
		lblMessage.setBounds(12, 48, 81, 15);
		getContentPane().add(lblMessage);

		btnNonce.setToolTipText("Press to add a random number to the current message");
		btnNonce.setBounds(570, 487, 212, 25);
		getContentPane().add(btnNonce);
		
		btnVerifyChecksum.setToolTipText("Press to verify if the checksum of the current message is correct");
		btnVerifyChecksum.setBounds(570, 376, 212, 25);
		getContentPane().add(btnVerifyChecksum);
		
		btnMoveUp.setToolTipText("Press to make the currently selected message move up in the list");
		btnMoveUp.setBounds(570, 116, 212, 25);
		getContentPane().add(btnMoveUp);
		
		btnMoveDown.setToolTipText("Press to make the currently selected message move down in the list");
		btnMoveDown.setBounds(570, 153, 212, 25);
		getContentPane().add(btnMoveDown);
		
		btnEncrypt.setToolTipText("Press to encrypt the current message");
		btnEncrypt.setBounds(570, 413, 212, 25);
		getContentPane().add(btnEncrypt);
		
		btnDecrypt.setToolTipText("Press to decrypt the current message");
		btnDecrypt.setBounds(570, 450, 212, 25);
		getContentPane().add(btnDecrypt);
		
		lblGroup.setBounds(12, 14, 274, 15);
		getContentPane().add(lblGroup);
		
		lblNode_1.setBounds(298, 14, 260, 15);
		getContentPane().add(lblNode_1);
		
		control.bind(txtMessage, list, btnAdd, btnSend, btnNonce, btnSplit,
				btnMerge, btnAddCheck, btnDelete, btnVerifyChecksum, btnMoveUp, 
				btnMoveDown, btnEncrypt, btnDecrypt);
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				//if (getWidth() > MAX_FRAME_WIDTH) {
				//	setSize(MAX_FRAME_WIDTH, getHeight());
				//}
				float size = (12f * getWidth() / 800);
				
				Font f = btnAdd.getFont();
				UIManager.put("Button.font", new FontUIResource(f.deriveFont(size)));

 				f = lblMessage.getFont();
				UIManager.put("Label.font", new FontUIResource(f.deriveFont(size)));
        
				f = UIManager.getFont("ToolTip.font");
				UIManager.getLookAndFeelDefaults().put("ToolTip.font", new FontUIResource(f.deriveFont(size)));
         
				SwingUtilities.updateComponentTreeUI(e.getComponent());
         
				f = txtMessage.getFont();
				size = (12f * getWidth() / 800);
	 			final Font res = f.deriveFont(size);
	 			list.setFont(res);
	 			txtMessage.setFont(res);

				txtMessage.setBounds(propSize(111), propSize(45), propSize(447), propSize(22));
				btnAdd.setBounds(propSize(570), propSize(43), propSize(212), propSize(25));
				btnSend.setBounds(propSize(570), propSize(9), propSize(212), propSize(25));
				btnSplit.setBounds(propSize(570), propSize(264), propSize(212), propSize(25));
				btnMerge.setBounds(propSize(570), propSize(227), propSize(212), propSize(25));
				btnAddCheck.setBounds(propSize(570), propSize(339), propSize(212), propSize(25));
				btnDelete.setBounds(propSize(570), propSize(536), propSize(212), propSize(25));
				lblMessage.setBounds(propSize(12), propSize(48), propSize(81), propSize(15));
				btnNonce.setBounds(propSize(570), propSize(487), propSize(212), propSize(25));
				btnVerifyChecksum.setBounds(propSize(570), propSize(376), propSize(212), propSize(25));
				btnMoveUp.setBounds(propSize(570), propSize(116), propSize(212), propSize(25));
				btnMoveDown.setBounds(propSize(570), propSize(153), propSize(212), propSize(25));
				btnEncrypt.setBounds(propSize(570), propSize(413), propSize(212), propSize(25));
				btnDecrypt.setBounds(propSize(570), propSize(450), propSize(212), propSize(25));
				lblGroup.setBounds(propSize(12), propSize(14), propSize(274), propSize(15));
				lblNode_1.setBounds(propSize(298), propSize(14), propSize(260), propSize(15));

				scrollPane.setBounds(propSize(12), propSize(79), propSize(546), getHeight() - propSize(118));

			}		
		});
		
	}
	
	private int propSize(int val) {
		return (val * getWidth()) / 800;
	}

	public JLabel getStatusField() {
		return null;
	}

	


}
