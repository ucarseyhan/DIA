package server.message;



import server.ClientMetaData;
import server.Operation;
import server.ServerMetaData;
import server.Time;

public interface Interactable {
	
	public void doOperation(Interactable message,boolean myClient,String assignedServerIp);
	public String getClientIp();
	public String getDestinationIp();
	public String getReceiverIpAddress();
	public String getSenderIpAddress();
	public int getSenderPort();
	public int getReceiverPort();
	public Operation getOperation();
	public boolean isServer();
	public ServerMetaData getServerMetaData();
	public ClientMetaData getClientMetaData();
	public Time getTime();
	public String getConnectedServer();

}
