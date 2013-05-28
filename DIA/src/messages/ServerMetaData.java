package messages;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class is used for keeping the server meta data in each
 * server. So by using these server list it can disseminate the 
 * data to other nodes.
 * @author seyhan
 *
 */
public class ServerMetaData implements Serializable 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int port = Constants.PORT;
	private String serverIp = "";
	private int capacity = 0;
	private Time lastTransaction;
	private ConcurrentHashMap<String, ClientMetaData> clientList;
	
	public ServerMetaData()
	{
		clientList = new ConcurrentHashMap<String, ClientMetaData>();
		lastTransaction = new Time();
	}

	public ServerMetaData(int port, String serverIp,int capacity) 
	{
		this.port = port;
		this.serverIp = serverIp;
		this.capacity = capacity;
		clientList = new ConcurrentHashMap<String, ClientMetaData>();
		lastTransaction = new Time();
	}
	public boolean containClient(String cIP)
	{
		try 
		{
			for (String ip : clientList.keySet()) 
			{
				if(ip.equalsIgnoreCase(cIP)) return true;
			}
			return false;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return false;
		}
	}
	public void addClient(String ip, ClientMetaData cM)
	{
		if(clientList.containsKey(ip))
		{
			clientList.remove(ip);
			clientList.put(ip, cM);
		}
	}
	public int getPort() 
	{
		return port;
	}

	public void setPort(int port) 
	{
		this.port = port;
	}

	public String getServerIp() 
	{
		return serverIp;
	}

	public void setServerIp(String serverIp) 
	{
		this.serverIp = serverIp;
	}

	public int getCapacity() 
	{
		return capacity;
	}

	public void setCapacity(int capacity) 
	{
		this.capacity = capacity;
	}



	public ConcurrentHashMap<String, ClientMetaData> getClientList() {
		return clientList;
	}

	public void setClientList(ConcurrentHashMap<String, ClientMetaData> clientList) {
		this.clientList = clientList;
	}

	public Time getLastTransaction() 
	{
		return lastTransaction;
	}

	public void setLastTransaction(Time lastTransaction) 
	{
		this.lastTransaction = lastTransaction;
	}
	
	
	
	

}
