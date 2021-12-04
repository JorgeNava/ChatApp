package Client;

import java.net.DatagramSocket;

public class PrivateChatConfig {
	AppConfiguration appConfig = AppConfiguration.getInstance();
	User originClient, recieverClient;
	String storedConversation;
	Message lastRecievedMessage;

	public PrivateChatConfig(User originClient, User recieverClient) {
		this.originClient = originClient;
		this.recieverClient = recieverClient;
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
