package Client;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.swing.JTextArea;

public class Listener implements Runnable{
	JTextArea chatTextArea;
	final int messageBytesLength = 256;
	Message recievedMessagePlaceholder;
	
	public Listener(Message recievedMessagePlaceholder) {
		this.recievedMessagePlaceholder = recievedMessagePlaceholder;
	}	
		
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
				recievedMessagePlaceholder = new Message(recievedMessage);
				recievedMessagePlaceholder.printMessageData();
			} while (! recievedMessagePlaceholder.flag.equals("EndClient"));
		}catch (Exception e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}
}
