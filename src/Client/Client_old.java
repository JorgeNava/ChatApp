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
import javax.swing.JLabel;
import java.awt.Dimension;
import javax.swing.JTextArea;

public class Client_old extends JPanel {
	InetAddress address;
	DatagramSocket socket;
	DatagramPacket senderPackage;
	BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	final int messageBytesLength = 256;
	byte[] messageBytes = new byte[messageBytesLength];
	int port;
	String ip;
	String message = "";
	
	public Client_old() {
		JTextField messageField;
		JTextField ipField;
		JTextField portField;
		JButton sendBtn;
		JButton connectionBtn;
		JLabel ipLabel;
		JLabel portLabel;
		JLabel clientLabel;
		JTextArea chatTextArea;
		Thread listener;
		
		setLayout(null);
		this.setPreferredSize(new Dimension(420, 360));

		connectionBtn = new JButton("Connect");
		connectionBtn.setBounds(303, 229, 97, 33);
		add(connectionBtn);

		sendBtn = new JButton("Send");
		sendBtn.setBounds(303, 310, 97, 25);
		add(sendBtn);

		messageField = new JTextField();
		messageField.setBounds(22, 273, 378, 29);
		add(messageField);
		messageField.setColumns(10);

		ipField = new JTextField();
		ipField.setText("localhost");
		ipField.setBounds(45, 201, 142, 25);
		add(ipField);
		ipField.setColumns(10);

		portField = new JTextField();
		portField.setText("8001");
		portField.setBounds(282, 201, 118, 25);
		add(portField);
		portField.setColumns(10);

		ipLabel = new JLabel("IP");
		ipLabel.setBounds(24, 205, 19, 16);
		add(ipLabel);

		portLabel = new JLabel("Service port");
		portLabel.setBounds(197, 205, 87, 16);
		add(portLabel);
		
		chatTextArea = new JTextArea();
		chatTextArea.setBounds(22, 11, 378, 173);
		add(chatTextArea);
		
		listener = new Thread(new Listener(chatTextArea));
		listener.start();

		connectionBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ip = ipField.getText();
				port = Integer.parseInt(portField.getText());

				try {
					socket = new DatagramSocket();
					address = InetAddress.getByName(ip);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});

		sendBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				message = messageField.getText();
				messageField.setText("");
				
				try {
					messageBytes = message.getBytes();
					senderPackage = new DatagramPacket(messageBytes, message.length(), address, port);
					socket.send(senderPackage);
				}catch (NumberFormatException | IOException e1) {
					e1.printStackTrace();
				}
			}
		});
	}
}
