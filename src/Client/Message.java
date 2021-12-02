package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Message {
	private final String messageFormatOrder[] = {"Origin User","Destination Port", "Message", "Flag"};
	private final String fieldSeparator = "¶";
	private final String attributeSeparator = "§";
	private final String registeredClientsSeparator = "¾";
	private final String serverIp = "localhost";
	private final int serverPort = 8101;
	private final int messageBytesLength = 256;

	public DatagramSocket socket;
	public User originUser;
	public int destinationPort; 
	public String message;
	public String formattedMessage;
	public String flag;
	
	
	public Message(User originUser, int destinationPort, String message, String flag, DatagramSocket senderSocket) {
		this.originUser = originUser;
		this.destinationPort = destinationPort;
		this.message = message;
		this.flag = flag;
		this.socket = senderSocket;
		this.createFormattedMessage();
	}
	
	public Message(String formattedMessage) {
		this.createMessageFromFormattedMessage(formattedMessage);
	}
	
	public Message() {}
	
	ArrayList<User> getRegisteredClientsFromMessage() {
		ArrayList<User> registeredClients =  new ArrayList<User>();
		String registeredClientsString = this.message;
		
		String usersStrings[] = registeredClientsString.split(registeredClientsSeparator);
		
		for (int i = 0; i < usersStrings.length; i++) {
			String userAttributes[] = usersStrings[i].split(attributeSeparator);
			String userAlias = userAttributes[0];
			int userPort = Integer.parseInt(userAttributes[1]);
			
			registeredClients.add(new User(userAlias, userPort));
		}
		
		return registeredClients;
	}
	
	public void sendMessage() throws IOException {
		InetAddress address = InetAddress.getByName(serverIp);
		byte[] messageBytes = new byte[messageBytesLength];
		
		messageBytes = this.formattedMessage.getBytes();
		DatagramPacket senderPackage = new DatagramPacket(messageBytes, this.formattedMessage.length(), address, this.destinationPort);
		this.socket.send(senderPackage);
	}
	
	void createFormattedMessage() {
		String convertedMessage = "";
		
		convertedMessage += this.originUser.alias + attributeSeparator;
		convertedMessage += this.originUser.port + fieldSeparator;
		convertedMessage += this.destinationPort + fieldSeparator;
		convertedMessage += this.message + fieldSeparator;
		convertedMessage += this.flag;
		
		this.formattedMessage = convertedMessage;
	}
	
	void createMessageFromFormattedMessage(String formattedMessage) {
		String messageByFields[] = formattedMessage.split(fieldSeparator);
		String messageOriginUser[] = messageByFields[0].split(attributeSeparator);

		String originUserAlias = messageOriginUser[0];
		int originUserPort = Integer.parseInt(messageOriginUser[1]);
		this.originUser = new User(originUserAlias, originUserPort);
		
		this.destinationPort = Integer.parseInt(messageByFields[1]);
		this.message = messageByFields[2];
		this.flag = messageByFields[3];
						
		this.formattedMessage = formattedMessage;
	}
	
	public void printMessageData() {
		System.out.println("Origin user alias: " + this.originUser.alias);
		System.out.println("Origin user port: " + this.originUser.port);
		System.out.println("Destination port: " + this.destinationPort);
		System.out.println("Message: " + this.message);
		System.out.println("Flag: " + this.flag);
		System.out.println("Formatted message: " + this.formattedMessage);
	}
}
