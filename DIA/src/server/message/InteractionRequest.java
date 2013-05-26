package server.message;
import server.ClientMetaData;
import server.Operation;
import server.ServerMetaData;
import server.Time;

/**
 * This class is used for the client interaction request.When the 
 * client wants to communicate with other client it generates the 
 * request interaction message and send directly to the assigned 
 * server. Then by controlling the timer value it waits the reply 
 * from server.
 * 
 * @author ucar
 *
 */
public class InteractionRequest extends InteractionMessage implements Interactable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//Declare variable
	private String message = "";
	//Default Constructor
	public InteractionRequest()
	{
		super();
	}

	@Override
	public String getClientIp() 
	{
		return super.getSenderIpAddress();
	}
	@Override
	public String getDestinationIp() 
	{
		return super.getReceiverIpAddress();
	}
	@Override
	public Operation getOperation()
	{
		return super.getOperation();
		
	}
	@Override
	public boolean isServer() 
	{
		return super.isServerRole();
	}
	@Override
	public ServerMetaData getServerMetaData()
	{
		return super.getServerMetaData();
	}
	@Override
	public ClientMetaData getClientMetaData()
	{
		return super.getClientMetaData();
	}
	@Override
	public Time getTime()
	{
		return super.getTime();
	}
	@Override
	public String getConnectedServer()
	{
		return super.getConnectedServer();
	}
	@Override
	public boolean isAddWaitingList()
	{
		return super.isAddWaitingList();
	}


	//=======Getter and setter method==========================
	public String getMessage() {
		return message;
	}
	public InteractionRequest(String message, 
							  int targetClientId,
							  String targetClientIp,
							  String myIp) 
	{
		
		super();
		super.setReceiverIpAddress(targetClientIp);
		super.setSenderIpAddress(myIp);
		this.message = message;
		
	}
	public void setMessage(String message) 
	{
		this.message = message;
	}
	public String getTargetClientIp() 
	{
		return super.getReceiverIpAddress();
	}


	public String getMyIp() 
	{
		return super.getSenderIpAddress();
	}



















	
	
	
	

}
