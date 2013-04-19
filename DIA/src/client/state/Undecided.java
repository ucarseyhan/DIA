package client.state;

import client.AssignmentRequest;
import client.Client;
import client.ControllerInformationMessage;
import client.InteractionRequest;
import client.InteractionResponse;
import client.PeriodicServerMessage;
/**
 * This class is used for representing the client 
 * UNDECIDED state. Each client which enters the system
 * start with UNDECIDED state. Necessary operation for each 
 * state is coded here.
 * 
 * @author ucar
 *
 */
public class Undecided implements State {
	//Declare variables.
	private Client client;
	
	//Default Constructor.
	public Undecided(){
		
	}
	//Specified Constructor
	public Undecided(Client c){
		this.client = c;
	}
	
	@Override
	/**
	 * When client issues an interaction with other
	 * clients. This method is executed in UNDECIDED
	 * state.
	 */
	public void requestInteraction() {
		try {
		} catch (Exception e) {
			System.out.println("Undecided state:requestInteraction() methods");
			e.printStackTrace();
		}
	}
	@Override
	public void requestAssignment() {
		try {
			//Send assignment request to controller.
			AssignmentRequest assgReq = new AssignmentRequest(client.getMyPort(), client.getMyIpAddress());
			//Send to controller
			client.connectToController(assgReq);
			//Change state to SERVER_ELECTION
			client.setState(client.getSERVERELECTION());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	@Override
	/**
	 * When client gets interaction response message from 
	 * one of the replicated server.
	 */
	public void getInteractionResponse(InteractionResponse intrResponse) {
		try {
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Undecided:getInteractionResponse()");
			e.printStackTrace();
		}
	}

	@Override
	/**
	 * This method is used for receiving periodic assigned
	 * server message.In this state there is no periodic 
	 * server message so this method is not implemented.
	 */
	public void getServerPeriodicMessage(PeriodicServerMessage msg) {
	}
	@Override
	/**
	 * Print the state information 
	 */
	public void printInfo() {
		System.out.println("Client is in UNDECIDED state");
	}
	@Override
	/**
	 * This method is executed when the client gets the controller
	 * information from nearest server.
	 */
	public void getControllerInfoMessage(ControllerInformationMessage cInf) {
		try {
			client.cancelTimerTask();
			String cIp = cInf.getControllerIPAddress();
			int cPort = cInf.getControllerPort();
			client.setControllerIpAddress(cIp);
			client.setControllerPort(cPort);
			//Controller information is ready.
			client.randomWaitRequestAssignment();
		} catch (Exception e) {
			System.out.println("Undecided:getControllerInfoMessage()");
			e.printStackTrace();
		}
	}
	@Override
	public void getInterActionRequest(InteractionRequest request) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void timeOutControl() {
		// TODO Auto-generated method stub
		
	}
	//================Getter and Setter methods===================
	public Client getClient() {
		return client;
	}
	public void setClient(Client client) {
		this.client = client;
	}



}
