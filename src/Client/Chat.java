package Client;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Chat extends JFrame{
	static final String HEADER = "CHAT";
	final String LOGIN_VIEW_ID = "LOGIN";
	final String LOBBY_VIEW_ID = "LOBBY";
	final String PRIVATE_CHAT_VIEW_ID = "PRIVATE CHAT";
	final String GROUP_CHAT_VIEW_ID = "GROUP CHAT";
	
	AppConfiguration appConfig = AppConfiguration.getInstance();
	Thread listener;

    CardLayout viewsCardLayout;
    JPanel viewsContainer;
    Login loginView;
    Lobby lobbyView;
    PrivateChat privateChatView;
    GroupChat groupChatView;
    JButton loginView_Btn; 
    JButton lobbyView_Btn;	
    JButton privateChatView_Btn;
    JButton groupChatView_Btn;
	
	public static void main(String[] args) throws SocketException {
		new Chat();
	}
	
	Chat() throws SocketException{
		super(HEADER);
		this.appConfig.setSocket(new DatagramSocket());
		
		this.listener = new Thread(new Listener(this));
		this.listener.start();
		
		this.viewsCardLayout = new CardLayout(5, 5);
		this.viewsContainer = new JPanel(viewsCardLayout);
		this.loginView = new Login();
		this.lobbyView = new Lobby();
		this.privateChatView = new PrivateChat();
		this.groupChatView = new GroupChat();
		this.loginView_Btn = new JButton("LOGIN"); 
		this.lobbyView_Btn = new JButton("START");	
		this.privateChatView_Btn = new JButton("BACK");
		this.groupChatView_Btn = new JButton("BACK");
		
        
		this.loginView_Btn.setBounds(224, 73, 97, 25);
		this.lobbyView_Btn.setBounds(313, 141, 97, 25);
		this.privateChatView_Btn.setBounds(342, 194, 95, 15);
		this.groupChatView_Btn.setBounds(342, 194, 95, 15);
		this.loginView.add(loginView_Btn);
		this.lobbyView.add(lobbyView_Btn);
		this.privateChatView.add(privateChatView_Btn);
		this.groupChatView.add(groupChatView_Btn);
		this.viewsContainer.add(loginView, LOGIN_VIEW_ID);       
		this.viewsContainer.add(lobbyView, LOBBY_VIEW_ID);
		this.viewsContainer.add(privateChatView, PRIVATE_CHAT_VIEW_ID);
		this.viewsContainer.add(groupChatView, GROUP_CHAT_VIEW_ID);

		this.loginView_Btn.addActionListener(e -> {
			loginView.startLogin();
			lobbyView.setClientUser();
			this.appConfig.setActualView(LOBBY_VIEW_ID);
			viewsCardLayout.show(viewsContainer, LOBBY_VIEW_ID); // LOGIN - LOBBY
        });
		this.lobbyView_Btn.addActionListener(e -> {
        	if(lobbyView.startPrivateChat() && !lobbyView.selectedChatIsGroupFlag) {
        		this.appConfig.setActualView(PRIVATE_CHAT_VIEW_ID);        		
        		privateChatView.setConfig(lobbyView.getPrivateChatConfig());
        		viewsCardLayout.show(viewsContainer, PRIVATE_CHAT_VIEW_ID); // LOBBY - PRIVATE CHAT        		
        	}else {
        		this.appConfig.setActualView(GROUP_CHAT_VIEW_ID);
        		System.out.println("Updating group chat config by chat.java");        		
         		groupChatView.setConfig(lobbyView.getGroupChatConfig());
        		System.out.println("GROUP CHAT ID (CHAT.java): "+groupChatView.chatConfig.chatId);
        		System.out.println("GROUP CHAT STORED CONV (CHAT.java): "+groupChatView.chatConfig.storedConversation);
        		viewsCardLayout.show(viewsContainer, GROUP_CHAT_VIEW_ID); // LOBBY - GROUP CHAT    
        	}
        });
		this.privateChatView_Btn.addActionListener(e -> {
			this.appConfig.setActualView(LOBBY_VIEW_ID);
        	viewsCardLayout.show(viewsContainer, LOBBY_VIEW_ID); // PRIVATE CHAT - LOBBY
        });
		this.groupChatView_Btn.addActionListener(e -> {
			this.appConfig.setActualView(LOBBY_VIEW_ID); 
			System.out.println("=== GROUP CHAT - LOBBY (CHAT.java) ===");
			this.appConfig.getRecievedMessage().printMessageData();
			lobbyView.updateRegisteredClientsList(this.appConfig.getRecievedMessage().registeredClients);
        	viewsCardLayout.show(viewsContainer, LOBBY_VIEW_ID); // GROUP CHAT - LOBBY
        });

		this.viewsCardLayout.show(this.viewsContainer, LOGIN_VIEW_ID);
        this.add(this.viewsContainer);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setResizable(false);
        this.setVisible(true);
        this.pack();
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Point middle = new Point(screenSize.width / 2, screenSize.height / 2);
        Point newLocation = new Point(middle.x - (this.getWidth() / 2), 
                                      middle.y - (this.getHeight() / 2));
        this.setLocation(newLocation);
	}
}
