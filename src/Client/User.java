package Client;

public class User {
	public String alias;
	public int port;
	
	public User(String alias, int port) {
		this.alias = alias;
		this.port = port;
	}
	
	public User() {
		this.alias = "";
		this.port = -1;
	}
}
