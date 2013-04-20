package server.message;


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
			String receiverIpAddress, int receiverPort,String message) {
		super(senderIpAddress, senderPort, receiverIpAddress, receiverPort);
		this.reply = message;
		// TODO Auto-generated constructor stub
	}
	//================Getter and Setter methods======================
	public String getReply() {
		return reply;
	}
	public void setReply(String reply) {
		this.reply = reply;
	}
	@Override
	public void doOperation(Interactable message, boolean myClient,
			String assignedServerIp) {
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
	public String getClientIp() {
		// TODO Auto-generated method stub
		return super.getSenderIpAddress();
	}
	@Override
	public String getDestinationIp() {
		// TODO Auto-generated method stub
		return super.getReceiverIpAddress();
	}
	
}