package Client;

public class User {
	public String alias;
	public int port;
	
	public User(String alias, int port) {
		this.alias = alias;
		this.port = port;
	}
	
	public User(String alias) {
		this.alias = alias;
		this.port = -1;
	}
	
	public User() {
		this.alias = "";
		this.port = -1;
	}
}
