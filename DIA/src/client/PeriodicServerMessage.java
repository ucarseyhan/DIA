package client;
/**
 * The periodic server hello message is used by the clients 
 * to control the server existence. By using this message client
 * can be informed about the  new client entrance into the system.
 * So it can have chance to interact these clients as well.
 * 
 * @author ucar
 *
 */
public class PeriodicServerMessage extends Message {
	//Declare variables
	private String serverId     = ""; //Replicated server id.
	private String controllerId = ""; //COntroller server id.
	private String serverIp = "";
	
	//Default constructor
	public PeriodicServerMessage(){
		
	}
	//Specified constructor.
	public PeriodicServerMessage(String serverId, String controllerId) {
		this.serverId = serverId;
		this.controllerId = controllerId;
	}
	//===================Getter and Setter Method=========================
	public String getServerId() {
		return serverId;
	}
	public void setServerId(String serverId) {
		this.serverId = serverId;
	}
	public String getControllerId() {
		return controllerId;
	}
	public void setControllerId(String controllerId) {
		this.controllerId = controllerId;
	}
	public String getServerIp() {
		return serverIp;
	}
	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}
	
	
	
	

}
