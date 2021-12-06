package Client;

import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable{
	private boolean isMessageFromServer;

	public ArrayList<User> registeredClients = new ArrayList<User>();
	public ArrayList<User> groupChatRecievers = new ArrayList<User>();
	public User originUser;
	public User destinationUser;
	public String message;
	public String formattedMessage;
	public String flag;	
	public int groupChatId;
	
	public Message(User originUser, User destinationUser, String message, String flag) {
		this.originUser = originUser;
		this.destinationUser = destinationUser;
		this.message = message;
		this.flag = flag;
		this.isMessageFromServer = false;
	}
	
	public Message(User originUser, ArrayList<User> groupChatRecievers, String message, String flag) {
		this.originUser = originUser;
		this.groupChatRecievers = groupChatRecievers;
		this.message = message;
		this.flag = flag;
		this.isMessageFromServer = false;
	}
	
	public Message() {}
	
	public boolean getIsMessageFromServer(){
		return this.isMessageFromServer;
	}
	
	public void setIsMessageFromServer(boolean isMessageFromServer){
		this.isMessageFromServer = isMessageFromServer;
	}
	
	public void printMessageData() {
		System.out.println("Origin user alias: " + this.originUser.alias);
		System.out.println("Origin user port: " + this.originUser.port);
		if(this.destinationUser != null) {
			System.out.println("Destination user alias: " + this.destinationUser.alias);
			System.out.println("Destination user port: " + this.destinationUser.port);			
		}
		System.out.println("Message: " + this.message);
		System.out.println("Flag: " + this.flag);
		System.out.println("Formatted message: " + this.formattedMessage);
		
		this.printConnectedClients();
		this.printGroupChatMembers();
	}
	public void printConnectedClients() {
		System.out.println("printConnectedClients");
		if(this.registeredClients.size() > 0) {
			for (User user : this.registeredClients) {
				System.out.println("user alias: " + user.alias);
				System.out.println("user port: " + user.port);
			}			
		}
	}
	public void printGroupChatMembers() {
		System.out.println("printGroupChatMembers");
		if(this.groupChatRecievers.size() > 0) {
			for (User user : this.groupChatRecievers) {
				System.out.println("user alias: " + user.alias);
				System.out.println("user port: " + user.port);
			}			
		}
	}
}
