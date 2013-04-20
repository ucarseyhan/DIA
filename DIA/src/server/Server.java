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

import server.message.Interactable;
import server.message.InteractionMessage;
import server.message.MessageBox;
/**
 * Server class is sued for creating the server object in simulation.
 * It has an internal thread for listening the central server service.
 * By using the server helper object the server object gets the message
 * when it is dedicated to client.
 * 
 * @author seyhan 
 *
 */
public class Server implements Observer
{
	/**
	 * Declare variable.
	 */
	//Server Information
	private int id = 0;
	private String serverIp = "";
	private int serverPort = 4000;
	private int capacity = 0;
	//Server data structure to keep the clients
	private MessageBox messageBox;
	private Hashtable<String, ClientMetaData> clientList;
	private Hashtable<String, ServerMetaData> serverList;
	private ServerHelper serverHelper;
	//Timer related objects
	private Timer timer;
	private TimerTask timerTask;
	private Random rand;
	//Interaction message
	private InteractionMessage periodicServerMessage;

	/**
	 * Create the Server object with specified ip and id.
	 * 
	 * @param ip
	 * @param id
	 */
	public Server(String ip,int id)
	{
		//Set necessary variables
		this.setId(id);
		this.setServerIp(serverIp);
		this.setServerPort(serverPort);
		//Create the server helper
		serverHelper = new ServerHelper(serverPort);
		//Create the client and server list
		clientList = new Hashtable<String,ClientMetaData>();
		serverList = new Hashtable<String,ServerMetaData>();
		//Start the server helper
		new Thread(serverHelper).start();
		/**
		 * Set the interaction message's attributes.
		 */
		
		periodicServerMessage = new InteractionMessage(serverIp,serverPort,
														ServerOperation.DEFAULT,
														clientList,
														serverList); 
		
		//Create the random objects
		rand = new Random();
	}
	/**
	 * Default constructor.
	 */
	public Server(){
		
	}
	/**
	 * Create the initialTimer Task
	 */
	public void initialTimerTask(){
		try {
			/**
			 * Periodically send HELLO messages
			 */
			timerTask = new TimerTask() {
				@Override
				public void run() {
					sendPeriodicHello();
				}
			};
			int random = rand.nextInt(clientList.size()+serverList.size());
			//Start the process.
			timer.schedule(timerTask, random * 500, random * 1000);
		} catch (Exception e) {
			System.out.println("Client:initialTimerTask() method");
			e.printStackTrace();
		}
	}
	/**
	 * Send periodic HELLO messages so others (clients and servers)
	 * can be aware of the server existence.
	 * 
	 */
	public void sendPeriodicHello(){
		try {
			/**
			 * First share the HELLO messages with servers.
			 */
			Set<String> set = serverList.keySet();
			Iterator<String> i = set.iterator();
			while (i.hasNext()) {
				String ip = (String) i.next();
				int port = serverList.get(ip).getPort();
				connectToClientNode(ip, port, periodicServerMessage);
			}
			/**
			 * Second share the HELLO messages with connected clients.
			 * No need to share the connected client information with
			 * clients
			 */
			periodicServerMessage.setClientList(null);
			set = clientList.keySet();
			i = set.iterator();
			while (i.hasNext()) {
				String ip = (String) i.next();
				int port = clientList.get(ip).getPort();
				connectToClientNode(ip, port, periodicServerMessage);
			}
		
		} catch (Exception e) {
			System.out.println("Server.sendPeriodicHello()");
			e.printStackTrace();
		}
	}
	@Override
	public void update(Observable o, Object arg) {
		try {
			if(o instanceof MessageBox){
				MessageBox mBox = (MessageBox)o;
				this.messageBox = mBox;
				/**
				 * Message is in the stack.So get it out and do
				 * the process.
				 */
				Interactable msg = messageBox.pop();
				String targetIp = msg.getReceiverIpAddress();
				String clientIp = msg.getSenderIpAddress();
				boolean myClient = isMyClient(targetIp);
				String assignedServerIp = findClientAssignedServer(clientIp);
				msg.doOperation(msg, myClient, assignedServerIp);
				
			}
		} catch (Exception e) {
			System.out.println("Server.update()");
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
	public void connectToClientNode(String ip,int port,InteractionMessage m){
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

	//////////////////////////GETTER and SETTER METHODS///////////////////////////////
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public int getCapacity() {
		return capacity;
	}
	public void setCapacity(int capacity) {
		this.capacity = capacity;
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

	public ServerHelper getServerHelper() {
		return serverHelper;
	}
	public void setServerHelper(ServerHelper serverHelper) {
		this.serverHelper = serverHelper;
	}
	public Hashtable<String, ServerMetaData> getServerList() {
		return serverList;
	}
	public void setServerList(Hashtable<String, ServerMetaData> serverList) {
		this.serverList = serverList;
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
