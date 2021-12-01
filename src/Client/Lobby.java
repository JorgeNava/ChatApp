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
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JLabel;
import javax.swing.JList;

import java.awt.Dimension;
import javax.swing.JTextArea;
import javax.swing.JCheckBox;

public class Lobby extends JPanel {
	JLabel aliasLabel;
	JList clientsList;
	JCheckBox groupChatCheckBox;
	JScrollPane clientsScrollPane;
	User clientUser, recieverUser;
	boolean selectedChatIsGroupFlag;
	boolean selectedGroupChatIsNewFlag;
	boolean selectedPrivateChatIsNewFlag;
	int selectedGroupChatId = -1, selectedPrivateChatId = -1;
	String connectedClients[] = {"Erick", "Ana", "Nathali", "Samantha", "John", "Sam"};
	/*
	 * 
	 * === CLIENT NODE ===
	 * 	 *
	 * LOOK FOR PLACE TO PUT USERS REGISTRATION TO connectedClients_new IN SERVER
	 *
	 * REQUEST TO SERVER FROM ANOTHER PART OF THE CODE connectedClients_new
	 * WATCH THAT REQUEST IS SENT CONSTANTLY (CONSIDER CREATING THIS IN A NEW THREAD)
	 *
	 * ADD USERNAMES TO PRIVATE CHATS
	 *
	 * ADD GROUP CHAT NAME TO GROUP CHAT CONFIGS
	 *
	 * ADD "ATTACH FILE" BUTTON TO PRIVATE AND GROUP CHAT INTERFACES
	 *
	 * ADD USER PHOTO TO USERS CLASS
	 * 
	 * 
	 * === BRIDGE NODE ===
	 * 
	 * IMPLEMENT PACKETS (MAYBE CONFIGS) SENDING/RECIVIENG TO/FROM BRIDGE NODE
	 * SEND MESSAGES AND STORE THEM IN CONFIGS INSTEAD OF SENDING CONFIGS
	 * WATCH FOR GROUP CHATS (HERE WE MUST SEND FIRST A CONFIG AS A MESSAGE)
	 * 
	 * 
	 * */
	ArrayList<User> connectedClients_new = new ArrayList<User>();
	ArrayList<PrivateChatConfig> connectedPrivateChatConfigs = new ArrayList<PrivateChatConfig>();
	ArrayList<GroupChatConfig> connectedGroupChatConfigs = new ArrayList<GroupChatConfig>();
	ArrayList<User> selectedClientsForGroupChat = new ArrayList<User>();
	
	public Lobby() {
		setLayout(null);
		this.setPreferredSize(new Dimension(420, 177));
		
		// KEEP AN EYE ON THIS .toArray() SINCE IT CAN CAUSE UNWANTED WANRINGS/ERRORS
		clientsList = new JList(getConnectedClientsAliasList(connectedClients_new).toArray()); 
		clientsList.setVisibleRowCount(4);
		clientsScrollPane = new JScrollPane(clientsList);
		clientsScrollPane.setBounds(80, 40, 228, 80);
		add(clientsScrollPane);		

		aliasLabel = new JLabel("ALIAS: Default");
		aliasLabel.setBounds(10, 11, 117, 16);
		add(aliasLabel);
		
		groupChatCheckBox = new JCheckBox("Create chat");
		groupChatCheckBox.setBounds(80, 123, 97, 23);
		add(groupChatCheckBox);
		
		clientsList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				String recieverAlias = (String) clientsList.getSelectedValue();
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
						selectedPrivateChatId = getPrivateChatConfigIndexByUser(recieverUser);
					}
				}
			}
		});
	}
	
	ArrayList<String> getConnectedClientsAliasList(ArrayList<User> connectedUsersList){
		ArrayList<String> connectedClientsAlias =  new ArrayList<String>();
		for (int i = 0; i < connectedUsersList.size(); i++) {
			connectedClientsAlias.add(connectedUsersList.get(i).alias);
         }
		return connectedClientsAlias;
	}

	int getPrivateChatConfigIndexByUser(User reciever) {
		int privateChatConfigIndex;
		boolean configWasFound = false;
		for (privateChatConfigIndex = 0; privateChatConfigIndex < connectedPrivateChatConfigs.size(); privateChatConfigIndex++) {
            if(connectedPrivateChatConfigs.get(privateChatConfigIndex).recieverClient == reciever) {
            	configWasFound = true;
            	break;
            }
         }
		
		if(configWasFound) { // FOR EXISTING PRIVATE CHAT
			selectedPrivateChatIsNewFlag = false;
		}else { // FOR NEW PRIVATE CHATS
			selectedPrivateChatIsNewFlag = true;
			privateChatConfigIndex = connectedPrivateChatConfigs.size() + 1;
		}
		return privateChatConfigIndex;
	}
	
	User getUserFromConnectedClientsByUserAlias(String recieverAlias) {
		int userIndexInConnectedClientsList;
		for (userIndexInConnectedClientsList = 0; userIndexInConnectedClientsList < connectedClients_new.size(); userIndexInConnectedClientsList++) {
            if(connectedClients_new.get(userIndexInConnectedClientsList).alias == recieverAlias) {
            	break;
            }
         }
		return connectedClients_new.get(userIndexInConnectedClientsList);
	}
	
	PrivateChatConfig getPrivateChatConfig() {
		PrivateChatConfig selectedPrivateChatConfig;
		if(this.selectedPrivateChatIsNewFlag) { 
			selectedPrivateChatConfig = new PrivateChatConfig(this.clientUser, this.recieverUser);
			connectedPrivateChatConfigs.add(selectedPrivateChatConfig);			
		}else {
			selectedPrivateChatConfig = connectedPrivateChatConfigs.get(this.selectedPrivateChatId);
		}
		return selectedPrivateChatConfig;
	}
	
	GroupChatConfig getGroupChatConfig() {
		GroupChatConfig selectedGroupChatConfig;
		if(this.selectedGroupChatIsNewFlag) {
			selectedGroupChatConfig = new GroupChatConfig(this.clientUser, this.selectedClientsForGroupChat);
			connectedGroupChatConfigs.add(selectedGroupChatConfig);			
		}else {
			selectedGroupChatConfig = connectedGroupChatConfigs.get(this.selectedGroupChatId);
		}
		return selectedGroupChatConfig;
	}
	
	boolean startPrivateChat() {
		return !this.groupChatCheckBox.isSelected();
	}
	
	void setClientUser(User clientUser) {
		this.clientUser = clientUser;
		aliasLabel.setText("ALIAS: " + this.clientUser.alias);
	}
}


