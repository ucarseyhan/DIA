package client.message;


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
public class InteractionRequest extends InteractionMessage {
	//Declare variable
	private String message = "";
	//Default Constructor
	public InteractionRequest(){
	}
	//Specified Constructor
	
	//=======Getter and setter method==========================
	public String getMessage() {
		return message;
	}

	public InteractionRequest(String senderIpAddress,
							  int senderPort,
							  String receiverIpAddress,
							  int receiverPort,String message)
	{
		super(senderIpAddress, senderPort, receiverIpAddress, receiverPort);
		this.message = message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	

	
	
	
	

}