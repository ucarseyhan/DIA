package messages;

import java.io.Serializable;
import java.util.Hashtable;



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
	private Time time;
	
	public InitialMessage()
	{
		
	}
	
	public InitialMessage(String cIp,String sIp)
	{
		this.ip = cIp;
		this.serverIp = sIp;
		this.endResult = false;
		serverDelay = new Hashtable<String, Long>();
		this.time = new Time();
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

	public Hashtable<String, Long> getServerDelay() 
	{
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
		// TODO Auto-generated method stub
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
		return time;
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
