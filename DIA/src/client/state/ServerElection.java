package client.state;

import client.AssignmentRequest;
import client.Client;
import client.ControllerInformationMessage;
import client.InteractionRequest;
import client.InteractionResponse;
import client.PeriodicServerMessage;
/**
 * The ServerElection class is used for the state 
 * SERVER_ELECTION.In this class server has already
 * send message to Controller and wait for one of the
 * replicated server reply. 
 * 
 * @author ucar
 *
 */
public class ServerElection implements State {
	//Declare variables.
	private Client client;
	//Default constructor
	public ServerElection(){
		
	}
	//Specified Constructor
	public ServerElection(Client c){
		this.client = c;
	}
	@Override
	/**
	 * This method is useless for the SERVERELECTION
	 * state.Because client has already send the request
	 * interaction to the controller and wait for one of the
	 * replicated server message.
	 */
	public void requestInteraction() {
		try {
//			//Send interaction message to controller
//			//Select random client
//			int randomClient = rand.nextInt(client.getClientList().size());
//			ClientMetaData rndClient = client.getClientList().get(randomClient);
//			//Create interaction request
//			InteractionRequest intRequest = new InteractionRequest(
//					client.getMyIpAddress(), client.getMyPort(),
//					rndClient.getIpAddress(), rndClient.getPort(),
//					"Request Interaction");
//			//Send to controller
//			client.connectToController(intRequest);
//			//Change state to SERVER_ELECTION
//			client.setState(client.getSERVERELECTION());
		} catch (Exception e) {
			System.out.println("Undecided state:requestInteraction() methods");
			e.printStackTrace();
		}
	}
	@Override
	/**
	 * Try until one of the replicated server
	 * send either hello message or response for your 
	 * assignment request.
	 */
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
	 * When client gets interaction response this method is
	 * going to work.
	 */
	public void getInteractionResponse(InteractionResponse intrResponse) {
		// TODO Auto-generated method stub
		client.cancelTimerTask();
		//Update the assigned server.
		String ip = intrResponse.getSenderIpAddress();
		int port = intrResponse.getSenderPort();
		client.setAssignedServerIpAdress(ip);
		client.setAssignedServerPort(port);
		//Update current state.
		client.setState(client.getDECIDED());
		client.randomWaitRequestInteraction();
		client.getCURRENTSTATE().timeOutControl();
	}
	@Override

	public void getServerPeriodicMessage(PeriodicServerMessage msg) {
		try {
		} catch (Exception e) {
			System.out.println("ServerElection:getServerPeriodicMessage()");
			e.printStackTrace();
		}
	}
	@Override
	public void getControllerInfoMessage(ControllerInformationMessage cInf) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void printInfo() {
		System.out.println("Client is SERVERELECTION state");
	}
	@Override
	public void timeOutControl() {
	}
	//====================Getter and Setter Method===========

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}
	@Override
	public void getInterActionRequest(InteractionRequest request) {
		// TODO Auto-generated method stub
		
	}
}
