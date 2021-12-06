package Client;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JLabel;
import javax.swing.JList;

import java.awt.Dimension;
import javax.swing.JTextArea;
import javax.swing.JCheckBox;

public class Lobby extends JPanel {
	AppConfiguration appConfig = AppConfiguration.getInstance();
	JLabel aliasLabel;
	JList clientsList;
	JCheckBox groupChatCheckBox;
	JScrollPane clientsScrollPane;
	User recieverUser;
	boolean selectedChatIsGroupFlag;
	boolean selectedGroupChatIsNewFlag;
	boolean selectedPrivateChatIsNewFlag;
	int selectedGroupChatId = -1, selectedPrivateChatId = -1;
	
	/*
	 * === CLIENT NODE ===
	 *
	 * 
	 * ADD USER'S ALIASES AS TITLES TO PRIVATE CHATS
	 *
	 * ADD GROUP CHAT NAME AS TITLE TO GROUP CHAT AND ALSO TO GROUP CHAT CONFIGS
	 *
	 * ADD "ATTACH FILE" BUTTON AND FUNCTIONALITY TO PRIVATE AND GROUP CHAT INTERFACES
	 *
	 * 
	 * */
	
	ArrayList<User> connectedClients = new ArrayList<User>();
	ArrayList<PrivateChatConfig> connectedPrivateChatConfigs = new ArrayList<PrivateChatConfig>();
	ArrayList<GroupChatConfig> connectedGroupChatConfigs = new ArrayList<GroupChatConfig>();
	ArrayList<User> selectedClientsForGroupChat = new ArrayList<User>();
	Message recievedMessage = new Message();
	Message latestRecievedMessage = new Message();
	
	public Lobby() {
		setLayout(null);
		this.setPreferredSize(new Dimension(420, 177));
		
		clientsList = new JList(getConnectedClientsAliasList(this.appConfig.getRegisteredClients())); 
		clientsList.setVisibleRowCount(4);
		clientsScrollPane = new JScrollPane(clientsList);
		clientsScrollPane.setBounds(80, 40, 228, 80);
		add(clientsScrollPane);		

		aliasLabel = new JLabel("ALIAS: Default");
		aliasLabel.setBounds(10, 11, 117, 16);
		add(aliasLabel);
		
		groupChatCheckBox = new JCheckBox("Create Group chat");
		groupChatCheckBox.setBounds(80, 123, 110, 23);
		add(groupChatCheckBox);
		
		clientsList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				String recieverAlias = (String) clientsList.getSelectedValue();
				System.out.println("recieverAlias String " +recieverAlias);
				recieverUser = getUserFromConnectedClientsByUserAlias(recieverAlias);
				
				if(groupChatCheckBox.isSelected()) { // FOR NEW GROUP CHATS
					System.out.println("ACTUAL SELECTED CLIENT: " +  recieverAlias);
					System.out.println("ALL SELECTED CLIENTS: ");
					for (int i = 0; i < selectedClientsForGroupChat.size(); i++) {
					      System.out.println(selectedClientsForGroupChat.get(i));
				    }
					selectedGroupChatId = connectedGroupChatConfigs.size() + 1;
					System.out.println("NEW GROUP CHAT ID: " + selectedGroupChatId);
										
					selectedClientsForGroupChat.add(recieverUser);
					selectedGroupChatIsNewFlag = true;
				}else {
					System.out.println("SINGLE SELECTED CLIENT: " + recieverAlias);
					selectedChatIsGroupFlag = recieverAlias.startsWith("Group");
					if(selectedChatIsGroupFlag) {	// FOR EXISTING GROUP CHATS
						selectedGroupChatId = (Integer.parseInt(recieverAlias.split("")[1])) - 1;
						selectedGroupChatIsNewFlag = false;
					}else { // FOR PRIVATE CHATS
						System.out.println("recieverUser# "+ recieverUser.alias);
						selectedPrivateChatId = getPrivateChatConfigIndexByUser(recieverUser);
						if(selectedPrivateChatId == -1) {
							selectedPrivateChatId = connectedPrivateChatConfigs.size() + 1;
							selectedPrivateChatIsNewFlag = true;
						}else{
							selectedPrivateChatIsNewFlag = false;
						}
					}
				}
			}
		});
	}
	
	ListModel getConnectedClientsAliasList(ArrayList<User> users){
		DefaultListModel model = new DefaultListModel();	
		
		for (int i = 0; i < users.size(); i++) {
			model.addElement(users.get(i).alias);
         }
		return model;
	}
	
	void processRecievedMessage() {
		if(this.appConfig.isNewMessageRecieved()) {
			Message recievedMessage = this.appConfig.getRecievedMessage();
			String recievedMessageFlag = recievedMessage.flag;

			if(recievedMessageFlag.equals("PrivateChat")) {
				updatePrivateChatConfigWithNewMessage(recievedMessage);
			}else if(recievedMessageFlag.equals("GroupChat")) {
				updateGroupChatConfigWithNewMessage(recievedMessage);
			}			
		}			
	}
	
	void updatePrivateChatConfigWithNewMessage(Message recievedMessage) {		
		int destinyPrivateChatConfigIndex = getPrivateChatConfigIndexByUser(recievedMessage.originUser);
		if(destinyPrivateChatConfigIndex != -1) {
			PrivateChatConfig destinyPrivateChatConfig = this.connectedPrivateChatConfigs.get(destinyPrivateChatConfigIndex); 
			
			destinyPrivateChatConfig.updateStoredConversation(recievedMessage.message);
			destinyPrivateChatConfig.setLastRecievedMessage(recievedMessage);
		}
	}
	
	void updateGroupChatConfigWithNewMessage(Message recievedMessage) {		
		int destinyGroupChatConfigIndex = getPrivateChatConfigIndexByUser(recievedMessage.originUser);
		if(destinyGroupChatConfigIndex != -1) {
			GroupChatConfig destinyGroupChatConfig = this.connectedGroupChatConfigs.get(destinyGroupChatConfigIndex); 
			
			destinyGroupChatConfig.updateStoredConversation(recievedMessage.message);
			destinyGroupChatConfig.setLastRecievedMessage(recievedMessage);
		}
	}

	int getPrivateChatConfigIndexByUser(User reciever) {
		int privateChatConfigIndex;
		boolean configWasFound = false;
		for (privateChatConfigIndex = 0; privateChatConfigIndex < this.connectedPrivateChatConfigs.size(); privateChatConfigIndex++) {
            if(this.connectedPrivateChatConfigs.get(privateChatConfigIndex).recieverClient.alias.equals(reciever.alias)) {
            	configWasFound = true;
            	break;
            }
         }
		
		if(!configWasFound) {
			privateChatConfigIndex = -1;
		}
		return privateChatConfigIndex;
	}
	
	int getGroupChatConfigIndexByChatId(int chatId) {
		int groupChatConfigIndex;
		boolean configWasFound = false;
		for (groupChatConfigIndex = 0; groupChatConfigIndex < this.connectedGroupChatConfigs.size(); groupChatConfigIndex++) {
            if(this.connectedGroupChatConfigs.get(groupChatConfigIndex).chatId == chatId) {
            	configWasFound = true;
            	break;
            }
         }
		
		if(!configWasFound) {
			groupChatConfigIndex = -1;
		}
		return groupChatConfigIndex;
	}
	
	User getUserFromConnectedClientsByUserAlias(String recieverAlias) {
		int userIndexInConnectedClientsList;
		int resultIndex =  -1;
		for (userIndexInConnectedClientsList = 0; userIndexInConnectedClientsList < this.connectedClients.size(); userIndexInConnectedClientsList++) {
            if(this.connectedClients.get(userIndexInConnectedClientsList).alias == recieverAlias) {
            	resultIndex = userIndexInConnectedClientsList;
            	break;
            }
         }
		return this.connectedClients.get(userIndexInConnectedClientsList);
	}
	
	PrivateChatConfig getPrivateChatConfig() {
		PrivateChatConfig selectedPrivateChatConfig;
		if(this.selectedPrivateChatIsNewFlag) { 
			selectedPrivateChatConfig = new PrivateChatConfig(this.appConfig.getClientUser(), this.recieverUser);
			this.connectedPrivateChatConfigs.add(selectedPrivateChatConfig);			
		}else {
			selectedPrivateChatConfig = this.connectedPrivateChatConfigs.get(this.selectedPrivateChatId);
		}
		return selectedPrivateChatConfig;
	}
	
	GroupChatConfig getGroupChatConfig() {
		GroupChatConfig selectedGroupChatConfig;
		if(this.selectedGroupChatIsNewFlag) {
			selectedGroupChatConfig = new GroupChatConfig(this.connectedGroupChatConfigs.size() + 1, this.appConfig.getClientUser(), this.selectedClientsForGroupChat);
			this.connectedGroupChatConfigs.add(selectedGroupChatConfig);			
		}else {
			selectedGroupChatConfig = this.connectedGroupChatConfigs.get(this.selectedGroupChatId);
		}
		return selectedGroupChatConfig;
	}
	
	PrivateChatConfig updatePrivateChatConfigByUser(Message message, PrivateChat privateChatView) {
		User senderUser = message.originUser;
		int privateChatConfigIndex = getPrivateChatConfigIndexByUser(senderUser);
		PrivateChatConfig userPrivateChatConfig = this.connectedPrivateChatConfigs.get(privateChatConfigIndex); 
		userPrivateChatConfig.lastRecievedMessage = message;
		userPrivateChatConfig.updateStoredConversation(senderUser.alias + ": " + message.message + "\n");
		this.connectedPrivateChatConfigs.set(privateChatConfigIndex, userPrivateChatConfig); 		
		return userPrivateChatConfig;
	}
	
	void updateGroupChatConfigByUser(Message message, GroupChat groupChatView) {
		User senderGroup = message.originUser;
		int senderChatId = Integer.parseInt(senderGroup.alias.split("-")[1]);
		int groupChatConfigIndex = getGroupChatConfigIndexByChatId(senderChatId);
		GroupChatConfig userGroupChatConfig = this.connectedGroupChatConfigs.get(groupChatConfigIndex); 
		
		userGroupChatConfig.lastRecievedMessage = message;
		userGroupChatConfig.updateStoredConversation(senderGroup.alias + ": " + message.message + "\n");
		this.connectedGroupChatConfigs.set(groupChatConfigIndex, userGroupChatConfig); 
		groupChatView.setConfig(userGroupChatConfig);	
	}
	
	public void updateRegisteredClientsList(ArrayList<User> registerClients) {
		this.connectedClients = registerClients;
		this.clientsList.setModel(getConnectedClientsAliasList(registerClients));
	}
	
	boolean startPrivateChat() {
		return !this.groupChatCheckBox.isSelected();
	}
	
	void setClientUser() {
		aliasLabel.setText("ALIAS: " + this.appConfig.getClientUser().alias);
	}
	
	void clearClientsList() {
		this.clientsList.clearSelection();
	}
}
