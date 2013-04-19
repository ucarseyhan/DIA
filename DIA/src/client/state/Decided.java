package client.state;

import java.util.Random;

import client.Client;
import client.ClientMetaData;
import client.ControllerInformationMessage;
import client.InteractionRequest;
import client.InteractionResponse;
import client.PeriodicServerMessage;
/**
 * This class is used for representing the Client 
 * DECIDE state. The necessary operations which are 
 * taken is coded in this class.
 * 
 * @author ucar
 *
 */
public class Decided implements State {
	//Declare variables.
	private Client client;
	private TimeOut timeOut;
	private Thread thread;
	private Random rand;

	//Default constructor
	public Decided(){
		
	}
	//Specified Constructor
	public Decided(Client c){
		this.client = c;
		rand = new Random();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void getInteractionResponse(InteractionResponse intrResponse) {
		client.cancelTimerTask();
		thread.stop();
		//Update the assigned server.
		String ip = intrResponse.getSenderIpAddress();
		int port = intrResponse.getSenderPort();
		client.setAssignedServerIpAdress(ip);
		client.setAssignedServerPort(port);
		//Update current state.
		client.getCURRENTSTATE().timeOutControl();
	}
	@Override
	/**
	 * In DECIDED state whenever the client issued an interaction it relays
	 * this data to assigned server by only giving the client id as input 
	 * parameter.
	 */
	public void requestInteraction() {
		try {
			try {
				//Send interaction message to controller
				//Select random client
				int randomClient = rand.nextInt(client.getClientList().size());
				ClientMetaData rndClient = client.getClientList().get(randomClient);
				//Create interaction request
				InteractionRequest intRequest = new InteractionRequest(client.getMyIpAddress(),
																	   client.getMyPort(), 
																	   rndClient.getIpAddress(),
																	   rndClient.getPort(), 
																	   "Request Interaction");
				//Send to controller
				client.connectToAssignedServer(intRequest);
				client.getCURRENTSTATE().timeOutControl();
				
			} catch (Exception e) {
				System.out.println("Undecided state:requestInteraction() methods");
				e.printStackTrace();
			}
		} catch (Exception e) {
			System.out.println("Decided:requestInteraction()");
		}
	}
	@Override
	public void timeOutControl() {
		int r = rand.nextInt(client.getClientList().size());
		timeOut = null;
		timeOut = new TimeOut(r*1000, "Timeout", client, client.getUNDECIDED());
		thread = new Thread(timeOut);
		thread.start();
	}
	@Override
	public void getServerPeriodicMessage(PeriodicServerMessage msg) {
		try {
			client.ServerHelloMessage(msg);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	@Override
	public void getInterActionRequest(InteractionRequest request) {
		try {
			System.out.println("Client:"+client.getMyIpAddress()+" get interaction request from:"+request.getSenderIpAddress());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	@Override
	public void printInfo() {
	}
	@Override
	public void getControllerInfoMessage(ControllerInformationMessage cInf) {
		// TODO Auto-generated method stub
	}
	
	//==========Getter and Setter Method===================
	public Client getClient() {
		return client;
	}
	public void setClient(Client client) {
		this.client = client;
	}
	@Override
	public void requestAssignment() {
		// TODO Auto-generated method stub
		
	}


}
