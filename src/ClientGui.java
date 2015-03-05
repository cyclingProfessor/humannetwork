import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
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

@SuppressWarnings("serial")
public class ClientGui extends JFrame {
	// LAYOUT - set up constants which can be multiplied by window width
	private final static float BORDER = 1.5f / 100f;

	private final static float BTN_WIDTH = 25 / 100f;
	private final static float BTN_HEIGHT = 3 / 100f;
	private final static float BTN_TOTAL_HEIGHT = BTN_HEIGHT + BORDER;
	
	private static final float STATUS_HEIGHT = 1 / 10f;
	
	private static final float MSG_WIDTH = 15 / 100f;
	private static final float MSG_HEIGHT = 3 / 100f;
	private final static float TXT_WIDTH = 1.0f - MSG_WIDTH;

	private final static float STATUS_TOP = BORDER;
	private final static float MSG_TOP = STATUS_TOP + STATUS_HEIGHT + BORDER;
	private final static float LIST_TOP = MSG_TOP + MSG_HEIGHT + BORDER;
	
	private static int INITIAL_WIDTH = 800;
	private static int INITIAL_HEIGHT = 680;

	private static float posn = STATUS_HEIGHT + 2 * BORDER;

	private enum BTN_DETAILS {
		SEND(posn, "Send","Press to send the current message to someone on the network", 1), 
		ADD(posn, "Add","Press to add the message content to the list of messages", 2),
		MOVE_UP(posn, "Move Up","Press to make the currently selected message move up in the list", 1),
		MOVE_DOWN(posn, "Move Down","Press to make the currently selected message move down in the list", 2),
		MERGE(posn, "Combine", "Press to merge the selected messages", 1),
		SPLIT(posn, "Split", "Press to split the message into small chunks", 2),
		ADD_CHECK(posn, "Add Checksum","Press to add a checksum at the end of the current messsage", 1),
		VERIFY(posn, "Verify Checksum","Press to verify if the checksum of the current message is correct", 1),
		ENCRYPT(posn, "Encrypt", "Press to encrypt the current message", 1),
		DECRYPT(posn, "Decrypt", "Press to decrypt the current message", 1),
		NONCE(posn, "Add Random Number","Press to add a random number to the current message", 2),
		DELETE(posn,"Delete", "Press to delete the selected messages", 1);

		private float offset;
		private JButton button;
		private String toolTip;

		BTN_DETAILS(float offset, String label, String tip, int gap) {
			this.offset = offset;
			this.button = new JButton(label);
			this.toolTip = tip;
			posn += gap * BTN_TOTAL_HEIGHT;
		}

		public JButton button() {
			return button;
		}

		public String toolTip() {
			return toolTip;
		}

		public float offset() {
			return offset;
		}
	}

	private JTextField txtMessage = new JTextField();
	private JScrollPane scrollPane = new JScrollPane();

	private JLabel lblMessage = new JLabel("Message: ");
	private JList<Message> list;
	private JLabel status;

	/**
	 * Initialise the contents of the
	 */
	public void initialize(Connection c, MessageList messages,
			ClientController control) {
		setTitle("[" + c.getGroup() + "] Node " + c.getNode() + " @ HumanNetwork");
		setBounds(100, 100, INITIAL_WIDTH, INITIAL_HEIGHT);
		setMinimumSize(new Dimension(INITIAL_WIDTH, INITIAL_HEIGHT));
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);

		for (BTN_DETAILS btn : BTN_DETAILS.values()) {
			JButton next = btn.button();
			next.setToolTipText(btn.toolTip());
			getContentPane().add(next);
		}

		txtMessage.setToolTipText("Enter your new message here");
		getContentPane().add(txtMessage);

		scrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		getContentPane().add(scrollPane);

		// MessageList listModel = new MessageList();
		list = new JList<Message>(messages);
		list.setToolTipText("Messages built, sent and received");
		scrollPane.setViewportView(list);

		StringBuffer initialMessage = new StringBuffer();
		initialMessage.append("<html><head><style type='text/css'>");
		initialMessage.append("body { color: #4444ff; font-weight: normal;}");
		initialMessage.append("div { width: 100%; text-align: center}");
		initialMessage.append("<div>Welcome to the DarkNet.  <br>"
				+ "You are in group: " + c.getGroup() + ". Your node number is "
				+ c.getNode() + ".<br>" + "Please wait for instructions.</div>");
		status = new JLabel(initialMessage + "", SwingConstants.CENTER);
		status.setBorder(BorderFactory.createLineBorder(Color.red));

		getContentPane().add(lblMessage);
		getContentPane().add(status);
		setSizes();

		control.bind(txtMessage, list, BTN_DETAILS.ADD.button(),
				BTN_DETAILS.SEND.button(), BTN_DETAILS.NONCE.button(),
				BTN_DETAILS.SPLIT.button(), BTN_DETAILS.MERGE.button(),
				BTN_DETAILS.ADD_CHECK.button(), BTN_DETAILS.DELETE.button(),
				BTN_DETAILS.VERIFY.button(), BTN_DETAILS.MOVE_UP.button(),
				BTN_DETAILS.MOVE_DOWN.button(), BTN_DETAILS.ENCRYPT.button(),
				BTN_DETAILS.DECRYPT.button());

		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				setSizes();
			}
		});

	}

	private void setSizes() {
		// Set all fonts relative to window height
		float size = (12f * getHeight() / INITIAL_HEIGHT);

		Font f = BTN_DETAILS.ADD.button().getFont();
		UIManager.put("Button.font", new FontUIResource(f.deriveFont(size)));

		f = lblMessage.getFont();
		UIManager.put("Label.font", new FontUIResource(f.deriveFont(size)));

		f = UIManager.getFont("ToolTip.font");
		UIManager.getLookAndFeelDefaults().put("ToolTip.font", new FontUIResource(f.deriveFont(size)));

		SwingUtilities.updateComponentTreeUI(this);

		f = txtMessage.getFont();
		final Font res = f.deriveFont(size);
		list.setFont(res);
		txtMessage.setFont(res);

		// set all button positions and sizes relative to window height
		for (BTN_DETAILS btn : BTN_DETAILS.values()) {
			JButton next = btn.button();
			setButtonBounds(next, btn.offset());
		}
		setStatusBounds(status);
		setRelativeBounds(lblMessage, 0, MSG_TOP, MSG_WIDTH, MSG_HEIGHT);
		setRelativeBounds(txtMessage, MSG_WIDTH, MSG_TOP, TXT_WIDTH, MSG_HEIGHT);
		setRelativeBounds(scrollPane, 0, LIST_TOP, 1.0f,	BTN_DETAILS.DELETE.offset() + BTN_HEIGHT - LIST_TOP);
	}

	private void setButtonBounds(JButton b, float y) {
		float h_factor = 1.14f * getHeight();
		int left = Math.round(getWidth() - h_factor * (2 * BORDER + BTN_WIDTH));
		b.setBounds(left, Math.round(h_factor * y), Math.round(h_factor * BTN_WIDTH), Math.round(h_factor * BTN_HEIGHT));
	}

	private void setRelativeBounds(Component c, float x, float y, float width, float height) {
		float h_factor = 1.14f * getHeight();
		float w_factor = getWidth() - h_factor * (4 * BORDER + BTN_WIDTH);
		c.setBounds(Math.round(h_factor * BORDER + w_factor * x), Math.round(h_factor * y), Math.round(w_factor * width), Math.round(h_factor * height));
	}

	private void setStatusBounds(Component c) {
		float h_factor = 1.14f * getHeight();
		int border = Math.round(h_factor * STATUS_TOP);
		c.setBounds(border, border, getWidth() - 3 * border, Math.round(h_factor * STATUS_HEIGHT));
	}

	public JLabel getStatusField() {
		return status;
	}

}
