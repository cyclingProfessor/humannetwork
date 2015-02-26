import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

public class ClientGui extends JFrame {
	// LAYOUT - set up constants which can be multiplied by window width
	private final static float BTN_WIDTH = 25/100f;
	private final static float BTN_LEFT = 71/100f;
	private final static float BORDER = 1.5f/100f;
	private final static float BTN_HEIGHT = 3/100f;
	private final static float BTN_TOTAL_HEIGHT = BTN_HEIGHT + BORDER;
	private static final float STATUS_HEIGHT = 1/10f;
	private static final float MSG_WIDTH = 11/100f;
	private static final float MSG_HEIGHT = 3/100f;
	private static int INITIAL_WIDTH = 800;
	
	private static float posn = STATUS_HEIGHT + 2 * BORDER;
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
	
	private JTextField txtMessage = new JTextField();
	private JScrollPane scrollPane = new JScrollPane();

	private JLabel lblMessage = new JLabel("Message: ");
	private JList<Message> list;
	private JLabel status ;
	
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
		setBounds(100, 100, INITIAL_WIDTH, (INITIAL_WIDTH * 350) / 400);
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
		
		txtMessage.setToolTipText("Enter your new message here");
		getContentPane().add(txtMessage);
		
		
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		getContentPane().add(scrollPane);
		
		//MessageList listModel = new MessageList();
		list = new JList<Message>(messages);
		list.setToolTipText("Messages built, sent and received");
		scrollPane.setViewportView(list);

		StringBuffer initialMessage = new StringBuffer();
		initialMessage.append("<html><head><style type='text/css'>");
		initialMessage.append("body { color: #4444ff; font-weight: normal;}");
		initialMessage.append("div { width: 100%; text-align: center}");
		initialMessage.append("<div>Welcome to the DarkNet.  <br>" +
		    "You are in group: " + c.group + ". Your node number is " + c.node + ".<br>" + 
				"Please wait for instruction.</div>");
		status = new JLabel(initialMessage + "",  SwingConstants.CENTER);
		status.setBorder(BorderFactory.createLineBorder(Color.red));

		getContentPane().add(lblMessage);
		getContentPane().add(status);
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
		float h_offset = BORDER;
		setRelativeBounds(status, BORDER, h_offset, BTN_LEFT + BTN_WIDTH - BORDER, STATUS_HEIGHT); h_offset += STATUS_HEIGHT + BORDER;
		setRelativeBounds(lblMessage, BORDER, h_offset, MSG_WIDTH, MSG_HEIGHT); 
		setRelativeBounds(txtMessage, MSG_WIDTH, h_offset, BTN_LEFT - BORDER, MSG_HEIGHT); h_offset += MSG_HEIGHT + BORDER;
		setRelativeBounds(scrollPane, BORDER, h_offset, BTN_LEFT - 3 * BORDER, BTN_DETAILS.DELETE.offset() + BTN_HEIGHT - h_offset);
	}
	
	private void setRelativeBounds(Component c, float x, float y, float width, float height) {
		int w = getWidth();
		c.setBounds(Math.round(w * x), Math.round(w * y), Math.round(w * width), Math.round(w * height));
	}
}
