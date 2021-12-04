package Client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class MessageSender {
    Message msg;
    ByteArrayOutputStream baos;
    ObjectOutputStream oos;
    InetAddress IPAddress;
    
    public MessageSender(Message msg) {
		this.msg = msg;
		this.baos = new ByteArrayOutputStream();
         try {
			this.oos = new ObjectOutputStream(this.baos);
			this.IPAddress = InetAddress.getByName("localhost");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public void sendMessage() {	
		DatagramSocket socket;
    	try {  	
        	socket = new DatagramSocket();
            oos.writeObject(msg);
            byte[] buf = baos.toByteArray();
            
            DatagramPacket sendPacket = new DatagramPacket(buf, buf.length,IPAddress, msg.destinationPort);
            socket.send(sendPacket);
        }catch (Exception e) {
			 System.err.println(e.getMessage());
		 }
    }
}
