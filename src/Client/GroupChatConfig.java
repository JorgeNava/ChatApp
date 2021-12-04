package Client;

import java.net.DatagramSocket;
import java.util.ArrayList;

public class GroupChatConfig {
	User originClient;
	String storedConversation;
	ArrayList<User> recievers = new ArrayList<User>();
	int chatId;
	Message lastRecievedMessage;

	public GroupChatConfig(User originClient, ArrayList<User> recievers) {
		this.originClient = originClient;
		this.recievers = recievers;
		this.storedConversation = "";
	}
	
	void updateStoredConversation(String message) {
		this.storedConversation += message;
	}
	
	public Message getLastRecievedMessage() {
		return lastRecievedMessage;
	}

	public void setLastRecievedMessage(Message lastRecievedMessage) {
		this.lastRecievedMessage = lastRecievedMessage;
	}
}
