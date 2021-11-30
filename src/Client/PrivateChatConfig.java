package Client;

public class PrivateChatConfig {
	User originClient, recieverClient;
	String storedConversation;
	int originPort, recieverPort;
	
	public PrivateChatConfig(User originClient, User recieverClient) {
		this.originClient = originClient;
		this.recieverClient = recieverClient;
		this.storedConversation = "";
	}
	
	void updateStoredConversation(String message) {
		this.storedConversation += message;
	}
}
