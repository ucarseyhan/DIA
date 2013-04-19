package client;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import client.state.State;

/**
 * This class is used for representing the client side.
 * @author ucar
 * 
 * Test Github
 *
 */
public class Client implements Observer {
	//Declare variables
	private State UNDECIDED;
	private State DECIDED;
	private State SERVERELECTION;
	private State CURRENTSTATE	= UNDECIDED;
	private ArrayList<ClientMetaData> clientList;
	private String controllerIpAddress = "";
	private int controllerPort = 4000;
	private String nearestIpAddress = "";
	private int nearestPort = 4000;
	private String myIpAddress = "";
	private int myPort = 4000;
	private String assignedServerIpAdress = "";
	private int assignedServerPort = 4000;
	private Timer timer;
	private TimerTask timerTask;
	private Random rand;
	private MessageBox messageBox;
	private int myId = -1;
	
	//Default constructor
	public Client(){
		//Read text file and construct server list.(for nearest)
		//Read text file construct client list.
		clientList = new ArrayList<ClientMetaData>();
		rand = new Random();
		messageBox = new MessageBox();
		timer  = new Timer();
		initialTimerTask();

	}
	/**
	 * In the first state client starts to get controller 
	 * information till one of the server response.
	 */
	public void initialTimerTask(){
		try {
			//First state get controller info.
			timerTask = new TimerTask() {
				@Override
				public void run() {
					sendControllerInfoRequest();
				}
			};
			int r = rand.nextInt(clientList.size());
			//Start the process.
			timer.schedule(timerTask, r * 500, r*1000);
		} catch (Exception e) {
			System.out.println("Client:initialTimerTask() method");
			e.printStackTrace();
		}
	}
	@Override
	/**
	 * When client gets this message it control the message via this 
	 * method.
	 */
	public void update(Observable o, Object obj) {
		/**
		 * Check the coming message do required operation.
		 */
		try {
			if(o instanceof MessageBox){
				MessageBox mBox = (MessageBox)o;
				this.messageBox = mBox;
				/**
				 * Message is in the stack.So get it out and do
				 * the process.
				 */
				Message msg = messageBox.pop();
				if(msg instanceof ControllerInformationMessage){
					cancelTimerTask();
					ControllerInformationMessage cInf = (ControllerInformationMessage)(msg);
					CURRENTSTATE.getControllerInfoMessage(cInf);
				}else if(msg instanceof InteractionMessage){
					if(msg instanceof InteractionResponse){
						//Client get interaction response.
						InteractionResponse intMsg = (InteractionResponse)(msg);
						CURRENTSTATE.getInteractionResponse(intMsg);
//						cancelTimerTask();
//						randomWaitRequestInteraction();
						int r = rand.nextInt(clientList.size());
						//Start the process.
						timer.schedule(timerTask, r * 500, r*1000);
					}
				}else if(msg instanceof PeriodicServerMessage){
					PeriodicServerMessage pMsg = (PeriodicServerMessage)(msg);
					CURRENTSTATE.getServerPeriodicMessage(pMsg);
				}else if(msg instanceof InteractionRequest){
					//InteractionRequest intRequest = (InteractionRequest)(msg);
					
				}
				
			}
			
		} catch (Exception e) {
			System.out.println("Client:update()");
			e.printStackTrace();
		}
	}
	public void randomWaitRequestAssignment(){
		try {
			int r = rand.nextInt(10);
			Thread.sleep(r * 1000);
			timerTask = new TimerTask() {
				@Override
				public void run() {
					CURRENTSTATE.requestAssignment();
				}
			};
			r = rand.nextInt(10);
			//Start the process.
			timer.schedule(timerTask, r * 500, r * 1000);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	public void randomWaitRequestInteraction(){
		try {
			int r = rand.nextInt(clientList.size());
			Thread.sleep(r * 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			timerTask = new TimerTask() {
				@Override
				public void run() {
					CURRENTSTATE.requestInteraction();
				}
			};
			int r = rand.nextInt(clientList.size());
			//Start the process.
			timer.schedule(timerTask, r * 500, r * 1000);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	/**
	 * In this method the client send controller request
	 * information from the nearest server.There is no chance
	 * of client to know the current controller.
	 */
	public void sendControllerInfoRequest() {
		try {
			//Create the message.
			ControllerInformationMessage controlRequest = new ControllerInformationMessage(getMyIpAddress(),
																	 getMyPort(),
																	 getNearestIpAddress(),
																	 getNearestPort(),
																	 getControllerIpAddress(),
																	 getControllerPort());
			//Find nearest server.
			findNearestServer();
			//Connect to nearest server and request controller information
			connectToNearestServer(controlRequest);
		} catch (Exception e) {
			System.out.println("Client:getControllerResponse() method");
			e.printStackTrace();
		}
	}
	/**
	 * This method is executed when client needs controller information from
	 * nearest server.By using this method client learns the current controller.
	 * @param m
	 */
	public void connectToNearestServer(Message m) {
		try {
			Socket socket = new Socket(getNearestIpAddress(),getNearestPort());
			ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());
			toServer.writeObject(m);
			toServer.flush();
		} catch (Exception e) {
			System.out.println("Client:connectToNearestServer()");
			e.printStackTrace();
		}
	}
	/**
	 * In the first phase of the client assignment
	 * client send message to controller directly.
	 */
	public void connectToController(Message m) {
		try {
			Socket socket = new Socket(getControllerIpAddress(),getControllerPort());
			ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());
			toServer.writeObject(m);
			toServer.flush();
		} catch (Exception e) {
			System.out.println("Client:connectToNearestServer()");
			e.printStackTrace();
		}
	}
	/**
	 * When client gets response from one of the replicated
	 * server it updates the assigned server information and
	 * from now on it sends its message to directly assigned server.
	 */
	public void connectToAssignedServer(Message m) {
		try {
			Socket socket = new Socket(getAssignedServerIpAdress(),getAssignedServerPort());
			ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());
			toServer.writeObject(m);
			toServer.flush();
		} catch (Exception e) {
			System.out.println("Client:connectToNearestServer()");
			e.printStackTrace();
		}
	}
	public void sendInteractionRequestToAssignedServer(){
		try {
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Client:sendInteractionRequestToAssignedServer()");
			e.printStackTrace();
		}
	}
	/**
	 * After some process client needs to change state.
	 * When client issued state change this method is 
	 * executed.
	 * @param s
	 */
	public void setState(State s){
		try {
			this.CURRENTSTATE = s;
		} catch (Exception e) {
			System.out.println("Client:setState()");
			e.printStackTrace();
		}
	}	
	/**
	 * This method is executed when client gets controller 
	 * information from nearest server. By using controller
	 * information client sends it request interaction to
	 * controller.
	 */
	public void getControllerInformation(){
		try {
			/**
			 * Use controller information and
			 * do the process.
			 * Update controller info.
			 * Send interaction request and change state.
			 */
		} catch (Exception e) {
			System.out.println("Client:getControllerInformation()");
			e.printStackTrace();
		}
	}
	/**
	 * In UNDECIDED state client send controller request
	 * to one of the nearest server. Search the nearest server
	 * send request by controlling timer.
	 * @return
	 */
	public void sendControllerInformationRequest(){
		try {
			
		} catch (Exception e) {
			System.out.println("Client:sendControllerInformationRequest()");
			e.printStackTrace();
		}
	}
	/**
	 * This method is executed when the client gets
	 * the SERVERHELLO message. It updates the local 
	 * information and change state to DECIDED.
	 * 
	 */
	public void ServerHelloMessage(PeriodicServerMessage msg){
		try {
			String ip = msg.getServerIp();
			if(!ip.equals(assignedServerIpAdress)){
				assignedServerIpAdress = ip;
			}
		} catch (Exception e) {
			System.out.println("Client:getPeriodicHelloMessage()");
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * @return
	 */
	public void findNearestServer(){
		try {
			/**
			 * If nearest server is not determined  search 
			 * the server list and update nearest server information.
			 */
		} catch (Exception e) {
			System.out.println("Client:findNearestServer()");
			e.printStackTrace();
		}
	}
	/**
	 * By using the coming message the timer task is cancelled.
	 * So when the clients wants to cancel the timer task the 
	 * cancelTimerTask is executed.
	 */
	public void cancelTimerTask(){
		try {
			timer.cancel();
			timer.purge();
		} catch (Exception e) {
			System.out.println("Client:cancelTimerTask()");
			e.printStackTrace();
		}
	}
	//====================Getter and Setter Method======================
	public State getUNDECIDED() {
		return UNDECIDED;
	}
	public void setUNDECIDED(State uNDECIDED) {
		UNDECIDED = uNDECIDED;
	}
	public State getDECIDED() {
		return DECIDED;
	}
	public void setDECIDED(State dECIDED) {
		DECIDED = dECIDED;
	}
	public State getSERVERELECTION() {
		return SERVERELECTION;
	}
	public void setSERVERELECTION(State sERVERELECTION) {
		SERVERELECTION = sERVERELECTION;
	}
	public State getCURRENTSTATE() {
		return CURRENTSTATE;
	}
	public void setCURRENTSTATE(State cURRENTSTATE) {
		CURRENTSTATE = cURRENTSTATE;
	}
	public String getControllerIpAddress() {
		return controllerIpAddress;
	}
	public void setControllerIpAddress(String controllerIpAddress) {
		this.controllerIpAddress = controllerIpAddress;
	}
	public String getMyIpAddress() {
		return myIpAddress;
	}
	public void setMyIpAddress(String myIpAddress) {
		this.myIpAddress = myIpAddress;
	}
	public int getMyId() {
		return myId;
	}
	public void setMyId(int myId) {
		this.myId = myId;
	}
	public int getControllerPort() {
		return controllerPort;
	}
	public void setControllerPort(int controllerPort) {
		this.controllerPort = controllerPort;
	}
	public ArrayList<ClientMetaData> getClientList() {
		return clientList;
	}
	public void setClientList(ArrayList<ClientMetaData> clientList) {
		this.clientList = clientList;
	}
	public int getMyPort() {
		return myPort;
	}
	public void setMyPort(int myPort) {
		this.myPort = myPort;
	}
	public String getAssignedServerIpAdress() {
		return assignedServerIpAdress;
	}
	public void setAssignedServerIpAdress(String assignedServerIpAdress) {
		this.assignedServerIpAdress = assignedServerIpAdress;
	}
	public int getAssignedServerPort() {
		return assignedServerPort;
	}
	public void setAssignedServerPort(int assignedServerPort) {
		this.assignedServerPort = assignedServerPort;
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
	
	
	
	
	
	
	
	

}
