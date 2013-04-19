package server;

import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
/**
 * Server helper class is used for the purpose each server 
 * is listening the central server. When the central server
 * decide one of the server as the responsible server central 
 * server send responsibility packet to server. This server 
 * is listening via the usage of the ServerHelper class
 * @author seyhan
 *
 */
public class ServerHelper implements Runnable {
	/**
	 * Declare the variable.
	 */
	private int port;
	private MessageBox messageBox;
	private ObjectInputStream inputFromClient;
	/**
	 * Default constructor.
	 */
	public ServerHelper(){
		
	}
	/**
	 * Specified constructor.
	 * @param port
	 */
	public ServerHelper(int port){
		this.port = port;
	}
	/**
	 * start() method is executing when the server helper class is 
	 * executing. When central server wants to communicate with the server
	 * the server is listening via usage of start().
	 */
	public void start(){
		try {
			ServerSocket serverSocket = new ServerSocket(this.port);
			System.out.println("Server serverhelper serverport is started:"+port);
			while (true) {
				Socket clientSocket =  serverSocket.accept();
				inputFromClient = new ObjectInputStream(clientSocket.getInputStream());	
				Message m = (Message)inputFromClient.readObject();
				messageBox.addMessage(m);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	/**
	 * Periodically call the run method for 
	 * the possibility that central server send 
	 * message.
	 */
	@Override
	public void run() {
		while (true) {
			start();
		}
	}
	//////////////////GETTER and SETTER METHODS//////////////////////
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
}
