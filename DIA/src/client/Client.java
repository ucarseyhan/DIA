package client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import messages.ClientMetaData;
import messages.Constants;
import messages.InitialMessage;
import messages.Interactable;
import messages.InteractionMessage;
import messages.InteractionRequest;
import messages.InteractionResponse;
import messages.Logger;
import messages.MessageBox;
import messages.Operation;
import messages.ServerMetaData;
import messages.Time;


/**
 * Client class is used for representing the 
 * client in the DIA simulation.
 * 
 */

/**
 * @author seyhan
 *
 */
public class Client implements Observer 
{
	private ConcurrentHashMap<String, ClientMetaData> clientList;
	private ConcurrentHashMap<String, ServerMetaData> serverList;
	private Hashtable<String, Long> serverDelay;
	private Timer timer;
	private TimerTask timerTask;
	private TimerTask clientBehaviourTask;
	private MessageBox messageBox;
	private ClientHelper clientHelper;
	private String clientIPAddress = " ";
	private int clientPort = Constants.PORT;
	private int totalClient = 0;
	private int totalInteraction = 1;
	private int max = 0; 
	private int min = 0;
	private int latencyReceive = 0;
	private boolean joinRequestSend = false;
	private boolean informInitialLatency = false;
	private String assignedServerIpAdress = " ";
	//Interaction message
	private InteractionMessage periodicClientMessage;
	private ClientMetaData myClientData;

	
	//Default constructor
	public Client()
	{
		/*
		 * Clean the results file
		 */
		Logger.print("Client start");
		File f = new File(Constants.RESULTFILE);
		if(f.exists())
		{
			f.delete();
		}
		
		//Create necessary data structure
		clientList = new ConcurrentHashMap<String, ClientMetaData>();
		serverList = new ConcurrentHashMap<String, ServerMetaData>();
		serverDelay = new Hashtable<String, Long>();
		messageBox = new MessageBox();
		messageBox.addObserver(this);
		/*
		 * Start to listen
		 */
		clientHelper = new ClientHelper(messageBox);
		new Thread(clientHelper).start();
		/*
		 * Read text files and fill the servers and 
		 * existing clients
		 */
		readServerFiles();
		readClientFiles();
		readParameterFiles();
		computeInitialLatency();
		//Create my  client data 
		myClientData = new ClientMetaData(clientPort, clientIPAddress,assignedServerIpAdress);
		myClientData.setClientServerLatency(serverDelay);
		
		periodicClientMessage = new InteractionMessage(clientIPAddress,
				clientPort, Operation.HELLO, myClientData);
		

		timer  = new Timer();
		//sendJoinRequest();
		initialTimerTask();
		initialClientBehaviourTask();

	}
	//=============================================================================================
	/**
	 * Read the server file and be aware of the existing
	 * servers
	 */
	public void readServerFiles()
	{
		try 
		{
	        FileInputStream fstream = null;
	        clientIPAddress = GetMachineName();
	        try 
	        {
	            fstream             = new FileInputStream(Constants.SERVERS);
	            DataInputStream in  = new DataInputStream(fstream);
	            BufferedReader br   = new BufferedReader(new InputStreamReader(in));
	            String strLine;
	            /**
	             * Add parameters into parameter file
	             * and read here.
	             */
	            while ((strLine = br.readLine()) != null) 
	            {
	                if(!strLine.equalsIgnoreCase(""))
	                {
	                	String ip = strLine;
	                	if(!serverList.containsKey(ip))
	                	{
							// Initial connected client is empty
							ServerMetaData serverMetaData = new ServerMetaData();
							serverMetaData.setServerIp(ip);
							serverList.put(ip, serverMetaData);
	                	}
	                }
	            }
	            in.close();
	        } 
	        catch (IOException ex) 
	        {
	            System.err.println("could not read the parameter value!");
	        } 
	        finally 
	        {
	            try 
	            {
	                fstream.close();
	            } 
	            catch (IOException ex) 
	            {
	                System.err.println("could not finish IO operation!");
	            }
	        }
		} 
		catch (Exception e) 
		{
			// TODO: handle exception
			System.out.println("Server.readServerFiles()");
			e.printStackTrace();
		}
	}
	
	/**
	 * Read the server file and be aware of the existing
	 * servers
	 */
	public void readClientFiles()
	{
		try 
		{
	        FileInputStream fstream = null;
	        try 
	        {
	            fstream             = new FileInputStream(Constants.CLIENTS);
	            DataInputStream in  = new DataInputStream(fstream);
	            BufferedReader br   = new BufferedReader(new InputStreamReader(in));
	            String strLine;
	            while ((strLine = br.readLine()) != null) 
	            {
	                if(!strLine.equalsIgnoreCase(""))
	                {
	                    String ip = strLine;
	                    if(!ip.equalsIgnoreCase(clientIPAddress))
	                    {
							// Initial connected client is empty
							ClientMetaData clientMetaData = new ClientMetaData();
							clientList.put(ip, clientMetaData);
	                    }

	                }
	            }
	            in.close();
	        } 
	        catch (IOException ex) 
	        {
	            System.err.println("could not read the parameter value!");
	        } 
	        finally 
	        {
	            try 
	            {
	                fstream.close();
	            } 
	            catch (IOException ex) 
	            {
	                System.err.println("could not finish IO operation!");
	            }
	        }
		} 
		catch (Exception e) 
		{
			// TODO: handle exception
			System.out.println("Client.readClientFiles()");
			e.printStackTrace();
		}
	}
	
	
	//=====================================================================================================
	/**
	 * Read the parameter file. The parameter list is;
	 * 0-server capacity
	 * 1-rjl(1 active, 0 disabled)
	 * 2-rjlThreshold
	 * 3-QoSThreshold
	 * 4-totalClient
	 * 5-write log enabled(1 active 0 disabled)
	 * 6-max
	 * 7-min
	 */
	public void readParameterFiles()
	{
		try 
		{
	        FileInputStream fstream = null;
	        try 
	        {
	            fstream             = new FileInputStream(Constants.PARAMETER);
	            DataInputStream in  = new DataInputStream(fstream);
	            BufferedReader br   = new BufferedReader(new InputStreamReader(in));
	            String strLine;
	            /**
	             * Add parameters into parameter file
	             * and read here.
	             */
	            String[] splitted = new String[8];
	            while ((strLine = br.readLine()) != null) 
	            {
	                if(!strLine.equalsIgnoreCase(""))
	                {
	                    splitted = strLine.split(" ");
	                    int capacity = Integer.parseInt(splitted[0]);
	                    int totalClient = Integer.parseInt(splitted[4]);
	                    int log = Integer.parseInt(splitted[5]);
	                    min = Integer.parseInt(splitted[6]);
	                    max = Integer.parseInt(splitted[7]);
	                    if(log == 1) Constants.LOG = true;
                    	this.totalClient = totalClient;
                    	//Set the server capacity
                    	for (String sIP : serverList.keySet()) 
                    	{
                    		serverList.get(sIP).setCapacity(capacity);
						}
	                }
	            }
	            in.close();
	        } 
	        catch (IOException ex) 
	        {
	            System.err.println("could not read the parameter value!");
	        } 
	        finally 
	        {
	            try 
	            {
	                fstream.close();
	            } 
	            catch (IOException ex) 
	            {
	                System.err.println("could not finish IO operation!");
	            }
	        }
		} 
		catch (Exception e) 
		{
			// TODO: handle exception
			System.out.println("Server.readServerFiles()");
			e.printStackTrace();
		}
	}
	//================================================================================================
	/**
	 * Get host name of the machine.
	 * 
	 * @return
	 */
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
	//===============================================================================================
	/**
	 * In initial phase servers needs the client-server latency.
	 * For that reason each client contacts with the existing 
	 * servers and compute the delay between each other. 
	 * 
	 */
	public void computeInitialLatency()
	{
		try 
		{
			for(String sIP : serverList.keySet())
			{
				Socket socket = new Socket(sIP,Constants.PORT);
				ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());
				InitialMessage initialMessage = new InitialMessage(clientIPAddress, sIP);
				toServer.writeObject(initialMessage);
				toServer.flush();
				Logger.print("ComputeInitialLatency Server:"+sIP+" Client:"+clientIPAddress);
			}
			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	//=========================================================================================================
	/**
	 * After determination of client server latency pair
	 * client inform each server about the latency so servers
	 * can use the delay to perform assignment operation.
	 */
	public void informInitialLatency()
	{
		try 
		{
			for(String sIP : serverList.keySet())
			{
				Socket socket = new Socket(sIP,Constants.PORT);
				ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());
				InitialMessage initialMessage = new InitialMessage(clientIPAddress, sIP);
				initialMessage.setServerDelay(serverDelay);
				initialMessage.setEndResult(true);
				toServer.writeObject(initialMessage);
				toServer.flush();
			}		
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	//=============================================================================================================
	/**
	 * 
	 */
	public void sendJoinRequest()
	{
		try 
		{
			
			Object[] servers = serverList.keySet().toArray();
			Object randomServer = servers[new Random().nextInt(servers.length)];
			String message = "Client:"+clientIPAddress+" send JOIN request to:"+randomServer;
			Logger.print(message);
			InteractionMessage intMessage = new InteractionMessage(clientIPAddress, clientPort, Operation.JOIN, myClientData);
			String s = (String) randomServer;
			Socket socket = new Socket(s,Constants.PORT);
			ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());
			toServer.writeObject(intMessage);
			toServer.flush();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
	}
	//======================================================================================
	/**
	 * 
	 */
	public void initialTimerTask()
	{
		try 
		{
			//First state get controller info.
			timerTask = new TimerTask() 
			{
				@Override
				public void run() 
				{
					if(assignedServerIpAdress.equalsIgnoreCase(" "))
					{
						if(latencyReceive == serverList.size()&& !informInitialLatency)
						{
							informInitialLatency();
							informInitialLatency = true;
							
						}
						if(latencyReceive == serverList.size() && !joinRequestSend)
						{
							sendJoinRequest();
							joinRequestSend = true;
						}
					}
					sendPeriodicHello();
				}
			};
			int r = generateNumber(1, serverList.size());
			//Start the process.
			timer.schedule(timerTask, r * 500, r*1000);
		} 
		catch (Exception e) 
		{
			System.out.println("Client:initialTimerTask() method");
			e.printStackTrace();
		}
	}
	
	public void initialClientBehaviourTask()
	{
		try 
		{
			clientBehaviourTask = new TimerTask() 
			{
				@Override
				public void run() 
				{
					clientBehaviour();
				}
			};
			int r = generateNumber(1, serverList.size());
			r = r+4;
			//Start the process.
			timer.schedule(clientBehaviourTask, r * 500, r*1000);
		} 
		catch (Exception e) 
		{
			System.out.println("Client:initialClientBehaviourTask()");
			e.printStackTrace();
		}
	}
	//=====================================================================================
	/**
	 * Send periodic HELLO messages to assigned server.
	 * So server can check the connection based on message
	 * reception.
	 * 
	 */
	public void sendPeriodicHello()
	{
		try 
		{
			//If current client is assigned to server
			if(!assignedServerIpAdress.equalsIgnoreCase(" "))
			{
				periodicClientMessage.setTime(new Time());
				connectToServer(periodicClientMessage);
			}
		} 
		catch (Exception e) 
		{
			System.out.println("Client.sendPeriodicHello()");
			e.printStackTrace();
		}
	}
		
	@Override
	/**
	 * When client gets this message it control the message via this 
	 * method.
	 */
	public void update(Observable o, Object obj) 
	{
		/**
		 * Check the coming message do required operation.
		 */
		try 
		{
			if(o instanceof MessageBox)
			{
				MessageBox mBox = (MessageBox)o;
				this.messageBox = mBox;
				/**
				 * Message is in the stack.So get it out and do
				 * the process.
				 */
				Interactable msg = messageBox.pop();
				if(msg instanceof InitialMessage)
				{
					InitialMessage initialFromServer = (InitialMessage) (msg);
					if(initialFromServer.isFromServer())
					{
						if(serverList.containsKey(initialFromServer.getServerIp()))
						{
							long delay = new Time().timeDifference(initialFromServer.getTime());
							serverDelay.put(initialFromServer.getServerIp(), delay);
							String message = "Server:"+initialFromServer.getServerIp()+" Latency:"+delay;
							Logger.print(message);
							latencyReceive++;
						}
					}
				}
				else
				{
					doOperationProcess(msg);
				}

			}
			
		} catch (Exception e) {
			System.out.println("Client:update()");
			e.printStackTrace();
		}
	}
	//===============================================================================
	/**
	 * For each coming messages below method is executed and
	 * necessary operations are applied.
	 * 
	 * @param msg
	 */
	public void doOperationProcess(Interactable msg)
	{
		try 
		{
			Operation op = msg.getOperation();
			String message = "";
			switch (op) 
			{
			//Receives HELLO messages
			case HELLO:
				message = "HELLO from "+msg.getSenderIpAddress();
				responseHelloOperation(msg);
				break;
			case INTERACT:
				message = "INTERACT response from "+msg.getSenderIpAddress();
				responseInteractOperation(msg);
				Logger.print(message);
				break;
			case JOIN:
				message = "JOIN response from "+msg.getSenderIpAddress();
				responseJoinOperation(msg);
				Logger.print(message);
				break;
			}
			
		} 
		catch (Exception e) 
		{
			System.out.println("Client.doOperationProcess()");
			e.printStackTrace();
		}
		
	}
	//===============================================================================
	//==================================================================================
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
			Set<String>	set = serverList.keySet();
			Iterator<String>i = set.iterator();
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
		} 
		catch (Exception e) 
		{
			System.out.println("Client.controlListTimerExpiration()");
			e.printStackTrace();
		}
	}
	//===============================================================================
	/**
	 * For each coming server's HELLO message, client updates
	 * the server list and related server metadata.
	 *  
	 * @param msg
	 */
	public void responseHelloOperation(Interactable msg)
	{
		try 
		{
			String serverIP = msg.getSenderIpAddress();
			if(!serverIP.equalsIgnoreCase(" "))
			{
				if(assignedServerIpAdress.equalsIgnoreCase(serverIP))
				{
					ServerMetaData sMeta = msg.getServerMetaData();
					sMeta.setLastTransaction(new Time());
					serverList.remove(assignedServerIpAdress);
					serverList.put(assignedServerIpAdress, sMeta);
				}
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	//===============================================================================
	public void responseInteractOperation(Interactable msg)
	{
		try 
		{
			String serverIp = msg.getSenderIpAddress();
			String cIP = msg.getReceiverIpAddress();
			String interactedClient = msg.getInteractIP();
			if(cIP.equalsIgnoreCase(clientIPAddress))
			{
				String log = Constants.INTRESP + " " + clientIPAddress + " "
						+ serverIp + " " + interactedClient + " "
						+ msg.getSequence() + " " + System.currentTimeMillis();
				Logger.log(log);
				Logger.print(log);
			}
			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	//==============================================================================
	public void responseJoinOperation(Interactable msg)
	{
		try 
		{
			String cIP = msg.getReceiverIpAddress();
			if(cIP.equalsIgnoreCase(clientIPAddress))
			{
				
				InteractionResponse intResponse = (InteractionResponse)(msg);
				assignedServerIpAdress = intResponse.getConnectedServer();
				String message = "Client:"+clientIPAddress+" Assigned==>"+assignedServerIpAdress;
				Logger.print(message);
			}
			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	
	//===============================================================================
	/**
	 * This method is evaluated by client and for each evaluation 
	 * client will implement some methods such as;
	 * DELETE:     client will disconnect from connected server and starts
	 * 		       to initial process again.
	 * INTERACT:   client wants to interact with another client
	 */
	public void clientBehaviour()
	{
		try 
		{
			//If current client is assigned to server
			if(!assignedServerIpAdress.equalsIgnoreCase(" "))
			{
				Operation op   = Operation.r.random();
				switch (op) 
				{
				case DELETE:
					responseClientDeleteBehaviour();
					break;
				case INTERACT:
					responseInteractBehaviour();
					break;
				}
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
	}
	//===================================================================================
	public void responseInteractBehaviour()
	{
		try 
		{
			/*
			 * Control if client is connected.If it is
			 * connected then initiate an interaction between
			 * other clients.
			 */
			if(!assignedServerIpAdress.equalsIgnoreCase(" "))
			{
				Object[] clients = clientList.keySet().toArray();
				Object randomClient = clients[new Random().nextInt(clients.length)];
				String message = "Client:"+clientIPAddress+" wants INTERACTION with:"+randomClient;
				totalInteraction++;
				String randClient = (String)(randomClient);
				InteractionRequest intRequest = new InteractionRequest(clientIPAddress, 
						clientPort, assignedServerIpAdress,message);
				intRequest.setOperation(Operation.INTERACT);
				intRequest.setInteractedClientIP(randClient);
				intRequest.setSequence(totalInteraction);
				connectToServer(intRequest);
				Logger.print(message);
				
				String log = Constants.INTREQ + " " + assignedServerIpAdress
						+ " " + randomClient + " " + totalInteraction + " "
						+ System.currentTimeMillis();
				Logger.log(log);
				Logger.print(log);
			}
			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	//===================================================================================
	/**
	 * 
	 */
	public void responseClientDeleteBehaviour()
	{
		try 
		{
			/*
			 * Control if client is connected
			 * Connected then send delete message
			 * to server and restarts initial 
			 * process
			 */
			if(!assignedServerIpAdress.equalsIgnoreCase(" ") && totalInteraction % 10 == 0)
			{
				InteractionMessage intMessage = new InteractionMessage(clientIPAddress, 
						clientPort, Operation.DELETE,myClientData);
				connectToServer(intMessage);
				assignedServerIpAdress = " ";
				cancelTimerTasks();
				timer = new Timer();
				/*
				 * Start initial process again.
				 */
				latencyReceive = 0;
				joinRequestSend = false;
				informInitialLatency = false;
				computeInitialLatency();
				informInitialLatency();
				initialTimerTask();
				initialClientBehaviourTask();
			}
			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	public void startInitialProcess()
	{
		try 
		{
			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	//===============================================================================
	/**
	 * When client wants to connect its assigned server then
	 * it will call the below method.
	 */
	public void connectToServer(Interactable m) 
	{
		try
		{
			Socket socket = new Socket(assignedServerIpAdress,Constants.PORT);
			ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());
			toServer.writeObject(m);
			toServer.flush();
		}
		catch (Exception e) 
		{
			System.out.println("Client:connectToServer()");
			e.printStackTrace();
		}
	}
	//==================================================================================
	/**
	 * By using the coming message the timer task is cancelled.
	 * So when the clients wants to cancel the timer task the 
	 * cancelTimerTask is executed.
	 */
	public void cancelTimerTasks()
	{
		try 
		{
			timer.cancel();
			timer.purge();
		}
		catch (Exception e) 
		{
			System.out.println("Client:cancelTimerTasks()");
			e.printStackTrace();
		}
	}
	//================================================================
	/**
	 * Generate random number in specific range.
	 * 
	 * @param min
	 * @param max
	 * @return
	 */
	public int generateNumber(int min,int max)
	{
		try 
		{
			return min + (int)(Math.random() * ((max - min) + 1));
			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return -1;
		}
	}
	//==========================================================================
	/**
	 * Sleep the current thread in random
	 * second.
	 * @param random
	 */
	public void sleep(int random)
	{
		try 
		{
			Thread.sleep(random * 1000 );
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	//===========================================================================
	public String getMyIpAddress() 
	{
		return clientIPAddress;
	}
	public void setMyIpAddress(String myIpAddress) 
	{
		this.clientIPAddress = myIpAddress;
	}
	public int getMyPort() 
	{
		return clientPort;
	}
	public void setMyPort(int myPort) 
	{
		this.clientPort = myPort;
	}
	public String getAssignedServerIpAdress() 
	{
		return assignedServerIpAdress;
	}
	public void setAssignedServerIpAdress(String assignedServerIpAdress) 
	{
		this.assignedServerIpAdress = assignedServerIpAdress;
	}
	public int getTotalClient() 
	{
		return totalClient;
	}
	public void setTotalClient(int totalClient) 
	{
		this.totalClient = totalClient;
	}
	public void printStartingMessages()
	{
		System.out.println("Client is startting");
	}
	public int getMax() {
		return max;
	}
	public void setMax(int max)
	{
		this.max = max;
	}
	public int getMin() 
	{
		return min;
	}
	public void setMin(int min) 
	{
		this.min = min;
	}
	
	
	
	
	
	
	

}
