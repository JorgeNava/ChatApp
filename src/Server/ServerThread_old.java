package Server;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread_old implements Runnable {
	final int supportedClients = 10;
	final int messageBytesLength = 256;
	private int serverPort;
	private int clientIndex;
	private int[] clientsPorts;
	private InetAddress[] clientsIPs;
	private Server_old serverInterfase;
	
	public ServerThread_old (int serverPort, Server_old server_old) {
		this.serverPort = serverPort;
		this.serverInterfase = server_old;
		this.clientIndex = 0;
		this.clientsPorts = new int [supportedClients];
		this.clientsIPs = new InetAddress[supportedClients];
	}

	public void run() {
		try {
			int clientPort;
			byte[] messageBytes;
			String compMessage;
			String recievedMessage;
			DatagramSocket socket = new DatagramSocket(serverPort);
			DatagramPacket recievedPackage;
			InetAddress clientAddress;			
			
			serverInterfase.successLabel.setVisible(true);
			
			do {
				compMessage = "";
				messageBytes = new byte[messageBytesLength];
				recievedMessage = new String(messageBytes);
				recievedPackage = new DatagramPacket(messageBytes, messageBytesLength);
				socket.receive(recievedPackage);
				
				recievedMessage = new String(messageBytes).trim();
				serverInterfase.messageField.setText(recievedMessage);
				
				clientPort = recievedPackage.getPort();
				clientAddress = recievedPackage.getAddress();
				
				if(recievedMessage.startsWith("&/")) {
					registerClient(clientPort, clientAddress);
					compMessage = "You were registered succesfully.";
				} else {
					compMessage = recievedMessage;
					sendPackage(compMessage);
				}				
			}while(true);
			
		}catch(Exception e) {
			System.err.println(e.getMessage());
			System.exit(1);	
		}	
	}
	
	void registerClient(int clientPort, InetAddress clientAddress) {
		clientsPorts[clientIndex] = clientPort;
		clientsIPs[clientIndex] = clientAddress;
		serverInterfase.messageField.setText("Client was registerd succesfuly!");
		clientIndex++;
	}
	
	void sendPackage(String compMessage) {
		byte[] senderMessageBytes = new byte[messageBytesLength];
		DatagramPacket senderPackage;
		DatagramSocket socket;
		
		for(int i = 0; i < clientIndex; i++) {
			try {
				socket = new DatagramSocket(serverPort + 1);
				senderMessageBytes = compMessage.getBytes();
				senderPackage = new DatagramPacket(senderMessageBytes, compMessage.length(), clientsIPs[i], clientsPorts[i]);
				socket.send(senderPackage);
				socket.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}	
}
