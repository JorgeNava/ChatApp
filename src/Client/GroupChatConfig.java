package Client;

import java.util.ArrayList;

public class GroupChatConfig {
	User originClient;
	String storedConversation;
	ArrayList<User> recievers = new ArrayList<User>();
	int chatId;

	public GroupChatConfig(User originClient, ArrayList<User> recievers) {
		this.originClient = originClient;
		this.recievers = recievers;
		this.storedConversation = "";
	}
	
	void updateStoredConversation(String message) {
		this.storedConversation += message;
	}
}
