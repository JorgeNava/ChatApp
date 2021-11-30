package Client;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JLabel;
import javax.swing.JList;

import java.awt.Dimension;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

public class PrivateChat extends JPanel {
	PrivateChatConfig chatConfig;
	JTextArea display;
	JScrollPane scroll;
	JTextField messageField;
	JButton sendBtn;
	
	public PrivateChat() {
		setLayout(null);
		this.setPreferredSize(new Dimension(420, 177));

		display = new JTextArea(16, 58);
	    display.setEditable(false);
	    scroll = new JScrollPane(display);
	    scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
	    scroll.setBounds(10, 5, 400, 120);
		add(scroll);

		messageField = new JTextField();
		messageField.setText("");
		messageField.setBounds(10, 135, 331, 35);
		add(messageField);
		messageField.setColumns(10);
		
		sendBtn = new JButton("SEND");
		sendBtn.setHorizontalAlignment(SwingConstants.RIGHT);
		sendBtn.setBounds(342, 135, 67, 15);
		add(sendBtn);
		
		sendBtn.addActionListener(e -> {
			updateChat(chatConfig.originClient, messageField.getText());
			messageField.setText("");
        });
	}
	
	void updateChat(User user, String message) {
		this.display.append(user.alias + ": " + message + "\n");
		this.display.update(this.display.getGraphics());
		this.chatConfig.updateStoredConversation(user.alias + ": " + message + "\n");
	}
	
	void setConfig(PrivateChatConfig config) {
		this.chatConfig = config;
		this.display.setText(this.chatConfig.storedConversation);
		this.display.update(this.display.getGraphics());
	}
}


