package server.message;

import java.io.Serializable;
import java.util.Hashtable;

import server.ClientMetaData;
import server.Constants;
import server.Operation;
import server.ServerMetaData;
import server.Time;

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

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public boolean isEndResult() {
		return endResult;
	}

	public void setEndResult(boolean endResult) {
		this.endResult = endResult;
	}

	public Hashtable<String, Long> getServerDelay() {
		return serverDelay;
	}

	public void setServerDelay(Hashtable<String, Long> serverDelay) {
		this.serverDelay = serverDelay;
	}
	

	public boolean isFromServer() {
		return fromServer;
	}

	public void setFromServer(boolean fromServer) {
		this.fromServer = fromServer;
	}

	@Override
	public void doOperation(Interactable message, boolean myClient,
			String assignedServerIp) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getClientIp() {
		// TODO Auto-generated method stub
		return ip;
	}

	@Override
	public String getDestinationIp() {
		// TODO Auto-generated method stub
		return serverIp;
	}

	@Override
	public String getReceiverIpAddress() {
		// TODO Auto-generated method stub
		return serverIp;
	}

	@Override
	public String getSenderIpAddress() {
		// TODO Auto-generated method stub
		return ip;
	}

	@Override
	public int getSenderPort() {
		// TODO Auto-generated method stub
		return Constants.PORT;
	}

	@Override
	public int getReceiverPort() {
		// TODO Auto-generated method stub
		return Constants.PORT;
	}

	@Override
	public Operation getOperation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isServer() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ServerMetaData getServerMetaData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ClientMetaData getClientMetaData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Time getTime() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getConnectedServer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAddWaitingList() {
		// TODO Auto-generated method stub
		return false;
	}
	
	

}
