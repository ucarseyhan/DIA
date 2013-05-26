package messages;

import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;


/**
 * This class is super class of both client interaction request 
 * and interaction reply. Whenever clients want to interact with 
 * each other the InteractionMessage is created and send to server.
 * When server wants to response to related client it again generates
 * the interaction response message and disseminates to client.
 * 
 * @author ucar
 *
 */
public class InteractionMessage implements Interactable,Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//Declare variables
	private String senderIpAddress     = ""; //Sender IP address
	private int senderPort = 0;				 //Sender port
	private int senderId = 0;
	private String receiverIpAddress   = ""; //Receiver IP address
	private int receiverPort = 0;			 //Receiver port
	
	private String connecToServerIp = "";
	private boolean addWaitingList = false;
	private String clientIP = "";
	
	private String interactedClientIP = " ";
	
	private Operation operation;
	private ConcurrentHashMap<String, ClientMetaData> clientList;
	private ConcurrentHashMap<String, ServerMetaData> serverList;
	private ServerMetaData serverMetaData;
	private ClientMetaData clientMetaData;
	private Time time;
	private boolean serverRole = false;
	private boolean requestCompleted = false;
	private int sequence = 0;
	
	//Default Constructor
	public InteractionMessage()
	{
		
	}
	
	//Specified constructor
	public InteractionMessage(String senderIpAddress, int senderPort,
			String receiverIpAddress, int receiverPort)
	{
		this.senderIpAddress = senderIpAddress;
		this.senderPort = senderPort;
		this.receiverIpAddress = receiverIpAddress;
		this.receiverPort = receiverPort;
		this.time = new Time();
	}
	
	public InteractionMessage(String senderIpAddress, int senderPort,
			Operation operation,ConcurrentHashMap<String, ClientMetaData> clientList,
			ServerMetaData serverMetaData) 
	{
		this.senderIpAddress = senderIpAddress;
		this.senderPort = senderPort;
		this.operation = operation;
		this.clientList = clientList;
		this.serverMetaData = serverMetaData;
		this.time = new Time();
	}
	
	public InteractionMessage(String senderIpAddress, int senderPort,
			Operation operation,ClientMetaData clientMetaData) 
	{
		this.senderIpAddress = senderIpAddress;
		this.senderPort = senderPort;
		this.operation = operation;
		this.clientMetaData = clientMetaData;
		this.time = new Time();
	}
	
//	periodicClientMessage = new InteractionMessage(clientIPAddress,clientPort,
//			Operation.DEFAULT,
//			myClientData);
	
	public void forward(String ip,int port,Interactable m)
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
				e.printStackTrace();
			}
		} 
		catch (Exception e) 
		{
		}
	}
	/**
	 * For client return connected client.
	 * @return
	 */
	public String getConnectedServer()
	{
		return clientMetaData.getConnectedServerIp();
	}
	@Override
	public String getClientIp() 
	{
		return clientIP;
	}

	public void setClientIP(String clientIP) 
	{
		this.clientIP = clientIP;
	}
	@Override
	public String getDestinationIp() 
	{
		return receiverIpAddress;
	}

	@Override
	public boolean isServer() 
	{
		return serverRole;
	}

	@Override
	public void setRequestCompleted() 
	{
		requestCompleted = true;
	}

	@Override
	public String getInteractIP() 
	{
		return interactedClientIP;
	}
	
	//========Getter and Setter Methods======================

	public String getSenderIpAddress() 
	{
		return senderIpAddress;
	}
	public void setSenderIpAddress(String senderIpAddress) 
	{
		this.senderIpAddress = senderIpAddress;
	}

	public String getReceiverIpAddress() 
	{
		return receiverIpAddress;
	}

	public void setReceiverIpAddress(String receiverIpAddress) 
	{
		this.receiverIpAddress = receiverIpAddress;
	}
	public int getSenderPort()
	{
		return senderPort;
	}
	public void setSenderPort(int senderPort)
	{
		this.senderPort = senderPort;
	}
	public int getReceiverPort() 
	{
		return receiverPort;
	}
	public void setReceiverPort(int receiverPort) 
	{
		this.receiverPort = receiverPort;
	}
	public Operation getOperation() 
	{
		return operation;
	}
	public void setOperation(Operation operation) 
	{
		this.operation = operation;
	}
	public ConcurrentHashMap<String, ClientMetaData> getClientList() 
	{
		return clientList;
	}
	public void setClientList(ConcurrentHashMap<String, ClientMetaData> clientList) 
	{
		this.clientList = clientList;
	}
	public ServerMetaData getServerMetaData() 
	{
		return serverMetaData;
	}
	public void setServerList(ServerMetaData serverMetaData) 
	{
		this.serverMetaData = serverMetaData;
	}
	public int getSenderId() 
	{
		return senderId;
	}
	public void setSenderId(int senderId) 
	{
		this.senderId = senderId;
	}
	public boolean isServerRole() 
	{
		return serverRole;
	}
	public void setServerRole(boolean serverRole) 
	{
		this.serverRole = serverRole;
	}
	public ConcurrentHashMap<String, ServerMetaData> getServerList() 
	{
		return serverList;
	}
	public void setServerList(ConcurrentHashMap<String, ServerMetaData> serverList) 
	{
		this.serverList = serverList;
	}
	public void setServerMetaData(ServerMetaData serverMetaData) 
	{
		this.serverMetaData = serverMetaData;
	}
	public ClientMetaData getClientMetaData() 
	{
		return clientMetaData;
	}
	public void setClientMetaData(ClientMetaData clientMetaData) 
	{
		this.clientMetaData = clientMetaData;
	}
	public Time getTime() 
	{
		return time;
	}
	public void setTime(Time time) 
	{
		this.time = time;
	}
	public String getConnecToServerIp() 
	{
		return connecToServerIp;
	}
	public void setConnecToServerIp(String connecToServerIp) 
	{
		this.connecToServerIp = connecToServerIp;
	}
	public boolean isAddWaitingList() 
	{
		return addWaitingList;
	}
	public void setAddWaitingList(boolean addWaitingList) 
	{
		this.addWaitingList = addWaitingList;
	}
	
	public boolean isRequestCompleted() 
	{
		return requestCompleted;
	}

	public void setRequestCompleted(boolean requestCompleted)
	{
		this.requestCompleted = requestCompleted;
	}

	public String getClientIP() 
	{
		return clientIP;
	}
	

	public String getInteractedClientIP() 
	{
		return interactedClientIP;
	}

	public void setInteractedClientIP(String interactedClientIP) 
	{
		this.interactedClientIP = interactedClientIP;
	}

	@Override
	public int getSequence() 
	{
		return sequence;
	}

	@Override
	public void setSequence(int seq) 
	{
		sequence = seq;
	}


	

	
	
	

	

	
	
	
}
