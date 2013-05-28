package messages;

/**
 * This class is used for sending the server response to user.In
 * other words after interaction target server informs the client
 * by sending the reply.
 * 
 * @author ucar
 *
 */
public class InteractionResponse extends InteractionMessage implements Interactable 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//Declare variable.
	private String reply = "";
	private boolean isQoSAssignment = false;
	//Default constructor.
	public InteractionResponse()
	{
		super();
	}
	//Specified constructor.
	public InteractionResponse(String reply) 
	{
		super();
		this.reply = reply;
	}
	
	public InteractionResponse(String senderIpAddress, int senderPort,
			String receiverIpAddress, int receiverPort,String message) 
	{
		super(senderIpAddress, senderPort, receiverIpAddress, receiverPort);
		this.reply = message;
		// TODO Auto-generated constructor stub
	}
	@Override
	public String getClientIp() 
	{
		return super.getClientIp();
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
	public void setinteractedClient(String ip)
	{
		super.setInteractedClientIP(ip);
	}
	//================Getter and Setter methods======================
	public String getReply() 
	{
		return reply;
	}
	public void setReply(String reply) 
	{
		this.reply = reply;
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
