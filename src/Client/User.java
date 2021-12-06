package Client;

import java.io.Serializable;

public class User implements Serializable{
	public String alias;
	public int port;
	
	public User(String alias, int port) {
		this.alias = alias;
		this.port = port;
	}
	public User(String alias) {
		this.alias = alias;
	}
	public User() {
		this.alias = "";
		this.port = -1;
	}
}
