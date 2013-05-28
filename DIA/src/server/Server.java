package server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
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
import java.util.concurrent.ConcurrentHashMap;

import messages.ClientMetaData;
import messages.Constants;
import messages.InitialMessage;
import messages.Interactable;
import messages.InteractionMessage;
import messages.InteractionResponse;
import messages.Logger;
import messages.Operation;
import messages.ServerMetaData;
import messages.Time;
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
	private String serverIp = "";
	private int serverPort = Constants.PORT;
	private int capacity = 0;
	private int totalClient = 0;
	private boolean RJL = false;
	private long rjlThreshold;
	private long QoSthr;
	//Server data structure to keep the clients
	private SocketBox socketBox;
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
	
	Socket clientSocket;
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
		socketBox = new SocketBox();
		socketBox.addObserver(this);
		readServerFiles();
		readParameterFiles();
		//Set necessary variables
		this.setServerIp(serverIp);
		this.setServerPort(serverPort);

		//Create the server helper
		serverHelper = new ServerHelper(serverPort,socketBox);

		//Create the server list.
		waitingList = new WaitingList();
		//Start the server helper
		new Thread(serverHelper).start();
		/**
		 * Set the interaction message's attributes.
		 */
		myServerData = new ServerMetaData(serverPort, serverIp,capacity);
		//No need to send client list
		myServerData.setClientList(clientList);
		
		periodicServerMessage = new InteractionMessage(serverIp, serverPort,
				Operation.HELLO, clientList, myServerData);
		//Set the message sender as server
		periodicServerMessage.setServerRole(true);
		
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
						// Initial connected client is empty
						ServerMetaData serverMetaData = new ServerMetaData();
						serverMetaData.setServerIp(ip);
						serverList.put(ip, serverMetaData);
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
	                    capacity = Integer.parseInt(splitted[0]);
	                    RJL  =  (Integer.parseInt(splitted[1]) == 1)? true:false;
	                    rjlThreshold = (long) Double.parseDouble(splitted[2]);
	                    QoSthr = Long.parseLong(splitted[3]);
	                    totalClient = Integer.parseInt(splitted[4]);
	                    Constants.LOG  =  (Integer.parseInt(splitted[5]) == 1)? true:false;
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
			System.out.println("Random:"+random+" Client Size:"+clientList.size()+" Server Size:"+serverList.size() );
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
				if(!ip.equalsIgnoreCase(serverIp))
				{
					int port = serverList.get(ip).getPort();
					periodicServerMessage.setTime(new Time());
					periodicServerMessage.setServerMetaData(myServerData);
					connectToServer(ip, port, periodicServerMessage);
				}

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
				periodicServerMessage.setTime(new Time());
				periodicServerMessage.setServerMetaData(myServerData);
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
			if(o instanceof SocketBox)
			{
				SocketBox sBox = (SocketBox)o;
				this.socketBox = sBox;
				
				clientSocket = socketBox.pop();
				Runnable r = new Runnable() 
				{
					Socket comingSocket = clientSocket;
					@Override
					public void run() 
					{
						ObjectInputStream inputFromClient = null;
						try 
						{
							inputFromClient = new ObjectInputStream(comingSocket.getInputStream());
							Interactable m = (Interactable)inputFromClient.readObject();
							doOperationProcess(m);
						} 
						catch (Exception e) 
						{
							e.printStackTrace();
						}	

						
					}
				};
				new Thread(r).start();
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
			if(msg instanceof InitialMessage)
			{
				
				InitialMessage init = (InitialMessage)(msg);
				Logger.print("Server get Initial Message from:"+init.getIp()+" End Result:"+init.isEndResult());
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
					init.setServerIp(serverIp);
					try 
					{
						Socket socket = new Socket(init.getIp(),Constants.PORT);
						ObjectOutputStream toClient = new ObjectOutputStream(socket.getOutputStream());
						toClient.writeObject(init);
						toClient.flush();
						socket.close();
					}
					catch (Exception e) 
					{
						e.printStackTrace();
					}
				}
			}
			else
			{
				//controlListTimerExpiration(); 
				Operation op = msg.getOperation();
				String message = "";
				switch (op) 
				{
				//Join client into system
				case JOIN:
					message = "JOIN REQUEST from:"+msg.getSenderIpAddress();
					responseJoinOperation(msg);
					Logger.print(message);
					break;
				//Interact with other clients
				case INTERACT:
					String clientIp = msg.getClientIp();
					String interactIp = msg.getInteractIP();
					message = "Client:"+clientIp+" wants INTERACT with:"+interactIp;
					responseInteractOperation(msg);
					Logger.print(message);
					break;
				// Add the client into client list
				case ADD:
					message = "ADD from "+msg.getSenderIpAddress();
					Logger.print(message);
					responseAddOperation(msg);
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
				}
			}
			} 
		catch (Exception e) 
		{
			System.out.println("Server.doOperationProcess()");
			e.printStackTrace();
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
				String sIp = (String) i.next();
				if(!sIp.equalsIgnoreCase(serverIp))
				{
					Time now = new Time();
					ServerMetaData sMeta = serverList.get(sIp);
					Time lastTransaction = sMeta.getLastTransaction();
					//If the time difference is greater than threshold remove
					if(now.timeDifference(lastTransaction) > Constants.TIME_THRESHOLD)
					{
						serverList.remove(sIp);
					}
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
					Logger.print("Client:"+cIp+" REMOVED");
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
				ConcurrentHashMap<String, ClientMetaData> clientList = serverList.get(serverIp).getClientList();
				for (String key: clientList.keySet()) 
				{
					if(clientList.get(key).isQoSAssignment()) totalQoS++;
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
			String sMin = "";
			String sOptimal = "";
			String[] servers;
			String line = "";
			
			//Get client ip
			String cIp = msg.getSenderIpAddress();
			Logger.print("JOIN REQUEST from Client:"+cIp);
			//Find the servers
			line = findServerWithMinDelayCapacity(cIp);
			Logger.print("Return Result:"+line);
			/*
			 * Parse the servers
			 */
			servers = line.split("~");
			sMin = servers[0];
			sOptimal = servers[1];
			
			String message = "Client:"+cIp+" sMin:"+sMin+" sOptimal:"+sOptimal;
			Logger.print(message);
			/*
			 * If found sMin and sOptimal are not equal each other then
			 * send waiting list message to sOptimal.
			 */
			if(!sOptimal.equalsIgnoreCase(sMin))
			{
				//Inform the server
				InteractionResponse intResponse = new InteractionResponse(
						serverIp, serverPort, sOptimal, Constants.PORT, "");
				intResponse.setClientIP(cIp);
				intResponse.setAddWaitingList(true);
				intResponse.setServerRole(true);
				intResponse.setOperation(Operation.WAITINGLIST);
				connectToServer(sOptimal, Constants.PORT, intResponse);
			}
			if(!sMin.equalsIgnoreCase(""))
			{
				if(!sMin.equalsIgnoreCase(serverIp))
				{
					//Connect to coming client into defined server
					//Inform the server
					InteractionResponse intResponse = new InteractionResponse(
							serverIp, serverPort, sMin, Constants.PORT, "");
					intResponse.setClientIP(cIp);
					intResponse.setConnecToServerIp(sMin);
					intResponse.setOperation(Operation.ADD);
					connectToServer(sMin, Constants.PORT, intResponse);
					Logger.print("FORWARD :"+sMin+" CLIENT:"+intResponse.getClientIp());
					//Create the messages and send it to client
					intResponse = new InteractionResponse(
							serverIp, serverPort, cIp, Constants.PORT, "");
					intResponse.setConnecToServerIp(sMin);
					intResponse.setOperation(Operation.JOIN);
					connectToClientNode(cIp, Constants.PORT, intResponse);
					message = "Forwarded Server:"+sMin+" Client:"+cIp;
					Logger.print(message);

				}
				else if(sMin.equalsIgnoreCase(serverIp))
				{
					InteractionResponse intResponse = new InteractionResponse(
							serverIp, serverPort, cIp, Constants.PORT, "");
					intResponse.setConnecToServerIp(serverIp);
					intResponse.setOperation(Operation.JOIN);
					connectToClientNode(cIp, Constants.PORT, intResponse);
					//Update the client metadata in first message from client
					ClientMetaData cMeta = clientLatencyData.get(cIp);
					cMeta.setConnectedServerIp(serverIp);
					cMeta.setLastTransaction(new Time());
					clientList.put(cIp, cMeta);
					Logger.print("responseJoinOperation Put Client:"+cIp);
					capacity--;
					message = "Accepted Server:"+sMin+" Client:"+cIp;
					Logger.print(message);
				}
			}
		} 
		catch (Exception e) 
		{
			// TODO: handle exception
			System.out.println("Server.responseJoinOperation()");
			e.printStackTrace();
		}
	}
	//=========================================================================================
	/**
	 * Client wants to interact with another client. Server first
	 * checks the interacted client. If it is server client then
	 * directly sends Interaction response. If the client is not
	 * server client then server forwards  the response to
	 * clients assigned server.
	 * @param msg
	 */
	public void responseInteractOperation(Interactable msg)
	{
		try 
		{
			
			String interactedClientIp = msg.getInteractIP();
			String clientIp = msg.getSenderIpAddress();
			Logger.print("Server:"+serverIp+" Gets INTERACTION:"+clientIp);
			if(!interactedClientIp.equalsIgnoreCase(" "))
			{
				Logger.print("INTERACTED IP:"+interactedClientIp);
				for(String c:clientList.keySet())
				{
					Logger.print("ConnectedClient:"+c);
				}
				if(clientList.containsKey(interactedClientIp))
				{
					String message = "INTERACTION COMPLETED";
					//Client is my client
					Logger.print("COMPLETED");
					int sequence = msg.getSequence();
					InteractionResponse intResponse = new InteractionResponse(
							serverIp, serverPort, clientIp,
							Constants.PORT, message);
					intResponse.setinteractedClient(interactedClientIp);
					intResponse.setOperation(Operation.INTERACT);
					intResponse.setSequence(sequence);
					connectToClientNode(clientIp, Constants.PORT, intResponse);
				}
				else
				{
					String assignedServerIp = findClientAssignedServer(interactedClientIp);
					Logger.print("Found Assigned Server:"+assignedServerIp);
					if(!assignedServerIp.equalsIgnoreCase(""))
					{
						connectToServer(assignedServerIp, Constants.PORT, msg);
					}
				}
			}

			
			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	//=========================================================================================
	/**
	 * Response to add operation.
	 * @param msg
	 */
	public void responseAddOperation(Interactable msg)
	{
		try 
		{
			String senderIp = msg.getSenderIpAddress();
			if(serverList.containsKey(senderIp))
			{
				String clientIP = msg.getClientIp();
				ClientMetaData cMeta = new ClientMetaData();
				cMeta.setConnectedServerIp(serverIp);
				clientList.put(clientIP, cMeta);
				Logger.print("responseAddOperation ClientIP:"+clientIP);
				capacity--;
				
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
								break;
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
		String sOptimal = "";
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
							 * If server has least delay then 
							 * set the optimum one.
							 */
							if(currentDelay < delay)
							{
								sOptimal = walkSip;
							}
							/*
							 * If server has capacity 
							 * then set the sIp
							 */
							Logger.print("Capacity:"+sMeta.getCapacity());
							if(currentDelay < delay && sMeta.getCapacity() > 0)
							{
								sIp = walkSip;
							}
							delay = currentDelay;
						}
					}
				}
			}
			return sIp+"~"+sOptimal;
			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return "";
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

			for (String walkSip : serverList.keySet()) 
			{
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
			String Ip = msg.getSenderIpAddress();
			if(serverList.containsKey(Ip))
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
					capacity++;
					//One client is removed applied reassignment
					applyReAssignmentOperation();
				}
			}
			/*
			 * Client can want to leave from connected 
			 * server. So when client issues and leave
			 * operation remove from client list.  
			 */
			else if(clientList.containsKey(Ip))
			{
				ClientMetaData clientMetaData = msg.getClientMetaData();
				String clientIp = clientMetaData.getIpAddress();
				clientList.remove(clientIp);
				//One client is removed applied reassignment
				applyReAssignmentOperation();
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
			String Ip = msg.getSenderIpAddress();
			/**
			 * If message sender is server.
			 */
			if(isServer)
			{
				/**
				 * Extract information update server list.
				 */
				
				ServerMetaData serverMetaData = msg.getServerMetaData();
				if(serverList.containsKey(Ip))
				{
					serverList.remove(Ip);
					serverList.put(Ip, serverMetaData);
				}
				else
				{
					/**
					 * Else put it into server list.
					 */
					serverList.put(Ip, serverMetaData);
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
						Logger.print("responseHelloOperation Put Client:"+clientIp);
					}
					else
					{
						clientList.put(clientIp, clientMetaData);
						Logger.print("responseHelloOperation Put Client:"+clientIp);
						
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
			for(String key: serverList.keySet())
			{
				ConcurrentHashMap<String , ClientMetaData> list = serverList.get(key).getClientList();
				if(list.containsKey(cIp)) return key;
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
	public void connectToServer(String ip,int port,Interactable m)
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

	
	public SocketBox getSocketBox() 
	{
		return socketBox;
	}
	public void setSocketBox(SocketBox socketBox)
	{
		this.socketBox = socketBox;
	}
	public ConcurrentHashMap<String, ClientMetaData> getClientList() 
	{
		return clientList;
	}
	public void setClientList(ConcurrentHashMap<String, ClientMetaData> clientList) 
	{
		this.clientList = clientList;
	}

	public ServerHelper getServerHelper()
	{
		return serverHelper;
	}
	public void setServerHelper(ServerHelper serverHelper) 
	{
		this.serverHelper = serverHelper;
	}
	public ConcurrentHashMap<String, ServerMetaData> getServerList() 
	{
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
