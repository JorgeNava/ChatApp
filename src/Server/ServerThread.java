package Server;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import Client.Message;
import Client.MessageSender;
import Client.User;

public class ServerThread implements Runnable {
	final int MESSAGES_BYTES_LENGTH = 1024;
	DatagramSocket socket;
	Server serverInterfase;
	int serverPort;
	
	ArrayList<User> registeredClients = new ArrayList<User>();
	DatagramSocket serverSenderSocket;

	
	public ServerThread (int serverPort, Server serverInterfase) {
		this.serverInterfase = serverInterfase;
		this.serverPort = serverPort;
	}

	public void run() {
		try {
			byte[] messageBytes = new byte[MESSAGES_BYTES_LENGTH];
			DatagramPacket recievedPackage;
			this.socket = new DatagramSocket(serverPort);		

			do {
				recievedPackage = new DatagramPacket(messageBytes, messageBytes.length);
				this.socket.receive(recievedPackage);
				
				byte[] data = recievedPackage.getData();
				ByteArrayInputStream in = new ByteArrayInputStream(data);
				ObjectInputStream is = new ObjectInputStream(in);
				

				try {
					Message recievedMessage = (Message) is.readObject();
					recievedMessage.printMessageData();
					serverInterfase.updateConsole(recievedMessage.originUser + " > Send: " + recievedMessage.message);
					processMessage(recievedMessage);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}			
			}while(true);
		}catch(Exception e) {
			System.err.println(e.getMessage());
			System.exit(1);	
		}	
	}
	
	void processMessage(Message recievedMessage) throws IOException {
		String recievedFlag = recievedMessage.flag;
		String messageWasFor = "";
		String responseMessage = "";
		String responseFlag = "";
		
		if(recievedFlag.equals("RegisterUser")) {
			registerClient(recievedMessage.originUser);
			messageWasFor = "Server";
			responseMessage = "Registration of user was done.";
			responseFlag = "RegistrationCompleted";
		}else if(recievedFlag.equals("UpdateRegisteredClientList")) {	// ! STILL MISSING TO IMPLEMENT THIS IN CLIENT SIDE
			messageWasFor = "Server";
			responseMessage = "Updated registered clients list was retrieved.";
			responseFlag = "UpdateRegisteredClientListCompleted";
		}else{
			messageWasFor = recievedFlag;
		}
		
		sendResponse(recievedMessage, messageWasFor, responseMessage, responseFlag);
	}
		
	void sendResponse(Message recievedMessage, String messageWasFor, String responseMessage, String responseFlag) throws IOException {
		if(messageWasFor.equals("Server")) {
			User originUser = new User("Server", this.serverSenderSocket.getLocalPort());
			Message serverMessage = new Message(originUser, recievedMessage.originUser, responseMessage, responseFlag);
			serverMessage.setIsMessageFromServer(true);
			serverMessage.registeredClients = this.registeredClients;
			MessageSender msgSender = new MessageSender(serverMessage);
			msgSender.sendMessage();
		}else if(messageWasFor.equals("PrivateChat")) {
			recievedMessage.setIsMessageFromServer(true);
			recievedMessage.registeredClients = this.registeredClients;
			MessageSender msgSender = new MessageSender(recievedMessage);
			msgSender.sendMessage();
		}else if(messageWasFor.equals("GroupChat")) {
			recievedMessage.registeredClients = this.registeredClients;
			recievedMessage.groupChatRecievers.forEach((groupMember) -> {
				if(!groupMember.equals(recievedMessage.originUser)) {
					Message serverMessage = recievedMessage;
					serverMessage.setIsMessageFromServer(true);
					serverMessage.destinationUser = groupMember;
					serverMessage.registeredClients = this.registeredClients;
					
					MessageSender msgSender = new MessageSender(serverMessage);				
					msgSender.sendMessage();
				}
			});
		}else {
			
		}
		
	}
	
	void registerClient(User newUser) {
		this.registeredClients.add(newUser);
		this.serverInterfase.updateConsole("Server > Client "+ newUser.alias +" was registerd succesfuly!");
	}
}
