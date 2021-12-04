package Client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

import javax.swing.JTextArea;

import Client.Message;

public class Listener implements Runnable {
	AppConfiguration appConfig = AppConfiguration.getInstance();
	JTextArea chatTextArea;
	final int messageBytesLength = 256;
	DatagramSocket socket = null;

	public Listener(Chat chat) {
	}

	public void run() {
		try {		
			socket = new DatagramSocket(); //NECESITA UN PUERTO
			byte[] incomingData = new byte[messageBytesLength];
			//byte[] collector = new byte[messageBytesLength];
			//DatagramPacket recievedPackage;
			//String recievedMessage = "";
		
			do {
				//collector = new byte[messageBytesLength];
				DatagramPacket recievedPackage = new DatagramPacket(incomingData, incomingData.length);
				socket.receive(recievedPackage);
				byte[] data = recievedPackage.getData();
				//recievedMessage = new String(collector).trim();
				ByteArrayInputStream in = new ByteArrayInputStream(data);
				ObjectInputStream is = new ObjectInputStream(in);
				try {
					Message message = (Message) is.readObject(); 
//				this.appConfig.setRecievedMessage(new Message(recievedMessage));
//				Message message = this.appConfig.getRecievedMessage();
//				message.printMessageData();
					if(! message.flag.equals("EndClient")) {
						processMessage(message);					
					}else { break; }
				}catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			} while (true);
		}catch (Exception e) {
			System.err.println(e.getMessage());
			//System.exit(1);
		}
	}

	void processMessage(Message message) throws IOException {
		String recievedFlag = message.flag;

		if (recievedFlag.equals("RegistrationCompleted")) {
			setRegisteredClients(message);
		} else if (recievedFlag.equals("UpdateRegisteredClientListCompleted")) {
			setRegisteredClients(message);
		} else {

		}
	}

	void setRegisteredClients(Message message) {
		this.appConfig.setRegisteredClients(message.getRegisteredClientsFromMessage());
	}
}
