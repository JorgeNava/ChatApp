package Client;

import java.util.ArrayList;

public class GroupChatConfig {
	int chatId;
	String storedConversation;
	User originClient;
	ArrayList<User> destinyRecievers = new ArrayList<User>();
	Message lastRecievedMessage;

	public GroupChatConfig(int chatId, User originClient, ArrayList<User> destinyRecievers) {
		this.chatId = chatId;
		this.originClient = originClient;
		this.destinyRecievers = destinyRecievers;
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
