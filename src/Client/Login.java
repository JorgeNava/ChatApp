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

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.Dimension;
import javax.swing.JTextArea;

public class Login extends JPanel {
	JTextField aliasField;
	JLabel aliasLabel;
	String clientAlias;
	User clientUser;
	
	public Login() {
		setLayout(null);
		this.setPreferredSize(new Dimension(420, 177));

		aliasField = new JTextField();
		aliasField.setText("JorgeNava_");
		aliasField.setBounds(73, 73, 142, 25);
		add(aliasField);
		aliasField.setColumns(10);

		aliasLabel = new JLabel("ALIAS");
		aliasLabel.setBounds(73, 46, 56, 16);
		add(aliasLabel);
		
	}
	
	public User getClientUser() {
		this.clientUser = new User( aliasField.getText(), 8001);
		return this.clientUser;
	}
}
