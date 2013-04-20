package client.message;


/**
 * This class is used for the client when it is in the 
 * UNDECIDED state.When the clients enter the system it
 * first checks the controller by sending this message to
 * nearest server and request the current controller information.
 * Via using this it changes state to SERVERELECTION and 
 * wait one of the server assignment.
 *  
 * @author ucar
 *
 */
public class ControllerInformationMessage extends Message {
	//Declare variables.
	private String clientIPAddress = "";
	private int clientPort = 0;
	private String nearestIpAddress = "";
	private int nearestPort = 0;
	private String controllerIPAddress = "";
	private int controllerPort = 0;
	
	//Default constructor.
	public ControllerInformationMessage(){
		
	}
	//Specified constructor
	public ControllerInformationMessage(String clientIPAddress, int clientPort,
			String nearestIpAddress, int nearestPort,
			String controllerIPAddress, int controllerPort) {
		this.clientIPAddress = clientIPAddress;
		this.clientPort = clientPort;
		this.nearestIpAddress = nearestIpAddress;
		this.nearestPort = nearestPort;
		this.controllerIPAddress = controllerIPAddress;
		this.controllerPort = controllerPort;
	}
	//===========Getter and Setter Method=================

	public String getClientIPAddress() {
		return clientIPAddress;
	}
	public void setClientIPAddress(String clientIPAddress) {
		this.clientIPAddress = clientIPAddress;
	}

	public String getControllerIPAddress() {
		return controllerIPAddress;
	}

	public void setControllerIPAddress(String controllerIPAddress) {
		this.controllerIPAddress = controllerIPAddress;
	}

	public int getClientPort() {
		return clientPort;
	}

	public void setClientPort(int clientPort) {
		this.clientPort = clientPort;
	}

	public String getNearestIpAddress() {
		return nearestIpAddress;
	}

	public void setNearestIpAddress(String nearestIpAddress) {
		this.nearestIpAddress = nearestIpAddress;
	}

	public int getNearestPort() {
		return nearestPort;
	}

	public void setNearestPort(int nearestPort) {
		this.nearestPort = nearestPort;
	}

	public int getControllerPort() {
		return controllerPort;
	}

	public void setControllerPort(int controllerPort) {
		this.controllerPort = controllerPort;
	}
	
	
	
	
}
