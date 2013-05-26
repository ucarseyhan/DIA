package client.message;

import java.io.Serializable;
import java.util.Hashtable;

import client.ClientMetaData;
import client.Constants;
import client.Operation;
import client.ServerMetaData;
import client.Time;


public class InitialMessage implements Interactable,Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String ip = "";
	private String serverIp = "";
	private boolean endResult = false;
	private boolean fromServer = false;
	private Hashtable<String, Long> serverDelay;
	
	public InitialMessage()
	{
		
	}
	
	public InitialMessage(String cIp,String sIp)
	{
		this.ip = cIp;
		this.serverIp = sIp;
		this.endResult = false;
		serverDelay = new Hashtable<String, Long>();
	}

	public String getIp() 
	{
		return ip;
	}

	public void setIp(String ip) 
	{
		this.ip = ip;
	}

	public String getServerIp()
	{
		return serverIp;
	}

	public void setServerIp(String serverIp) 
	{
		this.serverIp = serverIp;
	}

	public boolean isEndResult() 
	{
		return endResult;
	}

	public void setEndResult(boolean endResult) 
	{
		this.endResult = endResult;
	}

	public Hashtable<String, Long> getServerDelay() 
	{
		return serverDelay;
	}

	public void setServerDelay(Hashtable<String, Long> serverDelay) 
	{
		this.serverDelay = serverDelay;
	}
	

	public boolean isFromServer() 
	{
		return fromServer;
	}

	public void setFromServer(boolean fromServer) 
	{
		this.fromServer = fromServer;
	}

	@Override
	public void doOperation(Interactable message, boolean myClient,
			String assignedServerIp) 
	{
		
	}

	@Override
	public String getClientIp() 
	{
		return ip;
	}

	@Override
	public String getDestinationIp() 
	{
		return serverIp;
	}

	@Override
	public String getReceiverIpAddress() 
	{
		return serverIp;
	}

	@Override
	public String getSenderIpAddress() 
	{
		return ip;
	}

	@Override
	public int getSenderPort() 
	{
		return Constants.PORT;
	}

	@Override
	public int getReceiverPort()
	{
		return Constants.PORT;
	}

	@Override
	public Operation getOperation() 
	{
		return null;
	}

	@Override
	public boolean isServer() 
	{
		return false;
	}

	@Override
	public ServerMetaData getServerMetaData() 
	{
		return null;
	}

	@Override
	public ClientMetaData getClientMetaData() 
	{
		return null;
	}

	@Override
	public Time getTime() 
	{
		return null;
	}

	@Override
	public String getConnectedServer() 
	{
		return null;
	}

	@Override
	public boolean isAddWaitingList() 
	{
		return false;
	}

	@Override
	public void setRequestCompleted() 
	{
	}

	@Override
	public String getInteractIP() 
	{
		return null;
	}

	@Override
	public int getSequence() 
	{
		return 0;
	}

	@Override
	public void setSequence(int seq) 
	{	
	}
	
	

}
