package server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;
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
import server.message.InitialMessage;
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
	private int serverPort = Constants.PORT;
	private int capacity = 0;
	private int totalClient = 0;
	private boolean RJL = false;
	private long rjlThreshold;
	private long QoSthr;
	//Server data structure to keep the clients
	private MessageBox messageBox;
	private ConcurrentHashMap<String, ClientMetaData> clientList;
	private ConcurrentHashMap<String, ServerMetaData> serverList;
	private ConcurrentHashMap<String, ClientMetaData> clientLatencyData;
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
	 * Create the Server object with specified 
	 * IP and id.
	 * 
	 * @param ip
	 * @param id
	 */
	public Server()
	{
		/**
		 * Read the server and parameter files and
		 * adjust necessary settings
		 */
		//Create the client and server list
		clientList = new ConcurrentHashMap<String,ClientMetaData>();
		serverList = new ConcurrentHashMap<String,ServerMetaData>();
		clientLatencyData = new ConcurrentHashMap<String, ClientMetaData>();
		messageBox = new MessageBox();
		messageBox.addObserver(this);
		readServerFiles();
		readParameterFiles();
		//Set necessary variables
		this.setServerIp(serverIp);
		this.setServerPort(serverPort);

		//Create the server helper
		serverHelper = new ServerHelper(serverPort,messageBox);

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
		/*
		 * Start the Server HELLO messages
		 */
		timer = new Timer();
		initialTimerTask();
	}
	//==================================================================================================
	/**
	 * Read the server file and be aware of the existing
	 * servers
	 */
	public void readServerFiles()
	{
		try 
		{
	        FileInputStream fstream = null;
	        serverIp = GetMachineName();
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
	                    if(!serverIp.equals(ip))
	                    {
	                    	//Initial connected client is empty
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
	 * Read the parameter file. The parameter list is;
	 * 1-server capacity
	 * 2-rjl(1 active, 0 disabled)
	 * 3-rjlThreshold
	 * 4-QoSThreshold
	 * 5-totalClient
	 */
	public void readParameterFiles()
	{
		try 
		{
	        FileInputStream fstream = null;
	        serverIp = GetMachineName();
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
	            String[] splitted = new String[6];
	            while ((strLine = br.readLine()) != null) 
	            {
	                if(!strLine.equalsIgnoreCase(""))
	                {
	                    splitted = strLine.split(" ");
	                    int capacity = Integer.parseInt(splitted[0]);
	                    int rjl = Integer.parseInt(splitted[1]);
	                    long timerThreshold = (long) Double.parseDouble(splitted[2]);
	                    long qualityOfServerThr = Long.parseLong(splitted[3]);
	                    int totalClient = Integer.parseInt(splitted[4]);
	                    int log = Integer.parseInt(splitted[5]);
	                    if(log == 1) Constants.LOG = true;
	                    /**
	                     * Different than current server then create 
	                     * the Server meta data
	                     */
                    	rjlThreshold= timerThreshold;
                    	QoSthr = qualityOfServerThr;
                    	this.totalClient = totalClient;
                    	if(rjl == 1) RJL = true;
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
	//============================================================================================
	/**
	 * Create the initialTimer Task
	 */
	public void initialTimerTask()
	{
		try 
		{
			/**
			 * Periodically send HELLO messages
			 */
			timerTask = new TimerTask() 
			{
				@Override
				public void run() 
				{
					sendPeriodicHello();
				}
			};
			int random = rand.nextInt(clientList.size()+serverList.size());
			if(random == 0) random = rand.nextInt(5) +1;
			System.out.println("Random:"+random+" Size:"+clientList.size()+" -"+serverList.size() );
			//Start the process.
			timer.schedule(timerTask, random * 500, random * 1000);
		} 
		catch (Exception e) 
		{
			System.out.println("Client:initialTimerTask() method");
			e.printStackTrace();
		}
	}
	//===============================================================================================
	/**
	 * Send periodic HELLO messages so others (clients and servers)
	 * can be aware of the server existence.
	 * 
	 */
	public void sendPeriodicHello()
	{
		try 
		{
			/**
			 * First share the HELLO messages with servers.
			 */
			Set<String> set = serverList.keySet();
			Iterator<String> i = set.iterator();
			while (i.hasNext()) 
			{
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
			while (i.hasNext()) 
			{
				String ip = (String) i.next();
				int port = clientList.get(ip).getPort();
				connectToClientNode(ip, port, periodicServerMessage);
			}
		
		} 
		catch (Exception e) 
		{
			System.out.println("Server.sendPeriodicHello()");
			e.printStackTrace();
		}
	}
	//========================================================================================================
	@Override
	/**
	 * Observer pattern update method. For each coming packet
	 * message box is updated
	 */
	public void update(Observable o, Object arg) 
	{
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
				/**
				 * If message is InitialMessage then client try to learn
				 * the delay statics. Server wants to calculate latency
				 * just reply.
				 */
				if(msg instanceof InitialMessage)
				{
					InitialMessage init = (InitialMessage)(msg);
					/**
					 * If it is end result then save the client information.
					 */
					if(init.isEndResult())
					{
						ClientMetaData cM = new ClientMetaData(Constants.PORT, init.getIp(), "");
						cM.setClientServerLatency(init.getServerDelay());
						clientLatencyData.put(init.getIp(),cM);
					}
					else
					{
						/**
						 * Reply to client
						 */
						init.setFromServer(true);
						try 
						{
							Socket socket = new Socket(init.getIp(),Constants.PORT);
							ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());
							toServer.writeObject(init);
							toServer.flush();
							socket.close();
						}
						catch (Exception e) 
						{
							// TODO: handle exception
							e.printStackTrace();
						}
					}
				}
				else
				{
					String targetIp = msg.getReceiverIpAddress();
					String clientIp = msg.getSenderIpAddress();
					boolean myClient = isMyClient(targetIp);
					String assignedServerIp = findClientAssignedServer(clientIp);
					doOperationProcess(msg);
					msg.doOperation(msg, myClient, assignedServerIp);
				}

				
			}
		} 
		catch (Exception e) 
		{
			System.out.println("Server.update()");
			e.printStackTrace();
		}
	}
	//======================================================================================
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
			controlListTimerExpiration(); 
			Operation op = msg.getOperation();
			String message = "";
			switch (op) 
			{
			//Join client into system
			case JOIN:
				message = "JOIN from "+msg.getSenderIpAddress();
				responseJoinOperation(msg);
				Logger.print(message);
				break;
			// Add the client into client list
			case ADD:
				message = "ADD from "+msg.getSenderIpAddress();
				responseAddOPeration(msg);
				Logger.print(message);
				break;
			//Delete client	
			case DELETE:
				message = "DELETE from "+msg.getSenderIpAddress();
				responseDeleteOperation(msg);
				Logger.print(message);
				break;
			//Receives HELLO messages
			case HELLO:
				message = "HELLO from "+msg.getSenderIpAddress();
				responseHelloOperation(msg);
				Logger.print(message);
				break;
			//Return server summary
			case SUMMARY:
				message = "SUMMARY from "+msg.getSenderIpAddress();
				responseSummaryOperation(msg);
				Logger.print(message);
				break;
			case WAITINGLIST:
				message = "WAITINGLIST from "+msg.getSenderIpAddress();
				responseWaitingListOperation(msg);
				Logger.print(message);
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
					/*
					 * When client is removed from the server
					 * then check the waiting list to reassignment.
					 */
					applyReAssignmentOperation();
					
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
	//===================================================================================
	/**
	 * When client disconnect from server the server 
	 * control the current executing algorithm and apply
	 * based on the current algorithm.
	 */
	public void applyReAssignmentOperation()
	{
		try 
		{
			/*
			 * If executing algorithm is RJL then control the overall 
			 * system performance.
			 */
			if(RJL)
			{
				if(controlRjlPerformance())
				{
					/*
					 * Control the waiting list. If it is not empty
					 * then remove the first one. Add into the server
					 * client list and inform the previous server about
					 * the operation.
					 */
					if(!getWaitingList().isEmpty())
					{
						ClientMetaData cmData = waitingList.removeFirst();
						String cIp = cmData.getIpAddress();
						String sIp = cmData.getConnectedServerIp();
						/*
						 * Send JOIN message to client
						 */
						InteractionResponse intResponse = new InteractionResponse(
								serverIp, serverPort, cIp, Constants.PORT, "");
						intResponse.setConnecToServerIp(serverIp);
						intResponse.setOperation(Operation.JOIN);
						intResponse.forward(cIp, Constants.PORT, intResponse);
						
						/*
						 * Send DELETE messages to previous server
						 */
						InteractionResponse servResponse = new InteractionResponse(
								serverIp, serverPort, sIp, Constants.PORT, "");
						servResponse.setConnecToServerIp(serverIp);
						servResponse.setOperation(Operation.DELETE);
						servResponse.forward(sIp, Constants.PORT, servResponse);
					}
				}
			}
			else
			{
				/*
				 * Control the waiting list. If it is not empty
				 * then remove the first one. Add into the server
				 * client list and inform the previous server about
				 * the operation.
				 */
				if(!getWaitingList().isEmpty())
				{
					ClientMetaData cmData = waitingList.removeFirst();
					String cIp = cmData.getIpAddress();
					String sIp = cmData.getConnectedServerIp();
					/*
					 * Send JOIN message to client
					 */
					InteractionResponse intResponse = new InteractionResponse(
							serverIp, serverPort, cIp, Constants.PORT, "");
					intResponse.setConnecToServerIp(serverIp);
					intResponse.setOperation(Operation.JOIN);
					intResponse.forward(cIp, Constants.PORT, intResponse);
					
					/*
					 * Send DELETE messages to previous server
					 */
					InteractionResponse servResponse = new InteractionResponse(
							serverIp, serverPort, sIp, Constants.PORT, "");
					servResponse.setConnecToServerIp(serverIp);
					servResponse.setOperation(Operation.DELETE);
					servResponse.forward(sIp, Constants.PORT, servResponse);
				}
				
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	//============================================================================
	public boolean controlRjlPerformance()
	{
		int totalQoS = 0;
		try 
		{
			for (String clientIp : clientList.keySet()) 
			{
				ClientMetaData cData = clientList.get(clientIp);
				if(cData.isQoSAssignment()) totalQoS++;
			}
			
			for (String serverIp : serverList.keySet())
			{
				ArrayList<ClientMetaData> clientList = serverList.get(serverIp).getConnectedClient();
				for (int i = 0; i < clientList.size(); i++) 
				{
					if(clientList.get(i).isQoSAssignment()) totalQoS++;
				}
 			}
			double result = 1.0 * (totalQoS /  totalClient);
			if((long)(result) > rjlThreshold) return true;
			return false;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return false;
		}
	}
	//============================================================================
	/**
	 * JOIN operation is used by clients. After proposed 
	 * algorithm execution, client selects one of the 
	 * server and send an interaction request which keeps
	 * the JOIN operation.
	 * 
	 * @param msg
	 */
	public void responseJoinOperation(Interactable msg)
	{
		try 
		{
			String sQoSIp  = "";
			String sMinCap = "";
			String sCap    = "";
			String cIp = msg.getClientIp();
			sQoSIp  = findQoServer(cIp);
			sMinCap = findServerWithMinDelayCapacity(cIp);
			sCap    = findServerCapacity(cIp);
			/*
			 * Try the server with QoS 
			 */
			if(!sQoSIp.equalsIgnoreCase(""))
			{
				if(!sQoSIp.equalsIgnoreCase(serverIp))
				{
					//Control the server capacity
					if(serverHasCapacity(sQoSIp))
					{
						// Connect to coming client into defined server
						// Create the messages and send it to client
						InteractionResponse intResponse = new InteractionResponse(
								serverIp, serverPort, cIp, Constants.PORT, "");
						intResponse.setConnecToServerIp(sQoSIp);
						//Client uses this to inform the server
						intResponse.setQoSAssignment(true);
						intResponse.setOperation(Operation.JOIN);
						intResponse.forward(cIp, Constants.PORT, intResponse);
						//Inform the server
						intResponse = new InteractionResponse(
								serverIp, serverPort, sQoSIp, Constants.PORT, "");
						intResponse.setClientIP(cIp);
						intResponse.setConnecToServerIp(sQoSIp);
						intResponse.setOperation(Operation.ADD);
						intResponse.forward(sQoSIp, Constants.PORT, intResponse);
						

					}
				}
				/*
				 * This client is assigned to me
				 */
				else if(sQoSIp.equalsIgnoreCase(serverIp))
				{
					InteractionResponse intResponse = new InteractionResponse(
							serverIp, serverPort, cIp, Constants.PORT, "");
					intResponse.setConnecToServerIp(serverIp);
					intResponse.setOperation(Operation.JOIN);
					intResponse.forward(cIp, Constants.PORT, intResponse);
					//Update the client metadata in first message from client
					clientList.put(cIp, new ClientMetaData());
					
					
				}

			}
			/*
			 * Try the server with minimum delay and 
			 * available capacity 
			 */
			else if(!sMinCap.equalsIgnoreCase(""))
			{
				if(!sMinCap.equalsIgnoreCase(serverIp))
				{
					//Connect to coming client into defined server
					//Create the messages and send it to client
					InteractionResponse intResponse = new InteractionResponse(
							serverIp, serverPort, cIp, Constants.PORT, "");
					intResponse.setConnecToServerIp(sMinCap);
					intResponse.setOperation(Operation.JOIN);
					intResponse.forward(cIp, Constants.PORT, intResponse);
					//Inform the server
					intResponse = new InteractionResponse(
							serverIp, serverPort, sMinCap, Constants.PORT, "");
					intResponse.setClientIP(cIp);
					intResponse.setConnecToServerIp(sMinCap);
					intResponse.setOperation(Operation.ADD);
					intResponse.forward(sMinCap, Constants.PORT, intResponse);
				}
				else if(sMinCap.equalsIgnoreCase(serverIp))
				{
					InteractionResponse intResponse = new InteractionResponse(
							serverIp, serverPort, cIp, Constants.PORT, "");
					intResponse.setConnecToServerIp(serverIp);
					intResponse.setOperation(Operation.JOIN);
					intResponse.forward(cIp, Constants.PORT, intResponse);
					//Update the client metadata in first message from client
					clientList.put(cIp, new ClientMetaData());
				}

				
			}
			/*
			 * Try the server that satisfy the capacity requirement
			 */
			else if(!sCap.equalsIgnoreCase(""))
			{
				if(!sCap.equalsIgnoreCase(serverIp))
				{
					//Connect to coming client into defined server
					//Create the messages and send it to client
					InteractionResponse intResponse = new InteractionResponse(
							serverIp, serverPort, cIp, Constants.PORT, "");
					intResponse.setConnecToServerIp(sCap);
					intResponse.setOperation(Operation.JOIN);
					intResponse.forward(cIp, Constants.PORT, intResponse);
					
					//Inform the server
					intResponse = new InteractionResponse(
							serverIp, serverPort, sCap, Constants.PORT, "");
					intResponse.setClientIP(cIp);
					intResponse.setConnecToServerIp(sCap);
					intResponse.setOperation(Operation.ADD);
					intResponse.forward(sCap, Constants.PORT, intResponse);
				}
				else if(sCap.equalsIgnoreCase(serverIp))
				{
					InteractionResponse intResponse = new InteractionResponse(
							serverIp, serverPort, cIp, Constants.PORT, "");
					intResponse.setConnecToServerIp(serverIp);
					intResponse.setOperation(Operation.JOIN);
					intResponse.forward(cIp, Constants.PORT, intResponse);
					//Update the client metadata in first message from client
					clientList.put(cIp, new ClientMetaData());
				}

			}
			
		} 
		catch (Exception e) 
		{
			// TODO: handle exception
			System.out.println("Server.responseAddOperation()");
			e.printStackTrace();
		}
	}
	//=========================================================================================
	/**
	 * Response to add operation.
	 * @param msg
	 */
	public void responseAddOPeration(Interactable msg)
	{
		try 
		{
			String senderIp = msg.getSenderIpAddress();
			if(serverList.containsKey(senderIp))
			{
				String clientIP = msg.getClientIp();
				clientList.put(clientIP, new ClientMetaData());
				
			}
			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
	}
	//=========================================================================================
	/**
	 * Via given client IP find the QoS server and 
	 * return to the caller. If the server does not
	 * exist the return an empty server IP
	 * @param cIp
	 * @return
	 */
	public String findQoServer( String cIp)
	{
		String sIp = "";
		try 
		{
			for (String keyIp : clientLatencyData.keySet()) 
			{
				if(cIp.equalsIgnoreCase(keyIp))
				{
					ClientMetaData cmData= clientLatencyData.get(keyIp);
					Hashtable<String, Long> latency = cmData.getClientServerLatency();
					
					for(String walkSip: latency.keySet())
					{
						long currentDelay = latency.get(walkSip);
						ServerMetaData sMeta = serverList.get(walkSip);
						if(sMeta != null)
						{
							/*
							 * If server has least delay
							 */
							if(currentDelay < QoSthr)
							{
								sIp = walkSip;
							}
						}

					}
				}
			}
			return sIp;
			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return sIp;
		}
		
	}
	//=====================================================================================================
	/**
	 * Walk the server list via using the client IP and
	 * found the server with minimum delay and available 
	 * capacity.
	 * 
	 * @param cIp
	 * @return
	 */
	public String findServerWithMinDelayCapacity( String cIp)
	{
		String sIp = "";
		long delay = Long.MAX_VALUE;
		try 
		{
			for (String keyIp : clientLatencyData.keySet()) 
			{
				if(cIp.equalsIgnoreCase(keyIp))
				{
					ClientMetaData cmData= clientLatencyData.get(keyIp);
					Hashtable<String, Long> latency = cmData.getClientServerLatency();
					
					for(String walkSip: latency.keySet())
					{
						long currentDelay = latency.get(walkSip);
						ServerMetaData sMeta = serverList.get(walkSip);
						if(sMeta != null)
						{
							/*
							 * If server has least delay
							 */
							if(currentDelay < delay && sMeta.getCapacity() > 0)
							{
								sIp = walkSip;
								delay = currentDelay;
							}
						}

					}
				}
			}
			return sIp;
			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return sIp;
		}
	}
	//================================================================================
	/**
	 * Find the first server that has capacity so we can assign
	 * the coming client to the first server that satisfies the
	 * capacity requirement.
	 * 
	 * @param cIp
	 * @return
	 */
	public String findServerCapacity(String cIp)
	{
		String sIp = "";
		try 
		{

			for (String walkSip : serverList.keySet()) {
				ServerMetaData sMeta = serverList.get(walkSip);
				if (sMeta != null) 
				{
					/*
					 * If server has least delay
					 */
					if (sMeta.getCapacity() > 0)
					{
						sIp = walkSip;
						return sIp;
					}
				}

			}
			return sIp;
			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return sIp;
		}
		
	}
	//==================================================================================
	/**
	 * Control if given server has capacity
	 * if it has return true, if not false
	 * @param serverIp
	 * @return
	 */
	public boolean serverHasCapacity(String serverIp)
	{
		try 
		{
			for(String sIp: serverList.keySet())
			{
				ServerMetaData sM  = serverList.get(sIp);
				if(sM.getCapacity() > 0) return true;
			}
			return false;
			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return false;
		}
	}
	//===================================================================================
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
	//================================================================================
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
	//==========================================================================================
	public void responseWaitingListOperation(Interactable msg)
	{
		try 
		{
			boolean isserver = msg.isServer();
			if(isserver)
			{
				boolean add = msg.isAddWaitingList();
				if(add)
				{
					ClientMetaData cmData = msg.getClientMetaData();
					waitingList.add(cmData);
				}
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	//===========================================================================================
	/**
	 * Response to coming summary operation from
	 * client. 
	 * ========DISABLED============
	 * @param msg
	 */
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
			InteractionResponse summResponse = new InteractionResponse(serverIp, 
																	   serverPort, 
																	   clientIp, 
																	   clientPort, 
																	   reply);
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
	//============================================================================================
	/**
	 * Control the given client IP. If it
	 * is my client then return true.
	 * 
	 * @param cIp
	 * @return
	 */
	public boolean isMyClient(String cIp) 
	{

		try {
			Set<String> set = clientList.keySet();
			Iterator<String> i = set.iterator();
			while (i.hasNext()) 
			{
				String ip = (String) i.next();
				if (ip.equals(cIp))return true;
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
	//==============================================================================================
	/**
	 * Via given client IP try to find the client
	 * assigned server IP.
	 * 
	 * @param cIp
	 * @return
	 */
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
		} 
		catch (Exception e) 
		{
			// TODO: handle exception
			e.printStackTrace();
			return "";
		}
	}
	//==============================================================================================
	/**
	 * After the central server select the server server 
	 * connect to client and say that it is server of the 
	 * client.
	 */
	public void connectToClientNode(String ip,int port,InteractionMessage m)
	{

		try 
		{
			Socket socket = new Socket(ip, port);
			ObjectOutputStream toServer = new ObjectOutputStream(
					socket.getOutputStream());
			toServer.writeObject(m);
			toServer.flush();
			socket.close();
		} 
		catch (Exception e)
		{
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
	public ConcurrentHashMap<String, ClientMetaData> getClientList() {
		return clientList;
	}
	public void setClientList(ConcurrentHashMap<String, ClientMetaData> clientList) {
		this.clientList = clientList;
	}

	public ServerHelper getServerHelper() {
		return serverHelper;
	}
	public void setServerHelper(ServerHelper serverHelper) {
		this.serverHelper = serverHelper;
	}
	public ConcurrentHashMap<String, ServerMetaData> getServerList() {
		return serverList;
	}
	public void setServerList(ConcurrentHashMap<String, ServerMetaData> serverList) {
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
	public boolean isRJL() {
		return RJL;
	}
	public void setRJL(boolean rJL) {
		RJL = rJL;
	}
	public long getRjlThreshold() {
		return rjlThreshold;
	}
	public void setRjlThreshold(long rjlThreshold) {
		this.rjlThreshold = rjlThreshold;
	}
	public WaitingList getWaitingList() {
		return waitingList;
	}
	public void setWaitingList(WaitingList waitingList) {
		this.waitingList = waitingList;
	}
	public void printStartingMessages()
	{
		System.out.println("Server is startting");
	}
	
	
	
	

}
