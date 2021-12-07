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
		groupChatCheckBox.setBounds(80, 123, 170, 23);
		add(groupChatCheckBox);
		
		clientsList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				String recieverAlias = (String) clientsList.getSelectedValue();
				System.out.println("* ACTUAL SELECTED CLIENT: " +  recieverAlias);
				if(recieverAlias != null) {
					recieverUser = getUserFromConnectedClientsByUserAlias(recieverAlias);
					
					if(groupChatCheckBox.isSelected()) { // FOR NEW GROUP CHATS
						System.out.println("* ALL SELECTED CLIENTS: ");
						for (int i = 0; i < selectedClientsForGroupChat.size(); i++) {
							selectedClientsForGroupChat.get(i).printUser();
						}
						selectedGroupChatId = connectedGroupChatConfigs.size() + 1;
						System.out.println("* NEW GROUP CHAT ID: " + selectedGroupChatId);
						
						ArrayList<String> aliases = new ArrayList<String>();
						for (User user : selectedClientsForGroupChat) {
							aliases.add(user.alias);
						}
						if(!aliases.contains(recieverUser.alias)) {
							selectedClientsForGroupChat.add(recieverUser);
							selectedGroupChatIsNewFlag = true;						
						}
					}else {
						selectedChatIsGroupFlag = recieverAlias.startsWith("Group");
						System.out.println("* SELECTED IS A GROUP: " + selectedChatIsGroupFlag);
						if(selectedChatIsGroupFlag) {	// FOR EXISTING GROUP CHATS
							selectedGroupChatId = Integer.parseInt(recieverAlias.substring(recieverAlias.length() - 1));
							selectedGroupChatIsNewFlag = false;
							System.out.println("* SELECTED GROUP ID: " + selectedGroupChatId);
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
			}
		});
	}
	
	ListModel getConnectedClientsAliasList(ArrayList<User> users){
		DefaultListModel model = new DefaultListModel();	
		System.out.println("RECIEVED USERS IN getConnectedClientsAliasList");
		for (int i = 0; i < users.size(); i++) {
			System.out.println(users.get(i).alias);
			if(!users.get(i).alias.equals(this.appConfig.getClientUser().alias)){
				model.addElement(users.get(i).alias);				
			}
         }
		return model;
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
		System.out.println("##### "+ this.connectedGroupChatConfigs.size());
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
		System.out.println("=== getPrivateChatConfig ===");
		System.out.println("* this.selectedPrivateChatIsNewFlag: " + this.selectedPrivateChatIsNewFlag);

		if(this.selectedPrivateChatIsNewFlag) { 
			selectedPrivateChatConfig = new PrivateChatConfig(this.appConfig.getClientUser(), this.recieverUser);
			System.out.println("* connectedPrivateChatConfigs.size(): " + this.connectedPrivateChatConfigs.size());
			this.connectedPrivateChatConfigs.add(selectedPrivateChatConfig);			
		}else {
			System.out.println("* this.selectedPrivateChatId: " + this.selectedPrivateChatId);
			selectedPrivateChatConfig = this.connectedPrivateChatConfigs.get(this.selectedPrivateChatId);
		}
		return selectedPrivateChatConfig;
	}
	
	GroupChatConfig getGroupChatConfig() {
		GroupChatConfig selectedGroupChatConfig;
		System.out.println("=== getGroupChatConfig ===");
		if(this.selectedGroupChatIsNewFlag) {
			this.selectedClientsForGroupChat.add(this.appConfig.getClientUser());
			selectedGroupChatConfig = new GroupChatConfig(this.selectedGroupChatId, this.appConfig.getClientUser(), this.selectedClientsForGroupChat);
			System.out.println("* chatId: " + selectedGroupChatConfig.chatId);
			System.out.println("* getDestinyReciversAliases: " + selectedGroupChatConfig.getDestinyReciversAliases());
			this.connectedGroupChatConfigs.add(selectedGroupChatConfig);	

			User recieverClient = new User("Server", this.appConfig.getServerPort());
			String message = "";
			String flag = "RegisterGroup";

			Message groupRegistrationMessage = new Message(this.appConfig.getClientUser(), recieverClient, message, flag);
			groupRegistrationMessage.groupChatRecievers = this.selectedClientsForGroupChat;
			groupRegistrationMessage.groupChatId = selectedGroupChatConfig.chatId;
			MessageSender msgSender = new MessageSender(groupRegistrationMessage);
			msgSender.sendMessage();
		}else {
			System.out.println("* this.selectedGroupChatId: " + this.selectedGroupChatId);
			System.out.println("* connectedGroupChatConfigs size: " +  this.connectedGroupChatConfigs.size());
			System.out.println("* connectedGroupChatConfigs IDs:");
			for (GroupChatConfig config : connectedGroupChatConfigs) {
				System.out.println("** "+config.chatId);
			}
			System.out.println("* selectedGroupChatConfig: " + this.connectedGroupChatConfigs.get(this.selectedGroupChatId - 1).chatId);
			selectedGroupChatConfig = this.connectedGroupChatConfigs.get(this.selectedGroupChatId - 1);
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
	
	GroupChatConfig updateGroupChatConfigByUser(Message message, GroupChat groupChatView) {
		User senderGroup = message.originUser;
		// ! CHECK IF WE MUST REGISTER GROUPS WHITHOUT ORIGIN USERS CREATING THEM AND INSTEAD WHEN MESSAGES ARE RECIEVED
		// ! KEEP AN EYE ON THIS PART WHEN GOIGN BACK FROM GROUP CHAT
		int groupChatConfigIndex = getGroupChatConfigIndexByChatId(groupChatView.chatConfig.chatId);
		GroupChatConfig userGroupChatConfig = this.connectedGroupChatConfigs.get(groupChatConfigIndex); 
		
		userGroupChatConfig.lastRecievedMessage = message;
		userGroupChatConfig.updateStoredConversation(senderGroup.alias + ": " + message.message + "\n");
		this.connectedGroupChatConfigs.set(groupChatConfigIndex, userGroupChatConfig); 
		return userGroupChatConfig;
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

	void updateConnectedGroupChatConfigs(Message message) {
		System.out.println("=== updateConnectedGroupChatConfigs ===");
		boolean configWasAlreadyRegistered = false;
		for (GroupChatConfig config : this.connectedGroupChatConfigs) {
			if(config.chatId == message.groupChatId) {
				configWasAlreadyRegistered = true;
				break;
			}
		}
		System.out.println("* configWasAlreadyRegistered: " + configWasAlreadyRegistered);
		if(!configWasAlreadyRegistered) {
			int chatId = message.groupChatId;
			GroupChatConfig selectedGroupChatConfig = new GroupChatConfig(chatId, this.appConfig.getClientUser(), message.groupChatRecievers);
			System.out.println("* selectedGroupChatConfig.id: " + selectedGroupChatConfig.chatId);
			System.out.println("* getDestinyReciversAliases: " + selectedGroupChatConfig.getDestinyReciversAliases());
			this.connectedGroupChatConfigs.add(selectedGroupChatConfig);	
		}
	}
}
