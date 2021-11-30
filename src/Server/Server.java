package Server;

import java.awt.event.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

import javax.swing.*;

public class Server extends JFrame {
	JPanel serverPanel = new JPanel();
	JTextField messageField = new JTextField();
	JButton connectBtn = new JButton();
	JLabel successLabel = new JLabel("Service started succesful");
	JLabel portLabel = new JLabel("Port");
	JTextField portField = new JTextField();

	public Server() {
		super("Server");
		getContentPane().setLayout(null);
		serverPanel.setLayout(null);
		serverPanel.setBounds(40, 120, 400, 200);
		
		messageField.setBounds(40, 10, 200, 100);
		portLabel.setBounds(0, 0, 46, 14);
		portField.setBounds(60, 0, 140, 20);
		connectBtn.setBounds(70, 30, 130, 30);
		successLabel.setBounds(0,65,200,20);
		
		portField.setColumns(10);
		portField.setText("8001");

		connectBtn.setText("Start service");
		connectBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				makeConnection();
			}
		});
		
		successLabel.setVisible(false);
		
		serverPanel.add(portField);
		serverPanel.add(connectBtn);
		serverPanel.add(successLabel);
		getContentPane().add(messageField);
		getContentPane().add(serverPanel);
		
		serverPanel.add(portLabel);
	}

	public void makeConnection() {
		Thread serverThread = new Thread(new ServerThread(Integer.parseInt(portField.getText()), this));
		serverThread.start();	
	}
	
	public static void main(String[] args) {
		Server server = new Server();
		server.setSize(300, 260);
		server.setLocation(100, 100);
		server.setVisible(true);
		server.setResizable(false);
		server.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
