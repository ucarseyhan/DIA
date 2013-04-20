package server.message;

public interface Interactable {
	
	public void doOperation(Interactable message,boolean myClient,String assignedServerIp);
	public String getClientIp();
	public String getDestinationIp();
	public String getReceiverIpAddress();
	public String getSenderIpAddress();
	public int getSenderPort();
	public int getReceiverPort();

}
