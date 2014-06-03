import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JList;



public class ClientGui {

	JFrame frmDarknet;
	private JTextField txtMessage;

	/**
	 * Create the application.
	 */
	public ClientGui(Connection c, MessageList messages) {
		initialize(c, messages);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize(Connection c, MessageList messages) {
		frmDarknet = new JFrame();
		frmDarknet.setTitle(c.id + " @ DarkNet node " + c.node);
		frmDarknet.setBounds(100, 100, 800, 600);
		frmDarknet.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmDarknet.getContentPane().setLayout(null);
		
		txtMessage = new JTextField();
		txtMessage.setToolTipText("Enter your new message here");
		txtMessage.setBounds(111, 45, 447, 22);
		frmDarknet.getContentPane().add(txtMessage);
		txtMessage.setColumns(10);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(12, 79, 546, 482);
		frmDarknet.getContentPane().add(scrollPane);
		
		//MessageList listModel = new MessageList();
		final JList<String> list = new JList<String>(messages);
		scrollPane.setViewportView(list);

		JButton btnAdd = new JButton("Add");
		btnAdd.setBounds(570, 9, 94, 25);
		frmDarknet.getContentPane().add(btnAdd);
		
		JButton btnSend = new JButton("Send");
		btnSend.setBounds(688, 9, 94, 25);
		frmDarknet.getContentPane().add(btnSend);
		
		JButton btnSplit = new JButton("Split");
		btnSplit.setBounds(570, 190, 212, 25);
		frmDarknet.getContentPane().add(btnSplit);
		
		JButton btnMerge = new JButton("Merge");
		btnMerge.setBounds(570, 227, 212, 25);
		frmDarknet.getContentPane().add(btnMerge);
		
		JButton btnAddCheck = new JButton("Add Checksum");
		btnAddCheck.setBounds(570, 301, 212, 25);
		frmDarknet.getContentPane().add(btnAddCheck);
		
		JButton btnDelete = new JButton("Delete");
		btnDelete.setBounds(570, 536, 212, 25);
		frmDarknet.getContentPane().add(btnDelete);
		
		JLabel lblMessage = new JLabel("Message: ");
		lblMessage.setBounds(12, 48, 81, 15);
		frmDarknet.getContentPane().add(lblMessage);

		JButton btnNonce = new JButton("Add Random number");
		btnNonce.setBounds(570, 487, 212, 25);
		frmDarknet.getContentPane().add(btnNonce);
		
		JButton btnVerifyChecksum = new JButton("Verify Checksum");
		btnVerifyChecksum.setBounds(570, 338, 212, 25);
		frmDarknet.getContentPane().add(btnVerifyChecksum);
		
		JButton btnMoveUp = new JButton("Move Up");
		btnMoveUp.setBounds(570, 116, 212, 25);
		frmDarknet.getContentPane().add(btnMoveUp);
		
		JButton btnMoveDown = new JButton("Move Down");
		btnMoveDown.setBounds(570, 79, 212, 25);
		frmDarknet.getContentPane().add(btnMoveDown);
		
		JButton btnEncrypt = new JButton("Encrypt");
		btnEncrypt.setBounds(570, 413, 212, 25);
		frmDarknet.getContentPane().add(btnEncrypt);
		
		JButton btnDecrypt = new JButton("Decrypt");
		btnDecrypt.setBounds(570, 450, 212, 25);
		frmDarknet.getContentPane().add(btnDecrypt);
		
		Controller control = new Controller(c, messages);
		control.bind(txtMessage, list, btnAdd, btnSend, btnNonce, btnSplit,
				btnMerge, btnAddCheck, btnDelete, btnVerifyChecksum, btnMoveUp, 
				btnMoveDown, btnEncrypt, btnDecrypt);
		
		JLabel lblNode = new JLabel("Node: " + c.node);
		lblNode.setBounds(12, 14, 70, 15);
		frmDarknet.getContentPane().add(lblNode);
		
		
	}
}
