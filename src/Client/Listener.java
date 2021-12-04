package Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

import javax.swing.JTextArea;

public class Listener implements Runnable{
	AppConfiguration appConfig = AppConfiguration.getInstance();
	JTextArea chatTextArea;
	final int messageBytesLength = 256;
	
	public Listener(Chat chat) {}	
		
	public void run() {
		try {
			byte[] collector = new byte[messageBytesLength];
			DatagramSocket socket = new DatagramSocket();
			DatagramPacket recievedPackage;
			String recievedMessage = "";
		
			do {
				collector = new byte[messageBytesLength];
				recievedPackage = new DatagramPacket(collector, messageBytesLength);
				socket.receive(recievedPackage);
				recievedMessage = new String(collector).trim();
				
				this.appConfig.setRecievedMessage(new Message(recievedMessage));
				Message message = this.appConfig.getRecievedMessage();
				message.printMessageData();
				
				if(! message.flag.equals("EndClient")) {
					processMessage(message);					
				}else { break; }
			} while (true);
		}catch (Exception e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}
	
	void processMessage(Message message) throws IOException {
		String recievedFlag = message.flag;
		
		if(recievedFlag.equals("RegistrationCompleted")) {
			setRegisteredClients(message);
		}else if(recievedFlag.equals("UpdateRegisteredClientListCompleted")) {
			setRegisteredClients(message);
		}else{
			
		}		
	}
	
	void setRegisteredClients(Message message) {
		this.appConfig.setRegisteredClients(message.getRegisteredClientsFromMessage());
	}
}
