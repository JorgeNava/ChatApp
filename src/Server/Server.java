package Server;

import java.awt.event.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

import javax.swing.*;

import Client.Message;
import Client.User;

public class Server extends JFrame {
	JTextArea display;
	JScrollPane scroll;
	JTextArea textArea;
	final static int SERVER_PORT = 8010;
	
	public Server() {
		super("Server");
		getContentPane().setLayout(null);

		display = new JTextArea(16, 58);
	    display.setEditable(false);
	    scroll = new JScrollPane(display);
	    scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
	    scroll.setBounds(10, 5, 590, 400);
		getContentPane().add(scroll);
		
		textArea = new JTextArea();
		textArea.setBounds(10, 11, 5, 22);
		getContentPane().add(textArea);
	}

	public static void main(String[] args) {
		Server server = new Server();
		server.setSize(620, 460);
		server.setLocation(10, 20);
		server.setVisible(true);
		server.setResizable(false);
		server.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		server.makeConnection();
	}
	
	public void makeConnection() {
		Thread serverThread = new Thread(new ServerThread(SERVER_PORT, this));
		serverThread.start();	
	}
	
	void updateConsole(String message) {
		this.display.append(message+"\n");
		this.display.update(this.display.getGraphics());
	}
}
