package server;


import java.io.ObjectOutputStream;
import java.net.*;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
/**
 * Server class is sued for creating the server object in simulation.
 * It has an internal thread for listening the central server service.
 * By using the server helper object the server object gets the message
 * when it is dedicated to client.
 * 
 * @author seyhan 
 *
 */
public class Server implements Observer{
	/**
	 * Declare variable.
	 */
	private int id = 0;
	private String serverIp = "";
	private int serverPort = 4000;
	private String controllerIp = "";
	private int controllerPort = 4000;
	private MessageBox messageBox;
	private Hashtable<String, ClientMetaData> clientList;
	private Hashtable<String, ServerMetaData> serverList;
	private boolean coontroller = false;
	private ServerHelper serverHelper;
	private boolean controller = false;
	private Timer timer;
	private TimerTask timerTask;
	private Random rand;
	private InteractionMessage periodicServerMessage;
	/**
	 * Specified constructor.
	 * @param ip
	 * @param port
	 * @param centralPort
	 * @param recPort
	 * @param currentLoad
	 * @param id
	 */
	public Server(String ip,int port,int centralPort,int recPort,int currentLoad,int id){
		this.id = id;
		this.serverIp = ip;
		this.serverPort = port;
		serverHelper = new ServerHelper(serverPort);
		clientList = new Hashtable<String,ClientMetaData>();
		//readClient text file
		serverList = new Hashtable<String,ServerMetaData>();
		//read server text file.
		new Thread(serverHelper).start();
		periodicServerMessage = new InteractionMessage();
		periodicServerMessage.setSenderIpAddress(serverIp);
		periodicServerMessage.setSenderPort(serverPort);
		periodicServerMessage.setOperation(ServerOperation.DEFAULT);
		rand = new Random();
	}
	/**
	 * Default constructor.
	 */
	public Server(){
		
	}
	public void initialTimerTask(){
		try {
			//First state get controller info.
			timerTask = new TimerTask() {
				@Override
				public void run() {
					if(!clientList.isEmpty()) sendPeriodicHello();
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
	public void sendPeriodicHello(){
		try {
			Set<String> set = clientList.keySet();
			Iterator<String> i = set.iterator();
			while (i.hasNext()) {
				String ip = (String) i.next();
				int port = clientList.get(ip).getPort();
				connectToClientNode(ip, port, periodicServerMessage);
			}
		
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	@Override
	public void update(Observable o, Object arg1) {
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
					ControllerInformationMessage cM = (ControllerInformationMessage)(msg);
					//Client request controller info.
					int port = cM.getClientPort();
					String ip = cM.getClientIPAddress();
					cM.setControllerIPAddress(controllerIp);
					cM.setClientPort(controllerPort);
					connectToClientNode(ip,port,cM);
				}
				else if(msg instanceof InteractionRequest){
					//One of the assigned clients request interaction
					InteractionRequest intR = (InteractionRequest)(msg);
					//Decode message.
					String targetIp = intR.getTargetClientIp();
					String clientIp = intR.getMyIp();
					int clientPort = intR.getSenderPort();
					int port = intR.getSenderPort();
					//Control client IP if you are  target server.
					if(isMyClient(targetIp)){
						doMyClientOperation(targetIp, port, intR, clientIp, clientPort);
					}else{
						//If it is not my client first contact with assigned server.
						//Send request directly to it
						String assignedServerIp = findClientAssignedServer(clientIp);
						connectToServer(assignedServerIp,port,intR);
					}
				}else if(msg instanceof InteractionResponse){
					//Send the coming response to your directly connected client.
					InteractionResponse intrResponse = (InteractionResponse)(msg);
					String ip = intrResponse.getReceiverIpAddress();
					int port = intrResponse.getReceiverPort();
					connectToClientNode(ip, port, intrResponse);	
				}
				
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	public void doMyClientOperation(String targetIp,int port,InteractionRequest intR,String clientIp,int clientPort){
		try {
			//It is my client send message to client.
			connectToClientNode(targetIp, port, intR);
			//Construct Interaction response.
			String reply = "Your message has be delivered";
			InteractionResponse response = new InteractionResponse(
					this.getServerIp(), this.getServerPort(),
					clientIp, clientPort, reply);
			//Send response to my client node again.
			connectToClientNode(clientIp, clientPort, response);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	public boolean isMyClient(String cIp) {

		try {
			Set<String> set = clientList.keySet();
			Iterator<String> i = set.iterator();
			while (i.hasNext()) {
				String ip = (String) i.next();
				if (ip.equals(cIp))
					return true;
			}
			return false;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}

	}
	public String findClientAssignedServer(String cIp){
		try {
			Set<String> set = serverList.keySet();
			Iterator<String> i = set.iterator();
			while (i.hasNext()) {
				String ip = (String) i.next();
				if(ip.equals(cIp)) return ip ;	
			}
			return "";
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return "";
		}
	}
	/**
	 * After the central server select the server server 
	 * connect to client and say that it is server of the 
	 * client.
	 */
	public void connectToClientNode(String ip,int port,Message m){
		try {
			try {
				//Socket socket = new Socket("172.23.121.38",this.clientPort);
				Socket socket = new Socket(ip,port);
				ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());
				toServer.writeObject(m);
				toServer.flush();
				socket.close();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void connectToServer(String assignedServerIp,int port,Message m){
		try {
			System.out.println(this.id+" connecting to server:"+assignedServerIp);
			Socket socket = new Socket(assignedServerIp,port);
			ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());
			toServer.writeObject(m);
			toServer.flush();
			socket.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	//////////////////////////GETTER and SETTER METHODS///////////////////////////////
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getServerIp() {
		return serverIp;
	}
	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}
	public int getServerPort() {
		return serverPort;
	}
	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}
	public MessageBox getMessageBox() {
		return messageBox;
	}
	public void setMessageBox(MessageBox messageBox) {
		this.messageBox = messageBox;
	}
	public Hashtable<String, ClientMetaData> getClientList() {
		return clientList;
	}
	public void setClientList(Hashtable<String, ClientMetaData> clientList) {
		this.clientList = clientList;
	}
	public boolean isCoontroller() {
		return coontroller;
	}
	public void setCoontroller(boolean coontroller) {
		this.coontroller = coontroller;
	}
	public ServerHelper getServerHelper() {
		return serverHelper;
	}
	public void setServerHelper(ServerHelper serverHelper) {
		this.serverHelper = serverHelper;
	}
	public String getControllerIp() {
		return controllerIp;
	}
	public void setControllerIp(String controllerIp) {
		this.controllerIp = controllerIp;
	}
	public int getControllerPort() {
		return controllerPort;
	}
	public void setControllerPort(int controllerPort) {
		this.controllerPort = controllerPort;
	}
	public Hashtable<String, ServerMetaData> getServerList() {
		return serverList;
	}
	public void setServerList(Hashtable<String, ServerMetaData> serverList) {
		this.serverList = serverList;
	}
	public boolean isController() {
		return controller;
	}
	public void setController(boolean controller) {
		this.controller = controller;
	}
	public Timer getTimer() {
		return timer;
	}
	public void setTimer(Timer timer) {
		this.timer = timer;
	}
	public TimerTask getTimerTask() {
		return timerTask;
	}
	public void setTimerTask(TimerTask timerTask) {
		this.timerTask = timerTask;
	}
	public Random getRand() {
		return rand;
	}
	public void setRand(Random rand) {
		this.rand = rand;
	}
	public InteractionMessage getPeriodicServerMessage() {
		return periodicServerMessage;
	}
	public void setPeriodicServerMessage(InteractionMessage periodicServerMessage) {
		this.periodicServerMessage = periodicServerMessage;
	}
	
	

}
