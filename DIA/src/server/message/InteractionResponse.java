package server.message;
import server.ClientMetaData;
import server.Operation;
import server.ServerMetaData;
import server.Time;


/**
 * This class is used for sending the server response to user.In
 * other words after interaction target server informs the client
 * by sending the reply.
 * 
 * @author ucar
 *
 */
public class InteractionResponse extends InteractionMessage implements Interactable {
	//Declare variable.
	private String reply = "";
	//Default constructor.
	public InteractionResponse(){
		super();
	}
	//Specified constructor.
	public InteractionResponse(String reply) {
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
	public String getClientIp() {
		// TODO Auto-generated method stub
		return super.getSenderIpAddress();
	}
	@Override
	public String getDestinationIp() {
		// TODO Auto-generated method stub
		return super.getReceiverIpAddress();
	}
	@Override
	public Operation getOperation(){
		return super.getOperation();
		
	}
	@Override
	public boolean isServer() {
		// TODO Auto-generated method stub
		return super.isServerRole();
	}
	@Override
	public void doOperation(Interactable message, boolean myClient,String assignedServerIp) 
	{
		try {
			String ip = getReceiverIpAddress();
			int port = getReceiverPort();
			forward(ip, port, message);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}	
	}
	@Override
	public ServerMetaData getServerMetaData(){
		return super.getServerMetaData();
	}
	@Override
	public ClientMetaData getClientMetaData(){
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
	//================Getter and Setter methods======================
	public String getReply() {
		return reply;
	}
	public void setReply(String reply) {
		this.reply = reply;
	}


	
}
