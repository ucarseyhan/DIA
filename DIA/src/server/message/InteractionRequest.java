package server.message;


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
	private int targetClientId = 0;
	private String targetClientIp = "";
	private String myIp = "";
	//Default Constructor
	public InteractionRequest(){
	}
	//Specified Constructor
	
	//=======Getter and setter method==========================
	public String getMessage() {
		return message;
	}
	public InteractionRequest(String message, int targetClientId,String targetClientIp,String myIp) {

		this.message = message;
		this.targetClientId = targetClientId;
		this.targetClientIp = targetClientIp;
		this.myIp = myIp;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public int getTargetClientId() {
		return targetClientId;
	}
	public void setTargetClientId(int targetClientId) {
		this.targetClientId = targetClientId;
	}

	public String getTargetClientIp() {
		return targetClientIp;
	}

	public void setTargetClientIp(String targetClientIp) {
		this.targetClientIp = targetClientIp;
	}

	public String getMyIp() {
		return myIp;
	}

	public void setMyIp(String myIp) {
		this.myIp = myIp;
	}
	
	
	
	

}
