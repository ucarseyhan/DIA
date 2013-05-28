package messages;

import java.io.Serializable;
import java.util.Hashtable;

/**
 * This class is used for representing the client
 * in the system. By using these clients the current
 * client knows the other clients for interaction.
 * 
 * @author ucar
 * Windows
 */
public class ClientMetaData implements Comparable<ClientMetaData>,Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//Declare variable
	private int port = 0;
	private String ipAddress = "";
	private String connectedServerIp = "";
	private Time lastTransaction;
	private long latencyToQoSServer; 
	private boolean isQoSAssignment = false;
	private Hashtable<String, Long> clientServerLatency;
	
	//Specified constructor
	public ClientMetaData(int port, String ipAddress,String serverIp) 
	{
		this.port = port;
		this.ipAddress = ipAddress;
		this.connectedServerIp = serverIp;
		lastTransaction = new Time();
		clientServerLatency = new Hashtable<String, Long>();
	}
	
	public ClientMetaData(int port, String ipAddress,String serverIp,Hashtable<String, Long> latency) 
	{
		this.port = port;
		this.ipAddress = ipAddress;
		this.connectedServerIp = serverIp;
		lastTransaction = new Time();
		clientServerLatency = latency;
	}
	
	//Default constructor.
	public ClientMetaData()
	{
		lastTransaction = new Time();
	}
	
	@Override
	/**
	 * Compare the latency data and return if the
	 * current latency is bigger than compared one. 
	 */
	public int compareTo(ClientMetaData cmd) 
	{
		// TODO Auto-generated method stub
		long elapsedTime  = cmd.getLatencyToQoSServer();
		if(latencyToQoSServer > elapsedTime) return 1;
		return 0;
	}
		
	
	//========Getter and Setter Methods
	public int getPort() 
	{
		return port;
	}

	public void setPort(int port) 
	{
		this.port = port;
	}

	public String getIpAddress() 
	{
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) 
	{
		this.ipAddress = ipAddress;
	}

	public String getConnectedServerIp() {
		return connectedServerIp;
	}

	public void setConnectedServerIp(String connectedServerIp) 
	{
		this.connectedServerIp = connectedServerIp;
	}

	public Time getLastTransaction() 
	{
		return lastTransaction;
	}

	public void setLastTransaction(Time lastTransaction) 
	{
		this.lastTransaction = lastTransaction;
	}

	public long getLatencyToQoSServer() 
	{
		return latencyToQoSServer;
	}

	public void setLatencyToQoSServer(long latencyToQoSServer) 
	{
		this.latencyToQoSServer = latencyToQoSServer;
	}

	public Hashtable<String, Long> getClientServerLatency() 
	{
		return clientServerLatency;
	}

	public void setClientServerLatency(Hashtable<String, Long> clientServerLatency) 
	{
		this.clientServerLatency = clientServerLatency;
	}

	public boolean isQoSAssignment() 
	{
		return isQoSAssignment;
	}

	public void setQoSAssignment(boolean isQoSAssignment) 
	{
		this.isQoSAssignment = isQoSAssignment;
	}
	
	


	
	
	
	
	

}
