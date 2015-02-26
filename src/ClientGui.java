import java.awt.Component;
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
	// LAYOUT - set up constants which can be multiplied by window width
	private final static float BTN_WIDTH = 25/100f;
	private final static float BTN_LEFT = 71/100f;
	private final static float BORDER = 1.5f/100f;
	private final static float BTN_HEIGHT = 30/100f;
	private final static float BTN_TOTAL_HEIGHT = BTN_HEIGHT + BORDER;
	private static int INITIAL_WIDTH = 800;

	private final float TOP_CONTENT = 0f;
	
	private static float posn = 1/100f;
	private enum BTN_DETAILS {
		SEND     (posn, "Send", "Press to send the current message to someone on the network", 1), 
		ADD      (posn,"Add","Press to add the message content to the list of messages", 2),
		MOVE_UP  (posn, "Move Up", "Press to make the currently selected message move up in the list", 1), 
		MOVE_DOWN(posn, "Move Down", "Press to make the currently selected message move down in the list", 2), 
		MERGE    (posn, "", "Press to merge the selected messages", 1),
		SPLIT    (posn, "Split", "Press to split the message into small chunks", 2), 
		ADD_CHECK(posn, "Add Checksum", "Press to add a checksum at the end of the current messsage", 1), 
		VERIFY   (posn, "Verify Checksum", "Press to verify if the checksum of the current message is correct", 1), 
		ENCRYPT  (posn, "Encrypt", "Press to encrypt the current message", 1), 
		DECRYPT  (posn, "Decrypt", "Press to decrypt the current message", 1), 
		NONCE    (posn, "Add Random Number", "Press to add a random number to the current message", 2), 
		DELETE   (posn, "Delete", "Press to delete the selected messages", 1);
		
		private float offset;
		private JButton button;
		private String toolTip;
		
		BTN_DETAILS(float offset, String label, String tip, int gap) {
			this.offset = offset;
			this.button = new JButton(label);
			this.toolTip = tip;
			posn += gap * BTN_TOTAL_HEIGHT;
		}
		public JButton button() { return button; }
		public String toolTip() { return toolTip; }
		public float offset() { return offset; }
	}
	
	private JTextField txtMessage;
	private JScrollPane scrollPane = new JScrollPane();

	private JLabel lblMessage = new JLabel("Message: ");
	private JLabel lblGroup;
	private JLabel lblNode_1;
	private JList<Message> list;

	/**
	 * Create the application.
	 */
	public ClientGui(Connection c, MessageList messages, ClientController control) {
		initialize(c, messages, control);
	}

	/**
	 * Initialise the contents of the 
	 */
	private void initialize(Connection c, MessageList messages, ClientController control) {
		setTitle("[" + c.group + "] Node " + c.node + " @ HumanNetwork");
		setBounds(100, 100, INITIAL_WIDTH, (INITIAL_WIDTH * 4) / 3);
		final int SCREEN_HEIGHT = (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight());
		final int SCREEN_WIDTH = (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth());
                final int MAX_WIDTH = (4 * SCREEN_HEIGHT) / 3;
		final Rectangle maxRect = new Rectangle((SCREEN_WIDTH - MAX_WIDTH) / 2, 0, MAX_WIDTH, SCREEN_HEIGHT);
		final Dimension maxSize = new Dimension(MAX_WIDTH, SCREEN_HEIGHT);
		setMaximumSize(maxSize);
		setMaximizedBounds(maxRect);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);
		
		for (BTN_DETAILS btn: BTN_DETAILS.values()) {
			JButton next = btn.button();
			next.setToolTipText(btn.toolTip());
			getContentPane().add(next);
		}
		
		txtMessage = new JTextField();
		txtMessage.setToolTipText("Enter your new message here");
		getContentPane().add(txtMessage);
		txtMessage.setColumns(10);
		
		lblGroup = new JLabel("Group: " + c.group);
		lblNode_1 = new JLabel("Node: " + c.node);
		
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		getContentPane().add(scrollPane);
		
		//MessageList listModel = new MessageList();
		list = new JList<Message>(messages);
		list.setToolTipText("Messages built, sent and received");
		scrollPane.setViewportView(list);
		
		getContentPane().add(lblMessage);
		getContentPane().add(lblGroup);
		getContentPane().add(lblNode_1);
    setSizes();
    
		control.bind(txtMessage, list, 
				BTN_DETAILS.ADD.button(), 
				BTN_DETAILS.SEND.button(), 
				BTN_DETAILS.NONCE.button(), 
				BTN_DETAILS.SPLIT.button(), 
				BTN_DETAILS.MERGE.button(), 
				BTN_DETAILS.ADD_CHECK.button(),
				BTN_DETAILS.DELETE.button(), 
				BTN_DETAILS.VERIFY.button(), 
				BTN_DETAILS.MOVE_UP.button(), 
				BTN_DETAILS.MOVE_DOWN.button(), 
				BTN_DETAILS.ENCRYPT.button(), 
				BTN_DETAILS.DECRYPT.button());
		
		
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				setSizes();
			}		
		});
		
	}
	private void setSizes() {
		float size = (12f * getWidth() / INITIAL_WIDTH);

		Font f = BTN_DETAILS.ADD.button().getFont();
		UIManager.put("Button.font", new FontUIResource(f.deriveFont(size)));

		f = lblMessage.getFont();
		UIManager.put("Label.font", new FontUIResource(f.deriveFont(size)));

		f = UIManager.getFont("ToolTip.font");
		UIManager.getLookAndFeelDefaults().put("ToolTip.font", new FontUIResource(f.deriveFont(size)));

		SwingUtilities.updateComponentTreeUI(this);

		f = txtMessage.getFont();
		size = (12f * getWidth() / 800);
		final Font res = f.deriveFont(size);
		list.setFont(res);
		txtMessage.setFont(res);

		for (BTN_DETAILS btn: BTN_DETAILS.values()) {
			JButton next = btn.button();
			setRelativeBounds(next, BTN_LEFT, btn.offset(), BTN_WIDTH, BTN_HEIGHT);
		}
		setRelativeBounds(scrollPane, BORDER, 1/10f, BTN_LEFT - 2 * BORDER, 3/5f);
		setRelativeBounds(lblMessage, BORDER, 6/100f, 1/10f, 2/100f);
		setRelativeBounds(lblGroup, BORDER, 2/100f, 1/3f, 2/100f);
		setRelativeBounds(lblNode_1, 3/8f, 2/100f, 1/3f, 2/100f);
		setRelativeBounds(txtMessage, 14/100f, 56/100f, 55/100f, 3/100f);
	}
	
	private void setRelativeBounds(Component c, float x, float y, float width, float height) {
		int w = getWidth();
		c.setBounds(Math.round(w * x), Math.round(w * y), Math.round(w * width), Math.round(w * height));
	}
}
