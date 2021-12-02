package Client;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.swing.JTextArea;

public class Listener_old implements Runnable{
	JTextArea chatTextArea;
	final int messageBytesLength = 256;
	
	public Listener_old(JTextArea chatTextArea) {
		this.chatTextArea = chatTextArea;
	}	
		
	public void run() {
		InetAddress address;
		DatagramSocket socket;
		String message = "";
		String recievedMessage = "";
		DatagramPacket datagramPackage;
		DatagramPacket recievedPackage;
		byte[] collector = new byte[messageBytesLength];
		byte[] messageBytes = new byte[messageBytesLength];
		
		messageBytes = message.getBytes();
		try {
			socket = new DatagramSocket();			 
			address = InetAddress.getByName("localhost");
			 
			message = "&/";
			messageBytes = message.getBytes();
			datagramPackage = new DatagramPacket(messageBytes, message.length(), address, 11001);
			socket.send(datagramPackage);
			 
			do {
				collector = new byte[messageBytesLength];
				recievedPackage = new DatagramPacket(collector, messageBytesLength);
				socket.receive(recievedPackage);
				recievedMessage = new String(collector).trim(); 
				chatTextArea.append("Message recieved: " +recievedMessage + "\n");
				System.out.println(recievedMessage);
			} while (!recievedMessage.startsWith("/&"));
		}catch (Exception e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}
}
