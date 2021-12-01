package Client;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Chat extends JFrame{
	static final String HEADER = "CHAT";
	static final String LOGIN_VIEW_ID = "LOGIN";
	static final String LOBBY_VIEW_ID = "LOBBY";
	static final String PRIVATE_CHAT_VIEW_ID = "PRIVATE CHAT";
	static final String GROUP_CHAT_VIEW_ID = "GROUP CHAT";
	static final int BOUNDS_X = 660, BOUNDS_Y = 389, BOUNDS_W = 130, BOUNDS_H = 28;
	DatagramSocket socket;

	public static void main(String[] args) throws SocketException {
		new Chat();
	}
	
	Chat() throws SocketException{
		super(HEADER);
		this.socket = new DatagramSocket();
		
	    CardLayout viewsCardLayout = new CardLayout(5, 5);
	    JPanel viewsContainer = new JPanel(viewsCardLayout);
	    Login loginView = new Login();
	    Lobby lobbyView = new Lobby();
	    PrivateChat privateChatView = new PrivateChat();
	    GroupChat groupChatView = new GroupChat();
        JButton loginView_Btn = new JButton("LOGIN"); 
        JButton lobbyView_Btn = new JButton("START");	
        JButton privateChatView_Btn = new JButton("BACK");
        JButton groupChatView_Btn = new JButton("RETURN TO LOBBY");
		
        
        loginView_Btn.setBounds(224, 73, 97, 25);
        lobbyView_Btn.setBounds(313, 141, 97, 25);
        privateChatView_Btn.setBounds(342, 154, 67, 15);
        groupChatView_Btn.setBounds(BOUNDS_X, BOUNDS_Y, BOUNDS_W, BOUNDS_H);
        loginView.add(loginView_Btn);
        lobbyView.add(lobbyView_Btn);
        privateChatView.add(privateChatView_Btn);
        privateChatView.add(groupChatView_Btn);
        viewsContainer.add(loginView, LOGIN_VIEW_ID);       
        viewsContainer.add(lobbyView, LOBBY_VIEW_ID);
        viewsContainer.add(privateChatView, PRIVATE_CHAT_VIEW_ID);
        viewsContainer.add(groupChatView, GROUP_CHAT_VIEW_ID);

        loginView_Btn.addActionListener(e -> {
        	try {
				loginView.startLogin(this.socket);
				lobbyView.setClientUser(loginView.getClientUser());
				viewsCardLayout.show(viewsContainer, LOBBY_VIEW_ID); // LOGIN - LOBBY
			} catch (IOException e1) {
				e1.printStackTrace();
			}
        });
        lobbyView_Btn.addActionListener(e -> {
        	if(lobbyView.startPrivateChat() && !lobbyView.selectedChatIsGroupFlag) {
        		privateChatView.setConfig(lobbyView.getPrivateChatConfig());
        		viewsCardLayout.show(viewsContainer, PRIVATE_CHAT_VIEW_ID); // LOBBY - PRIVATE CHAT        		
        	}else {
         		groupChatView.setConfig(lobbyView.getGroupChatConfig());
        		viewsCardLayout.show(viewsContainer, GROUP_CHAT_VIEW_ID); // LOBBY - GROUP CHAT    
        	}
        });
        privateChatView_Btn.addActionListener(e -> {
        	viewsCardLayout.show(viewsContainer, LOBBY_VIEW_ID); // PRIVATE CHAT - LOBBY
        });
        groupChatView_Btn.addActionListener(e -> {
        	viewsCardLayout.show(viewsContainer, LOGIN_VIEW_ID);
        });

        viewsCardLayout.show(viewsContainer, LOGIN_VIEW_ID);
        this.add(viewsContainer);
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
