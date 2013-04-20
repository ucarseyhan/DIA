package server.message;

import server.ServerOperation;

/**
 * This class is super class of both client interaction request 
 * and interaction reply. Whenever clients want to interact with 
 * each other the InteractionMessage is created and send to server.
 * When server wants to response to related client it again generates
 * the interaction response message and disseminates to client.
 * 
 * @author ucar
 *
 */
public class InteractionMessage extends Message {
	//Declare variables
	private String senderIpAddress     = ""; //Sender IP address
	private int senderPort = 0;				 //Sender port
	private String receiverIpAddress   = ""; //Receiver IP address
	private int receiverPort = 0;			 //Receiver port
	private ServerOperation operation;
	//Default Constructor
	public InteractionMessage(){
		
	}
	//Specified constructor
	public InteractionMessage(String senderIpAddress, int senderPort,
			String receiverIpAddress, int receiverPort) {
		this.senderIpAddress = senderIpAddress;
		this.senderPort = senderPort;
		this.receiverIpAddress = receiverIpAddress;
		this.receiverPort = receiverPort;
	}
	//========Getter and Setter Methods======================

	public String getSenderIpAddress() {
		return senderIpAddress;
	}
	public void setSenderIpAddress(String senderIpAddress) {
		this.senderIpAddress = senderIpAddress;
	}

	public String getReceiverIpAddress() {
		return receiverIpAddress;
	}

	public void setReceiverIpAddress(String receiverIpAddress) {
		this.receiverIpAddress = receiverIpAddress;
	}
	public int getSenderPort() {
		return senderPort;
	}
	public void setSenderPort(int senderPort) {
		this.senderPort = senderPort;
	}
	public int getReceiverPort() {
		return receiverPort;
	}
	public void setReceiverPort(int receiverPort) {
		this.receiverPort = receiverPort;
	}
	public ServerOperation getOperation() {
		return operation;
	}
	public void setOperation(ServerOperation operation) {
		this.operation = operation;
	}
	
	
}
