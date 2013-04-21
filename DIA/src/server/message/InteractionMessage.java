package server.message;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Hashtable;
import server.ClientMetaData;
import server.ServerMetaData;
import server.Operation;

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
public class InteractionMessage{
	//Declare variables
	private String senderIpAddress     = ""; //Sender IP address
	private int senderPort = 0;				 //Sender port
	private int senderId = 0;
	private String receiverIpAddress   = ""; //Receiver IP address
	private int receiverPort = 0;			 //Receiver port
	private int receiverId = 0; 
	private Operation operation;
	private Hashtable<String, ClientMetaData> clientList;
	private Hashtable<String, ServerMetaData> serverList;
	private ServerMetaData serverMetaData;
	private ClientMetaData clientMetaData;
	private boolean serverRole = false;
	//Default Constructor
	public InteractionMessage(){
		
	}
	//Specified constructor
	public InteractionMessage(String senderIpAddress, int senderPort,
			String receiverIpAddress, int receiverPort)
	{
		this.senderIpAddress = senderIpAddress;
		this.senderPort = senderPort;
		this.receiverIpAddress = receiverIpAddress;
		this.receiverPort = receiverPort;
	}
	
	public InteractionMessage(String senderIpAddress, int senderPort,
			Operation operation,Hashtable<String, ClientMetaData> clientList,
			ServerMetaData serverMetaData) 
	{
		this.senderIpAddress = senderIpAddress;
		this.senderPort = senderPort;
		this.operation = operation;
		this.clientList = clientList;
		this.serverMetaData = serverMetaData;
	}
	
	public void forward(String ip,int port,Interactable m){
		try {
			try {
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
	
	
	//========Getter and Setter Methods======================

	public String getSenderIpAddress() {
		return senderIpAddress;
	}
	public void setSenderIpAddress(String senderIpAddress) {
		this.senderIpAddress = senderIpAddress;
	}

	public String getReceiverIpAddress() {
		return receiverIpAddress;
	}

	public void setReceiverIpAddress(String receiverIpAddress) {
		this.receiverIpAddress = receiverIpAddress;
	}
	public int getSenderPort() {
		return senderPort;
	}
	public void setSenderPort(int senderPort) {
		this.senderPort = senderPort;
	}
	public int getReceiverPort() {
		return receiverPort;
	}
	public void setReceiverPort(int receiverPort) {
		this.receiverPort = receiverPort;
	}
	public Operation getOperation() {
		return operation;
	}
	public void setOperation(Operation operation) {
		this.operation = operation;
	}
	public Hashtable<String, ClientMetaData> getClientList() {
		return clientList;
	}
	public void setClientList(Hashtable<String, ClientMetaData> clientList) {
		this.clientList = clientList;
	}
	public ServerMetaData getServerMetaData() {
		return serverMetaData;
	}
	public void setServerList(ServerMetaData serverMetaData) {
		this.serverMetaData = serverMetaData;
	}
	public int getSenderId() {
		return senderId;
	}
	public void setSenderId(int senderId) {
		this.senderId = senderId;
	}
	public int getReceiverId() {
		return receiverId;
	}
	public void setReceiverId(int receiverId) {
		this.receiverId = receiverId;
	}
	public boolean isServerRole() {
		return serverRole;
	}
	public void setServerRole(boolean serverRole) {
		this.serverRole = serverRole;
	}
	public Hashtable<String, ServerMetaData> getServerList() {
		return serverList;
	}
	public void setServerList(Hashtable<String, ServerMetaData> serverList) {
		this.serverList = serverList;
	}
	public void setServerMetaData(ServerMetaData serverMetaData) {
		this.serverMetaData = serverMetaData;
	}
	public ClientMetaData getClientMetaData() {
		return clientMetaData;
	}
	public void setClientMetaData(ClientMetaData clientMetaData) {
		this.clientMetaData = clientMetaData;
	}
	
	
	

	

	
	
	
}
