package client.message;

import client.ClientMetaData;
import client.Operation;
import client.ServerMetaData;
import client.Time;

public interface Interactable 
{
	
	public void doOperation(Interactable message,boolean myClient,String assignedServerIp);
	public String getClientIp();
	public String getDestinationIp();
	public String getReceiverIpAddress();
	public String getSenderIpAddress();
	public int getSenderPort();
	public int getReceiverPort();
	public Operation getOperation();
	public boolean isServer();
	public boolean isAddWaitingList();
	public ServerMetaData getServerMetaData();
	public ClientMetaData getClientMetaData();
	public Time getTime();
	public String getConnectedServer();
	public void setRequestCompleted();
	public String getInteractIP();
	public int getSequence();
	public void setSequence(int seq);

}
