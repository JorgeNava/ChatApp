package Client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class MessageSender {
	final int SERVER_PORT = 8010;
    ByteArrayOutputStream baos;
    ObjectOutputStream oos;
    InetAddress IPAddress;
    Message msg;

    public MessageSender(Message msg) {
    	try {
    		this.msg = msg;
    		this.baos = new ByteArrayOutputStream();
			this.oos = new ObjectOutputStream(this.baos);
			this.IPAddress = InetAddress.getByName("localhost");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public void sendMessage() {	
		DatagramSocket socket;
    	try {  	
        	socket = new DatagramSocket();
            oos.writeObject(this.msg);
            byte[] buf = baos.toByteArray();
            DatagramPacket sendPacket;

            if(this.msg.getIsMessageFromServer()) {	// FROM SERVER - CLIENT/S
            	sendPacket = new DatagramPacket(buf, buf.length,IPAddress, msg.destinationUser.port);            	
            }else {	// FROM CLIENT TO SERVER
            	sendPacket = new DatagramPacket(buf, buf.length,IPAddress, SERVER_PORT);            	
            }
            socket.send(sendPacket);
        }catch (Exception e) {
			 System.err.println(e.getMessage());
		 }
    }
}
