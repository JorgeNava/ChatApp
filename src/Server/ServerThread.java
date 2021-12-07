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

import Client.GroupChatConfig;
import Client.Message;
import Client.MessageSender;
import Client.PrivateChatConfig;
import Client.User;

public class ServerThread implements Runnable {
	final int MESSAGES_BYTES_LENGTH = 1024 * 5;
	Server serverInterfase;
	int serverPort;
	
	ArrayList<PrivateChatConfig> connectedPrivateChatConfigs = new ArrayList<PrivateChatConfig>();
	ArrayList<GroupChatConfig> connectedGroupChatConfigs = new ArrayList<GroupChatConfig>();
	
	ArrayList<User> registeredClients = new ArrayList<User>();
	DatagramSocket serverSenderSocket;
	boolean registerNewGroup = true;
	
	public ServerThread (int serverPort, Server serverInterfase) {
		this.serverInterfase = serverInterfase;
		this.serverPort = serverPort;
	}

	public void run() {
		try {
			this.serverSenderSocket = new DatagramSocket(serverPort);
			byte[] messageBytes = new byte[MESSAGES_BYTES_LENGTH];
			DatagramPacket recievedPackage;
			do {
				try {
					recievedPackage = new DatagramPacket(messageBytes, messageBytes.length);
					this.serverSenderSocket.receive(recievedPackage);	
					byte[] data = recievedPackage.getData();
					ByteArrayInputStream in = new ByteArrayInputStream(data);
					ObjectInputStream is = new ObjectInputStream(in);
					System.out.println("RECIEVED MESSAGE:");
					Message recievedMessage = (Message) is.readObject();
					System.out.println("RECIEVED MESSAGE 222222222222:");
					recievedMessage.printMessageData();
					serverInterfase.updateConsole(recievedMessage.originUser.alias + " > Send: " + recievedMessage.message);
					processMessage(recievedMessage);
					getConnectedClientsAliasList(recievedMessage);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}			
			}while(true);
		}catch(Exception e) {
			System.err.println(e.getMessage());
			//System.exit(1);	
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
		}else if(recievedFlag.equals("RegisterGroup")) {
			this.registerNewGroup = true;
			for (User user : this.registeredClients) {
				if(user.alias.equals("Group "+recievedMessage.groupChatId)) {
					this.registerNewGroup = false;
				}
			}
			
			if(this.registerNewGroup) {
				registerGroup(recievedMessage);
				messageWasFor = "Server";
				responseMessage = "Registration of group was done.";
				responseFlag = "GroupRegistrationCompleted";				
			}
		}else{
			messageWasFor = recievedFlag;
		}
		sendResponse(recievedMessage, messageWasFor, responseMessage, responseFlag);
	}
		
	void sendResponse(Message recievedMessage, String messageWasFor, String responseMessage, String responseFlag) throws IOException {
		if(messageWasFor.equals("Server")) {
			User originUser = new User("Server", this.serverSenderSocket.getLocalPort());
			if(this.registerNewGroup) {
				for(User user: this.registeredClients) {
					if(!user.alias.equals(recievedMessage.originUser.alias)) {
						responseFlag = "UpdateRegisteredClientListCompleted";
					}
					Message updateMessage = new Message(originUser, user, responseMessage, responseFlag);
					updateMessage.setIsMessageFromServer(true);
					updateMessage.registeredClients = this.registeredClients;
					updateMessage.groupChatRecievers = recievedMessage.groupChatRecievers;
					MessageSender msgSenderUpdate = new MessageSender(updateMessage);
					msgSenderUpdate.sendMessage();				
				}
			}
			this.registerNewGroup = true;			
		}else if(messageWasFor.equals("PrivateChat")) {
			recievedMessage.setIsMessageFromServer(true);
			recievedMessage.registeredClients = this.registeredClients;
			System.out.println("Message to send to private chat");
			recievedMessage.printMessageData();
			MessageSender msgSender = new MessageSender(recievedMessage);
			msgSender.sendMessage();
		}else if(messageWasFor.equals("GroupChat")) {
			recievedMessage.registeredClients = this.registeredClients;
			recievedMessage.groupChatRecievers.forEach((groupMember) -> {
				if(!groupMember.alias.equals(recievedMessage.originUser.alias)) {
					Message serverMessage = recievedMessage;
					serverMessage.setIsMessageFromServer(true);
					serverMessage.destinationUser = groupMember;
					
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
	
	void registerGroup(Message message) {
		String groupName = "Group " + message.groupChatId;
		this.registeredClients.add(new User(groupName));
		this.serverInterfase.updateConsole("Server > Group "+ message.groupChatId +" was registerd succesfuly!");
	}

	
	void getConnectedClientsAliasList(Message m){
		ArrayList<User> connectedUsersList = this.registeredClients;
	}
}
