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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Message implements Serializable{
	private boolean isMessageFromServer;
	public boolean isMessageFile = false;
	public ArrayList<User> registeredClients = new ArrayList<User>();
	public ArrayList<User> groupChatRecievers = new ArrayList<User>();
	public User originUser;
	public User destinationUser;
	public String message;
	public String flag;	
	public int groupChatId;
	public String fileString;
	public String sentFileName;
	
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
		System.out.println("> Origin user alias: " + this.originUser.alias);
		System.out.println("> Origin user port: " + this.originUser.port);
		if(this.destinationUser != null) {
			System.out.println("> Destination user alias: " + this.destinationUser.alias);
			System.out.println("> Destination user port: " + this.destinationUser.port);			
		}
		System.out.println("> Message: " + this.message);
		System.out.println("> Flag: " + this.flag);
		System.out.println("> Group chat id: " + this.groupChatId);
		System.out.println("> Is Message file: " + this.isMessageFile);
		System.out.println("> Is Bytes file: " + this.fileString);
		System.out.println("> SEnt File name: " + this.sentFileName);
		this.printConnectedClients();
		this.printGroupChatMembers();
	}
	public void printConnectedClients() {
		if(this.registeredClients.size() > 0) {
			for (User user : this.registeredClients) {
				System.out.println("User alias: " + user.alias);
				System.out.println("User port: " + user.port);
			}			
		}
	}
	public void printGroupChatMembers() {
		System.out.println("> printGroupChatMembers");
		if(this.flag.equals("GroupChat")) {
			for (User user : this.groupChatRecievers) {
				System.out.println("User alias: " + user.alias);
				System.out.println("User port: " + user.port);
			}			
		}
	}
	public void setFileBytes(String filePath) {	
		try {
			sentFileName =  filePath.substring(filePath.lastIndexOf("\\")+1);
			this.isMessageFile = true;
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filePath));
	        int length = bis.available();
	        byte[] bytes = new byte[length];
	        bis.read(bytes);
	        this.fileString = new String(bytes,StandardCharsets.UTF_8);
	        System.out.println(this.fileString);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}
	public void downloadFile() {	
		try {
			String filePath = formatPath(System.getProperty("user.dir"));
			System.out.println(filePath);
			File userDirectory = new File(filePath + this.destinationUser.alias + "Downloads");
			if(!userDirectory.exists()) {
				userDirectory.mkdir();
			}
			System.out.println(formatPath(userDirectory.getAbsolutePath()));
			System.out.println(userDirectory.getAbsolutePath());
			BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(formatPath(userDirectory.getAbsolutePath()) + sentFileName));
			System.out.println(this.fileString);
			byte[] bytes = this.fileString.getBytes();
			System.out.println(bytes);
			fos.write(bytes);
			fos.flush();
            fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}
	public String formatPath(String path) {
		String resultPath;
		resultPath = path.replace("\\", "\\\\");
		resultPath += "\\\\";
		return resultPath;
	}
}
