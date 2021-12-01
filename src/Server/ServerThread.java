package Server;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import Client.Message;
import Client.User;

public class ServerThread implements Runnable {
	final int supportedClients = 100;
	final int messageBytesLength = 256;
	Server serverInterfase;
	int serverPort;
	int registeredClientsIndex;
	int privateChatsIndex;
	int groupChatsIndex;

	ArrayList<User> registeredClients = new ArrayList<User>();
	
	private int[] clientsPorts;
	private InetAddress[] clientsIPs;
	
	public ServerThread (int serverPort, Server serverInterfase) {
		this.serverPort = serverPort;
		this.serverInterfase = serverInterfase;
		this.registeredClientsIndex = 0;
		this.privateChatsIndex = 0;
		this.groupChatsIndex = 0;
		this.clientsPorts = new int [supportedClients];
		this.clientsIPs = new InetAddress[supportedClients];
	}

	public void run() {
		try {
			int clientPort;
			byte[] messageBytes;
			String recievedString;
			DatagramSocket socket = new DatagramSocket(serverPort);
			DatagramPacket recievedPackage;
			InetAddress clientAddress;			
			

			do {
				messageBytes = new byte[messageBytesLength];
				recievedString = new String(messageBytes);
				recievedPackage = new DatagramPacket(messageBytes, messageBytesLength);
				socket.receive(recievedPackage);
				
				recievedString = new String(messageBytes).trim();
				
				Message recievedMessage = new Message(recievedString);
				recievedMessage.printMessageData();
				
				serverInterfase.updateConsole("Recieved message: "+ recievedMessage.message);
				
				proccessFlags(recievedMessage);			
			}while(true);
		}catch(Exception e) {
			System.err.println(e.getMessage());
			System.exit(1);	
		}	
	}
	
	void proccessFlags(Message message) {
		String responseMessage = "";

		if(message.flag.equals("RegisterUser")) {
			registerClient(message.originUser);		
			responseMessage = "You were registered succesfully.";
		}else{ // Messages for other clients/users/nodes
			responseMessage = message.message;
		}
		
		sendResponse(responseMessage);
	}
	
	void registerClient(User newUser) {
		this.registeredClients.add(newUser);
		this.serverInterfase.updateConsole("Client "+ newUser.alias +" was registerd succesfuly!");
	}
	
	void sendResponse(String responseMessage) {
		byte[] senderMessageBytes = new byte[messageBytesLength];
		DatagramPacket senderPackage;
		DatagramSocket socket;
		
		// ! IDENTIFY IF MESSAGE IS FOR CLIENT APP, PRIVATE CHAT OR GROUP CHAT
		
		//for(int i = 0; i < clientIndex; i++) {
		try {
			socket = new DatagramSocket(serverPort + 1);
			senderMessageBytes = responseMessage.getBytes();
			senderPackage = new DatagramPacket(senderMessageBytes, responseMessage.length(), clientsIPs[i], clientsPorts[i]);
			socket.send(senderPackage);
			socket.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
		//}
	}	
}
