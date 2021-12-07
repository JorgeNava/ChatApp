package Client;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.file.Files;
import java.util.ArrayList;

import javax.swing.JTextArea;

public class Listener implements Runnable {
	AppConfiguration appConfig = AppConfiguration.getInstance();
	JTextArea chatTextArea;
	final int messageBytesLength = 1024;
	Chat chat;
	String rootPath = "D:\\UAG\\10_Cuatrimestre\\SistemasDistribuidos\\PrimerParcial\\Proyectos\\SD Final Project\\src\\";
	public Listener(Chat chat) {
		this.chat = chat;
	}

	public void run() {
		try {
			byte[] incomingData = new byte[messageBytesLength];

			do {
				try {
					DatagramPacket recievedPackage = new DatagramPacket(incomingData, incomingData.length);
					this.appConfig.getSocket().receive(recievedPackage);
					byte[] data = recievedPackage.getData();
					ByteArrayInputStream in = new ByteArrayInputStream(data);
					ObjectInputStream is = new ObjectInputStream(in);

					Message message = (Message) is.readObject();
					this.appConfig.setRecievedMessage(message);
					message.printMessageData();
					if (!message.flag.equals("EndClient")) {
						processMessage(message);
					} else {
						break;
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			} while (true);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}

	void processMessage(Message message) throws IOException {
		String recievedFlag = message.flag;

		if (recievedFlag.equals("RegistrationCompleted")) {
			setRegisteredClients(message);
			if (this.appConfig.getActualView().equals(this.chat.LOBBY_VIEW_ID)) {
				this.chat.lobbyView.updateRegisteredClientsList(message.registeredClients);
			}
		} else if (recievedFlag.equals("UpdateRegisteredClientListCompleted")) {
			setRegisteredClients(message);
			if (this.appConfig.getActualView().equals(this.chat.LOBBY_VIEW_ID)) {
				this.chat.lobbyView.updateRegisteredClientsList(message.registeredClients);
			}
		} else if (recievedFlag.equals("PrivateChat")) {
			this.chat.privateChatView.chatConfig = this.chat.lobbyView.updatePrivateChatConfigByUser(message, this.chat.privateChatView);
			if(message.originUser.alias.equals(this.chat.privateChatView.chatConfig.recieverClient.alias)) {
				if (this.appConfig.getActualView().equals(this.chat.PRIVATE_CHAT_VIEW_ID)) {
					this.chat.privateChatView.updateChat(message);
				}
			}
			//ADD IF STATEMENT FOR FILE TRANSFER FLAG, RUN DOWNLOAD FILE HERE
				message.downloadFile();
		} else if (recievedFlag.equals("GroupChat")) {
			this.chat.lobbyView.updateGroupChatConfigByUser(message, this.chat.groupChatView);

			if (this.appConfig.getActualView().equals(this.chat.GROUP_CHAT_VIEW_ID)) {
				this.chat.groupChatView.updateChat(message);
			}
			//ADD IF STATEMENT FOR FILE TRANSFER FLAG, RUN DOWNLOAD FILE HERE 
		} else {

		}
	}

	void setRegisteredClients(Message message) {
		this.appConfig.setRegisteredClients(message.registeredClients);
		System.out.println(this.appConfig.getRegisteredClients());
	}
}
