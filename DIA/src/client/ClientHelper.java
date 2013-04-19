package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientHelper implements Runnable {
	/**
	 * NodeHelper class is used for listening the upcoming connection. It is
	 * started as a thread in the node class. Listen the port and push the
	 * coming message to message box for the purpose of the node usage.
	 */
	/**
	 * Declare the variable.
	 */
	private int port = 4000;
	private MessageBox messageBox;
	private ObjectInputStream inputFromClient;
	private ServerSocket serverSocket;

	// Constructors
	
	public ClientHelper() {

	}

	public ClientHelper(MessageBox mBox) {
		this.messageBox = mBox;
		try {
			serverSocket = new ServerSocket(this.port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Start the listening porcess.
	public void start() {
		try {
			System.out.println("Server serverhelper serverport is started:"+ port);
			while (true) {
				Socket clientSocket = serverSocket.accept();
				inputFromClient = new ObjectInputStream(clientSocket.getInputStream());
				Message msg = (Message) inputFromClient.readObject();
				messageBox.addMessage(msg);

			}
		} catch (Exception e) {
			// TODO: handle exception
			// e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (true) {
			start();
		}
	}
}
