package client;
/**
 * This class is used for representing the client
 * in the system. By using these clients the current
 * client knows the other clients for interaction.
 * 
 * @author ucar
 *
 */
public class ClientMetaData {
	
	//Declare variable
	private int port = 0;
	private String ipAddress = "";
	
	//Specified constructor
	public ClientMetaData(int port, String ipAddress) {
		super();
		this.port = port;
		this.ipAddress = ipAddress;
	}
	
	//Default constructor.
	public ClientMetaData(){
		
	}
	//========Getter and Setter Methods

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	
	

}
