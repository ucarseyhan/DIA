package server;
/**
 * This class is used for representing the client
 * in the system. By using these clients the current
 * client knows the other clients for interaction.
 * 
 * @author ucar
 * Windows
 */
public class ClientMetaData implements Comparable<ClientMetaData> {
	//Declare variable
	private int port = 0;
	private String ipAddress = "";
	private String connectedServerIp = "";
	private Time lastTransaction;
	private long latencyToQoSServer;
	
	//Specified constructor
	public ClientMetaData(int port, String ipAddress,String serverIp) {
		super();
		this.port = port;
		this.ipAddress = ipAddress;
		this.connectedServerIp = serverIp;
		lastTransaction = new Time();
	}
	
	//Default constructor.
	public ClientMetaData(){
		
	}
	
	@Override
	/**
	 * Compare the latency data and return if the
	 * current latency is bigger than compared one. 
	 */
	public int compareTo(ClientMetaData cmd) {
		// TODO Auto-generated method stub
		long elapsedTime  = cmd.getLatencyToQoSServer();
		if(latencyToQoSServer > elapsedTime) return 1;
		return 0;
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

	public String getConnectedServerIp() {
		return connectedServerIp;
	}

	public void setConnectedServerIp(String connectedServerIp) {
		this.connectedServerIp = connectedServerIp;
	}

	public Time getLastTransaction() {
		return lastTransaction;
	}

	public void setLastTransaction(Time lastTransaction) {
		this.lastTransaction = lastTransaction;
	}

	public long getLatencyToQoSServer() {
		return latencyToQoSServer;
	}

	public void setLatencyToQoSServer(long latencyToQoSServer) {
		this.latencyToQoSServer = latencyToQoSServer;
	}


	
	
	
	
	

}
