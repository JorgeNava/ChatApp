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
	static final int BOUNDS_X = 660, BOUNDS_Y = 389, BOUNDS_W = 130, BOUNDS_H = 28;
	
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
		/* 
		 * ! MISSING TO SEND this.recievedMessage TO ALL COMPONENTS AND ANALYZE HOW
		 * ! TO IDENTIFY IN WHICH CHAT SHOULD BE PRINTED
		 * 
		 * ! CONSIDER CREATING A LISTENER THREAD FOR EVERY OPENED CHAT AND AN EXTRA
		 * ! ONE TO ALWAYS BE GETTING UPDATED registeredClients LIST
		 * 
		 * ! ADD FUNCTIONALITY TO SEND MESSAGES WHEN PRIVATE/GROUP CHATS BUTTONS IS PRESSED
		 */
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
		this.groupChatView_Btn = new JButton("RETURN TO LOBBY");
		
        
		this.loginView_Btn.setBounds(224, 73, 97, 25);
		this.lobbyView_Btn.setBounds(313, 141, 97, 25);
		this.privateChatView_Btn.setBounds(342, 154, 67, 15);
		this.groupChatView_Btn.setBounds(BOUNDS_X, BOUNDS_Y, BOUNDS_W, BOUNDS_H);
		this.loginView.add(loginView_Btn);
		this.lobbyView.add(lobbyView_Btn);
		this.privateChatView.add(privateChatView_Btn);
		this.privateChatView.add(groupChatView_Btn);
		this.viewsContainer.add(loginView, LOGIN_VIEW_ID);       
		this.viewsContainer.add(lobbyView, LOBBY_VIEW_ID);
		this.viewsContainer.add(privateChatView, PRIVATE_CHAT_VIEW_ID);
		this.viewsContainer.add(groupChatView, GROUP_CHAT_VIEW_ID);

		this.loginView_Btn.addActionListener(e -> {
        	try {
				loginView.startLogin();
				lobbyView.setClientUser();
				this.appConfig.setActualView(LOBBY_VIEW_ID);
				viewsCardLayout.show(viewsContainer, LOBBY_VIEW_ID); // LOGIN - LOBBY
			} catch (IOException e1) {
				e1.printStackTrace();
			}
        });
		this.lobbyView_Btn.addActionListener(e -> {
        	if(lobbyView.startPrivateChat() && !lobbyView.selectedChatIsGroupFlag) {
        		privateChatView.setConfig(lobbyView.getPrivateChatConfig());
        		this.appConfig.setActualView(PRIVATE_CHAT_VIEW_ID);
        		viewsCardLayout.show(viewsContainer, PRIVATE_CHAT_VIEW_ID); // LOBBY - PRIVATE CHAT        		
        	}else {
         		groupChatView.setConfig(lobbyView.getGroupChatConfig());
        		this.appConfig.setActualView(GROUP_CHAT_VIEW_ID);
        		viewsCardLayout.show(viewsContainer, GROUP_CHAT_VIEW_ID); // LOBBY - GROUP CHAT    
        	}
        });
		this.privateChatView_Btn.addActionListener(e -> {
        	// loginView.setRegisteredClientsList(this.recievedMessage, this.registeredClients);
    		this.appConfig.setActualView(LOBBY_VIEW_ID);
        	viewsCardLayout.show(viewsContainer, LOBBY_VIEW_ID); // PRIVATE CHAT - LOBBY
        });
		this.groupChatView_Btn.addActionListener(e -> {
        	// loginView.setRegisteredClientsList(this.recievedMessage, this.registeredClients);
    		this.appConfig.setActualView(LOBBY_VIEW_ID);
        	viewsCardLayout.show(viewsContainer, LOGIN_VIEW_ID);
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
