package Client;

import java.net.DatagramSocket;
import java.util.ArrayList;

public class AppConfiguration {
    private static AppConfiguration instance = null;
	private final int SERVER_PORT = 8010;

	private ArrayList<User> registeredClients = new ArrayList<User>();
	private Message latestRecievedMessage =  new Message();
	private Message recievedMessage = new Message();
	private User clientUser = new User();
	private DatagramSocket socket;
	private String actualView = "Login";
	
	private AppConfiguration() {
	}
	
	public boolean isNewMessageRecieved() {
		boolean wasLatestRecievedMessageUpdated = false;
		if(! this.recievedMessage.formattedMessage.equals(this.latestRecievedMessage.formattedMessage)) {
			wasLatestRecievedMessageUpdated = true;
		}		
		return wasLatestRecievedMessageUpdated;
	}
	
	 public static AppConfiguration getInstance(){
		 if(instance == null)
			 instance = new AppConfiguration();
		 return instance;
	 }
	
	 public int getServerPort() {
		 return SERVER_PORT;
	 }

	 public String getActualView() {
		 return actualView;
	 }
	
	 public void setActualView(String actualView) {
		 this.actualView = actualView;
	 }

	 public User getClientUser() {
		 return clientUser;
	 }

	public void setClientUser(User clientUser) {
		this.clientUser = clientUser;
	}
	 
	public ArrayList<User> getRegisteredClients() {
		return registeredClients;
	}

	public void setRegisteredClients(ArrayList<User> registeredClients) {
		this.registeredClients = registeredClients;
	}

	public Message getLatestRecievedMessage() {
		return latestRecievedMessage;
	}

	public void setLatestRecievedMessage(Message latestRecievedMessage) {
		this.latestRecievedMessage = latestRecievedMessage;
	}

	public Message getRecievedMessage() {
		return recievedMessage;
	}

	public void setRecievedMessage(Message recievedMessage) {
		this.recievedMessage = recievedMessage;
	}

	public DatagramSocket getSocket() {
		return socket;
	}

	public void setSocket(DatagramSocket socket) {
		this.socket = socket;
	}
}
