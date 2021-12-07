package Client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;

public class Message implements Serializable{
	private boolean isMessageFromServer;
	public boolean isMessageFile;
	public ArrayList<User> registeredClients = new ArrayList<User>();
	public ArrayList<User> groupChatRecievers = new ArrayList<User>();
	public User originUser;
	public User destinationUser;
	public String message;
	public String formattedMessage;
	public String flag;	
	public byte[] fileBytes;
	
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
		System.out.println("Destination user alias: " + this.destinationUser.alias);
		System.out.println("Destination user port: " + this.destinationUser.port);
		System.out.println("Message: " + this.message);
		System.out.println("Flag: " + this.flag);
		System.out.println("Formatted message: " + this.formattedMessage);
		
		this.printConnectedClients();
	}
	public void printConnectedClients() {
		System.out.println("printConnectedClients");
		for (int i = 0; i < this.registeredClients.size(); i++) {
			System.out.println("user alias: " + this.registeredClients.get(i).alias);
			System.out.println("user port: " + this.registeredClients.get(i).port);
         }
	}
	public void setFileBytes(String filePath) {	
		try {
			this.isMessageFile = true;
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filePath));
	        int length = bis.available();
	        this.fileBytes = new byte[length];
	        bis.read(this.fileBytes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}
	public void downloadFile(String filePath) {	
		try {
			File userDirectory = new File(rootPath + message.destinationUser.alias);
			if (this.isMessageFile) {
				if(!userDirectory.exists()) {
					userDirectory.mkdir();
				}
			
			BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(filePath));
            fos.write(this.fileBytes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}
}
