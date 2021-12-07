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
import javax.swing.JFileChooser;
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
	JButton sendBtn, sentFileBtn;
	JLabel lblContactAlias;
	JFileChooser fileDialog;

	public PrivateChat() {
		setLayout(null);
		this.setPreferredSize(new Dimension(440, 210));

		lblContactAlias = new JLabel();
		lblContactAlias.setBounds(10, 0, 100, 16);
		add(lblContactAlias);

		display = new JTextArea(16, 78);
		display.setEditable(false);
		scroll = new JScrollPane(display);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setBounds(10, 25, 428, 120);
		add(scroll);

		messageField = new JTextField();
		messageField.setText("");
		messageField.setBounds(10, 155, 331, 35);
		add(messageField);
		messageField.setColumns(10);

		sendBtn = new JButton("SEND     ");
		sendBtn.setHorizontalAlignment(SwingConstants.RIGHT);
		sendBtn.setBounds(342, 155, 95, 15);
		add(sendBtn);

		sentFileBtn = new JButton("SEND FILE");
		sentFileBtn.setHorizontalAlignment(SwingConstants.RIGHT);
		sentFileBtn.setBounds(342, 175, 95, 15);
		add(sentFileBtn);

		fileDialog = new JFileChooser();

		sendBtn.addActionListener(e -> {
			User originClient = this.chatConfig.originClient;
			User recieverClient = this.chatConfig.recieverClient;
			String messageContent = messageField.getText();
			String flag = "PrivateChat";

			Message message = new Message(originClient, recieverClient, messageContent, flag);
			MessageSender msgSender = new MessageSender(message);
			updateChat(message);
			this.chatConfig.updateStoredConversation((message.originUser.alias + ": " + message.message + "\n"));
			msgSender.sendMessage();
			messageField.setText("");
		});

		sentFileBtn.addActionListener(e -> {
			String filePath = "";
			int returnVal = fileDialog.showOpenDialog(this);
			User originClient = this.chatConfig.originClient;
			User recieverClient = this.chatConfig.recieverClient;
			String messageContent = "File sent";
			String flag = "PrivateChat";
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				filePath = fileDialog.getSelectedFile().getAbsolutePath();
				filePath = filePath.replace("\\", "\\\\");
				Message message = new Message(originClient, recieverClient, messageContent, flag);
				message.setFileBytes(filePath);
				MessageSender msgSender = new MessageSender(message);
				updateChat(message);
				this.chatConfig.updateStoredConversation((message.originUser.alias + ": " + message.message + "\n"));
				msgSender.sendMessage();
				messageField.setText("");
			} else {
				System.out.println("File Chooser cancelled");
			}
		});
	}

	void updateChat(Message message) {
		System.out.println(message.originUser.alias + ": " + message.message + "\n");
		System.out.println("PrivateChat storedconv " + this.chatConfig.storedConversation);
		this.display.append(message.originUser.alias + ": " + message.message + "\n");
		this.display.update(this.display.getGraphics());
	}

	void setConfig(PrivateChatConfig config) {
		this.chatConfig = config;
		this.lblContactAlias.setText(chatConfig.recieverClient.alias);
		this.display.setText(this.chatConfig.storedConversation);
		this.display.update(this.display.getGraphics());
	}
}
