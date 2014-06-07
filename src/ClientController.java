import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public class ClientController {

	Random rand = new Random();
	MessageList messages;
	Connection c;
	
	public ClientController (Connection c, MessageList messages){
		this.messages = messages;
		this.c = c;
	}
	
	public static String chunk(int i, int j){
		return "<" + (i) +"/" + (j) + ">";
	}
	
	public static int totalFromChunk(String s){
		System.out.println("Chunk " + s);
		String[] parts = s.split("/");
		if (parts.length < 2){
			return 0;
		} else {
			String half = parts[1];
			System.out.println("Half " + half);
			if (half.length() < 1){
				return 0;
			} else {
				try {
					String total = half.substring(0, half.length()-1);
					System.out.println("Total " + total);
					return Integer.parseInt(total);
				} catch (Exception e) {
					e.printStackTrace();
					return 0;
				}	
			}
		}
	}
	
	public static int pieceFromChunk(String s){
		System.out.println("Chunk " + s);
		String[] parts = s.split("/");
		if (parts.length < 2){
			return 0;
		} else {
			String half = parts[0];
			try {
				return Integer.parseInt(half);
			} catch (Exception e) {
				e.printStackTrace();
				return 0;
			}
		}
	}
	
	public void bind(final JTextField txtMessage, final JList<Message> list,
			JButton btnAdd, JButton btnSend, JButton btnNonce,
			JButton btnSplit, JButton btnMerge, JButton btnAddCheck,
			JButton btnDelete, JButton btnVerifyChecksum, JButton btnMoveUp, 
			JButton btnMoveDown, JButton btnEncrypt, JButton btnDecrypt){


		list.addListSelectionListener(new ListSelectionListener(){
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				int[] array = list.getSelectedIndices();
				System.out.print("Selected rows: ");
				for(int i : array){
					System.out.print(i + " ");
				}
				System.out.println();
				
				if(array.length == 1){
					txtMessage.setText(messages.get(array[0]).getContent());
				}
			}
		});

		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String message = txtMessage.getText();
				System.out.println("Add clicked "+ txtMessage.getText());
				messages.addMessage(message);
			}
		});

		btnSend.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String message = txtMessage.getText();
				String to = (String) JOptionPane.showInputDialog("Node number of the recipient (0 for broadcast)");
				try {
					int toNode = Integer.parseInt(to);
					String rawMessage = toNode + "" + (char) 13 + message;
					if (message.length() > 40){
						JOptionPane.showMessageDialog(txtMessage, "Message is too long!");
					} else {
						c.write(rawMessage);
						messages.addMessage(c.node, toNode, c.node, message);
						System.out.println("Sent:" + rawMessage);
					}
				}
				catch(Exception ex){
					System.out.println("Some problem sending ...");
				}
			}
		});
		
		btnNonce.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Nonce clicked");
				String message = txtMessage.getText();
				txtMessage.setText(message + "|" + (1000 + rand.nextInt(9000)));
			}
		});
		
		btnSplit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String message = txtMessage.getText();
				System.out.println("Split clicked with " + message);
				//String nonce = "%" + (1000 + rand.nextInt(9000));
				int length = message.length();
				int size = 30;
				System.out.println("Length is " + length);
				System.out.println("Splitting in " + (1 + length/size));
				for(int i = 0 ; i < 1 + length / size; i++){
					int end = ((i+1)*size <= length) ? (i+1)*size : length;
					//String part = chunk(i+1, 1+length/size);
					messages.addMessage(message.substring(i*size, end));
				}
			}
		});

		btnMerge.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Merge clicked");
				int[] selected = list.getSelectedIndices();
				//String[] pieces = new String[selected.length];
				System.out.println("Selected " + selected.length);
				String message = "";
				for(int i = 0; i< selected.length; i++){
					message = message + messages.get(selected[i]).getContent();
				}
				messages.addMessage(message);
			}
		});
		
		btnAddCheck.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					System.out.println("Add Checksum clicked");
					String message = txtMessage.getText();
					int sum = 0 ;
					for (char c : message.toCharArray()){
						sum = (sum + c) % 100;
					}
					txtMessage.setText(message + ":" + (String.format("%02d", sum)));
			}
		});

		btnVerifyChecksum.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Verify Checksum clicked");
				String message = txtMessage.getText();
				boolean correct = false;
				if(message.length()>3){
					String content = message.substring(0,message.length()-3);
					int sum = 0 ;
					for (char c : content.toCharArray()){
						sum = (sum + c) % 100;
					}
					try {
						int checksum = Integer.parseInt(message.substring(message.length()-2, message.length()));
						correct = sum == checksum;
					} catch (Exception e){
						System.out.println("Incorrect checksum");
					}
				}
				if(correct){
					JOptionPane.showMessageDialog(txtMessage,"Correct!");
				} else {
					JOptionPane.showMessageDialog(txtMessage,"Error!");
				}
			}
		});

		btnMoveUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int[] array = list.getSelectedIndices();
				System.out.println("MoveUp clicked");
				if(array.length == 1 && array[0] != 0){
					messages.moveUp(array[0]);
					list.setSelectedIndex(array[0]-1);
				}
			}
		});
		
		btnMoveDown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int[] array = list.getSelectedIndices();
				System.out.println("MoveDown clicked");
				if(array.length == 1 && array[0] != messages.size()-1){
					messages.moveDown(array[0]);
					list.setSelectedIndex(array[0]+1);
				}
			}
		});
		
		btnEncrypt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String message = txtMessage.getText();
				String encrypted = "";
				try {
					for(char c : message.toCharArray()){
						encrypted = encrypted + String.format("%02x", (int) c);
					}
					txtMessage.setText(encrypted);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		btnDecrypt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String message = txtMessage.getText();
				String decrypted = "";
				for(int i = 0; 2*i+2<= message.length(); i++){
					String c = message.substring(i*2,i*2+2);
					decrypted = decrypted + (char) Integer.decode("0x"+c).intValue();
				}
				txtMessage.setText(decrypted);
			}
		});
		
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int[] array = list.getSelectedIndices();
				System.out.println("Delete clicked");
				for(int i : array){
					messages.remove(i);
				}
			}
		});
	}
	
}
