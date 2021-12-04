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
	DatagramSocket serverSenderSocket;
	private final String fieldSeparator = "¶";
	private final String attributeSeparator = "§";
	private final String registeredClientsSeparator = "¾";

	
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
				
				serverInterfase.updateConsole(recievedMessage.originUser + " > Send: " + recievedMessage.message);
				
				processMessage(recievedMessage);			
			}while(true);
		}catch(Exception e) {
			System.err.println(e.getMessage());
			System.exit(1);	
		}	
	}
	
	// Run server services based in recievedMessage flag
	void processMessage(Message recievedMessage) throws IOException {
		String recievedFlag = recievedMessage.flag;
		String messageWasFor = "";
		String responseMessage = "";
		String responseFlag = "";
		
		if(recievedFlag.equals("RegisterUser")) {
			registerClient(recievedMessage.originUser);
			messageWasFor = "Server";
			responseMessage = getRegisteredClientsString();
			responseFlag = "RegistrationCompleted";
		}else if(recievedFlag.equals("UpdateRegisteredClientList")) {	// ! STILL MISSING TO IMPLEMENT THIS IN CLIENT SIDE
			messageWasFor = "Server";
			responseMessage = getRegisteredClientsString();
			responseFlag = "UpdateRegisteredClientListCompleted";
		}else{ // ! STILL MISSING TO IMPLEMENT THIS IN CLIENT SIDE 
			// Messages destined for Private/Group Chats
			// Recieved messages for this chats must have PrivateChat or GroupChat flags
			messageWasFor = recievedFlag;
			responseMessage = recievedMessage.message;
			responseFlag = recievedFlag;
		}
		
		sendResponse(recievedMessage, messageWasFor, responseMessage, responseFlag);
	}
		
	void sendResponse(Message recievedMessage, String messageWasFor, String responseMessage, String responseFlag) throws IOException {
		Message serverMessage = new Message();
		User originUser = new User();
		int destinationPort;
		
		if(messageWasFor.equals("Server")) {	// Send response message to origin client since it was for a service
			originUser = new User("Server", this.serverSenderSocket.getLocalPort());
			destinationPort = recievedMessage.originUser.port;
			
			serverMessage = new Message(originUser, destinationPort, responseMessage, responseFlag, this.serverSenderSocket);
			serverMessage.sendMessage();
		}else if(messageWasFor.equals("PrivateChat")) {
			// ! STILL MISSING TO IMPLEMENT REDIRECTION OF PRIVATE CHATS MESSAGES
		}else if(messageWasFor.equals("GroupChat")) {
			// ! STILL MISSING TO IMPLEMENT MULTI-REDIRECTION OF GROUP CHATS MESSAGES
		}else {
			
		}
		
	}
	
	String getRegisteredClientsString() {
		String registeredClientsString = "";
		for (int i = 0; i < this.registeredClients.size(); i++) {
			User client = this.registeredClients.get(i);
			
			registeredClientsString += client.alias + attributeSeparator;
			registeredClientsString += client.port + registeredClientsSeparator;
		}
		return registeredClientsString;
	}
	
	void registerClient(User newUser) {
		this.registeredClients.add(newUser);
		this.serverInterfase.updateConsole("Server > Client "+ newUser.alias +" was registerd succesfuly!");
	}
}
