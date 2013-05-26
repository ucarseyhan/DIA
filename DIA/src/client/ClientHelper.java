package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import messages.Constants;
import messages.Interactable;
import messages.MessageBox;


public class ClientHelper implements Runnable 
{
	/* 
	 * Declare the variable.
	 */
	private int port = Constants.PORT;
	private MessageBox messageBox;
	private ObjectInputStream inputFromClient;
	private ServerSocket serverSocket;

	//Constructors
	public ClientHelper() 
	{

	}
	//Specified Constructor
	public ClientHelper(MessageBox mBox) 
	{
		this.messageBox = mBox;
		try 
		{
			serverSocket = new ServerSocket(this.port);
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	//===================================================================
	/*
	 * Start the listening process.
	 */
	public void start() 
	{
		try 
		{
			while (true) 
			{
				Socket clientSocket = serverSocket.accept();
				inputFromClient = new ObjectInputStream(clientSocket.getInputStream());
				Interactable msg = (Interactable) inputFromClient.readObject();
				messageBox.addMessage(msg);
			}
		} 
		catch (Exception e) 
		{
		}
	}

	@Override
	public void run() 
	{
		while (true) 
		{
			start();
		}
	}
}
