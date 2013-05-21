package server;


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.Enumeration;
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
import server.message.InteractionResponse;
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
	private ServerMetaData myServerData;
	private ServerHelper serverHelper;
	//Timer related objects
	private Timer timer;
	private TimerTask timerTask;
	private Random rand;
	//Interaction message
	private InteractionMessage periodicServerMessage;
	//Waiting list of server.
	private WaitingList waitingList;

	/**
	 * Create the Server object with specified ip and id.
	 * 
	 * @param ip
	 * @param id
	 */
	public Server(String ip,int id)
	{
		/**
		 * Read the server and parameter files and
		 * adjust necessary settings
		 */
		readServerFiles();
		//Set necessary variables
		this.setId(id);
		this.setServerIp(serverIp);
		this.setServerPort(serverPort);
		//Create the server helper
		serverHelper = new ServerHelper(serverPort);
		//Create the client and server list
		clientList = new Hashtable<String,ClientMetaData>();
		serverList = new Hashtable<String,ServerMetaData>();
		//Create the server list.
		waitingList = new WaitingList();
		//Start the server helper
		new Thread(serverHelper).start();
		/**
		 * Set the interaction message's attributes.
		 */
		myServerData = new ServerMetaData(serverPort, serverIp,capacity);
		//No need to send client list
		myServerData.setConnectedClient(null);
		
		periodicServerMessage = new InteractionMessage(serverIp,serverPort,
														Operation.DEFAULT,
														clientList,
														myServerData);
		//Set the message sender as server
		periodicServerMessage.setServerRole(true);
		periodicServerMessage.setOperation(Operation.HELLO);
		
		//Create the random objects
		rand = new Random();		
		/**
		 * Start the Server HELLO messages
		 */
		initialTimerTask();
	}
	/**
	 * Default constructor.
	 */
	public Server(){
		
	}
	public void readServerFiles(){
		try {
	        FileInputStream fstream = null;
	        serverIp = GetMachineName();
	        try {
	            fstream             = new FileInputStream(Constants.PARAMETER);
	            DataInputStream in  = new DataInputStream(fstream);
	            BufferedReader br   = new BufferedReader(new InputStreamReader(in));
	            String strLine;
	            /**
	             * Add parameters into parameter file
	             * and read here.
	             */
	            String[] splitted = new String[2];
	            while ((strLine = br.readLine()) != null) {
	                if(!strLine.equalsIgnoreCase("")){
	                    splitted = strLine.split(" ");
	                    String ip = splitted[0];
	                    int capacity = Integer.parseInt(splitted[1]);
	                    /**
	                     * Different than current server then create 
	                     * the Server meta data
	                     */
	                    if(!serverIp.equals(ip))
	                    {
	                    	//Initial connected client is empty
	                    	ServerMetaData serverMetaData = new ServerMetaData();
	                    	serverMetaData.setServerIp(ip);
	                    	serverMetaData.setCapacity(capacity);
	                    	serverList.put(ip, serverMetaData);	
	                    }
	                }
	            }
	            in.close();
	        } catch (IOException ex) {
	            System.err.println("could not read the parameter value!");
	        } finally {
	            try {
	                fstream.close();
	            } catch (IOException ex) {
	                System.err.println("could not finish IO operation!");
	            }
	        }
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Server.readServerFiles()");
		}
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
				doOperationProcess(msg);
				msg.doOperation(msg, myClient, assignedServerIp);
				
			}
		} catch (Exception e) {
			System.out.println("Server.update()");
			e.printStackTrace();
		}
	}
	public void doOperationProcess(Interactable msg)
	{
		try 
		{
			controlListTimerExpiration(); 
			Operation op = msg.getOperation();
			switch (op) 
			{
			//Add client into client list
			case ADD:
				responseAddOperation(msg);
				break;
			//Delete client	
			case DELETE:
				responseDeleteOperation(msg);
				break;
			//Receives HELLO messages
			case HELLO:
				responseHelloOperation(msg);
				break;
			//Return server summary
			case SUMMARY:
				responseSummaryOperation(msg);
				break;
			case WAITINGLIST:
				break;

			default:
				System.out.println("NULL OPERATION");
				break;
			}
		} 
		catch (Exception e) 
		{
			System.out.println("Server.doOperationProcess()");
		}
		
	}
	/**
	 * For each operation control the server and client lists.
	 * If any of them goes down then remove from current server
	 * data structures.
	 */
	public void controlListTimerExpiration()
	{
		try 
		{
			//Check server
			Set<String> set = serverList.keySet();
			Iterator<String> i = set.iterator();
			while (i.hasNext()) 
			{
				Time now = new Time();
				String sIp = (String) i.next();
				ServerMetaData sMeta = serverList.get(sIp);
				Time lastTransaction = sMeta.getLastTransaction();
				//If the time difference is greater than threshold remove
				if(now.timeDifference(lastTransaction) > Constants.TIME_THRESHOLD)
				{
					serverList.remove(sIp);
				}
				
			}
			
			//Check clients
			set = clientList.keySet();
			i = set.iterator();
			while (i.hasNext()) 
			{
				Time now = new Time();
				String cIp = (String) i.next();
				ClientMetaData cMeta = clientList.get(cIp);
				Time lastTransaction = cMeta.getLastTransaction();
				//If the time difference is greater than threshold remove
				if(now.timeDifference(lastTransaction) > Constants.TIME_THRESHOLD)
				{
					clientList.remove(cIp);
				}
				
			}
		} 
		catch (Exception e) 
		{
			// TODO: handle exception
			System.out.println("Server.controlListTimerExpiration()");
			e.printStackTrace();
		}
	}
	/**
	 * ADD operation is used by clients. After proposed 
	 * algorithm execution, client selects one of the 
	 * server and send an interaction request which keeps
	 * the ADD operation.
	 * 
	 * @param msg
	 */
	public void responseAddOperation(Interactable msg)
	{
		try 
		{
			String clientIp = msg.getSenderIpAddress();
			if(clientList.containsKey(clientIp))
			{
				clientList.remove(clientIp);
				clientList.put(clientIp, msg.getClientMetaData());
			}
			else
			{
				clientList.put(clientIp, msg.getClientMetaData());
			}
		} 
		catch (Exception e) 
		{
			// TODO: handle exception
			System.out.println("Server.responseAddOperation()");
			e.printStackTrace();
		}
	}
	/**
	 * Delete operation can be only used by server. Clients are allowed
	 * to request ADD operation.If any server request delete operation 
	 * from current server then  control if it is verified server. Then,
	 * control the client list if the parameter client exist.
	 * @param msg
	 */
	public void responseDeleteOperation(Interactable msg)
	{
		try 
		{
			String serverIp = msg.getSenderIpAddress();
			if(serverList.containsKey(serverIp))
			{
				/**
				 * Read the client meta data  and apply
				 * the operation
				 */
				ClientMetaData clientMetaData = msg.getClientMetaData();
				String clientIp = clientMetaData.getIpAddress();
				if(clientList.containsKey(clientIp))
				{
					clientList.remove(clientIp);
				}
			}

		} 
		catch (Exception e) 
		{
			// TODO: handle exception
			System.out.println("Server.responseDeleteOperation()");
			e.printStackTrace();
		}
		
	}
	/**
	 * HELLO message can come from both server and clients
	 * So, if it comes from server update the server  
	 */
	public void responseHelloOperation(Interactable msg)
	{
		try {
			boolean isServer = msg.isServer();
			/**
			 * If message sender is server.
			 */
			if(isServer)
			{
				/**
				 * Extract information update server list.
				 */
				String serverIp = msg.getSenderIpAddress();
				ServerMetaData serverMetaData = msg.getServerMetaData();
				if(serverList.containsKey(serverIp))
				{
					serverList.remove(serverIp);
					serverList.put(serverIp, serverMetaData);
				}
				else
				{
					/**
					 * Else put it into server list.
					 */
					serverList.put(serverIp, serverMetaData);
				}
			}
			/**
			 * Else it comes from client
			 */
			else
			{
				/**
				 * Control the client if it is connected to 
				 * current server.
				 */
				String connectedServer = msg.getConnectedServer();
				if(connectedServer.equals(serverIp))
				{
					String clientIp = msg.getSenderIpAddress();
					ClientMetaData clientMetaData = msg.getClientMetaData();
					if(clientList.containsKey(clientIp))
					{
						clientList.remove(clientIp);
						clientList.put(clientIp, clientMetaData);
					}
					else
					{
						clientList.put(clientIp, clientMetaData);
						
					}
				}
			}
		} 
		catch (Exception e) 
		{
			// TODO: handle exception
			System.out.println("Server.responseHelloOperation()");
			e.printStackTrace();
		}
	}
	public void responseSummaryOperation(Interactable msg)
	{
		try 
		{
			/**
			 * Generate the summary information
			 * via using the servers
			 */
			String reply = "Summary Of Servers";
			String clientIp = msg.getSenderIpAddress();
			int clientPort = msg.getSenderPort();
			//Generate the summary response
			InteractionResponse summResponse = new InteractionResponse(serverIp, serverPort, clientIp, clientPort, reply);
			//Set as this message is summary of server
			summResponse.setOperation(Operation.SUMMARY);
			//Set the server list
			summResponse.setServerList(serverList);
			//Connect the client and send the server list.
			connectToClientNode(clientIp, clientPort, summResponse);
		} 
		catch (Exception e) 
		{
			// TODO: handle exception
			System.out.println("Server.responseSummaryOperation()");
			e.printStackTrace();
		}
	}
	public boolean isMyClient(String cIp) 
	{

		try {
			Set<String> set = clientList.keySet();
			Iterator<String> i = set.iterator();
			while (i.hasNext()) 
			{
				String ip = (String) i.next();
				if (ip.equals(cIp))
					return true;
			}
			return false;
		} 
		catch (Exception e) 
		{
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}

	}
	public String findClientAssignedServer(String cIp)
	{
		try 
		{
			Set<String> set = serverList.keySet();
			Iterator<String> i = set.iterator();
			while (i.hasNext()) 
			{
				String ip = (String) i.next();
				if(ip.equals(cIp)) return ip ;	
			}
			return "";
		} catch (Exception e) 
		{
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
	public void connectToClientNode(String ip,int port,InteractionMessage m)
	{
		try 
		{
			try 
			{
				Socket socket = new Socket(ip,port);
				ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());
				toServer.writeObject(m);
				toServer.flush();
				socket.close();
			}
			catch (Exception e) 
			{
				// TODO: handle exception
				e.printStackTrace();
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	private String GetMachineName() 
	{
		String name = null;
		Enumeration<NetworkInterface> enet = null;
		try 
		{
			enet = NetworkInterface.getNetworkInterfaces();
		} 
		catch (SocketException e1) 
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		while (enet.hasMoreElements() && (name == null)) 
		{
			NetworkInterface net = enet.nextElement();

			try 
			{
				if (net.isLoopback())	continue;
			} 
			catch (SocketException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Enumeration<InetAddress> eaddr = net.getInetAddresses();
			while (eaddr.hasMoreElements()) 
			{
				InetAddress inet = eaddr.nextElement();
				if (inet.getCanonicalHostName().equalsIgnoreCase(inet.getHostAddress()) == false) 
				{
					name = inet.getCanonicalHostName();
					break;
				}
			}
		}

		return name;
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
