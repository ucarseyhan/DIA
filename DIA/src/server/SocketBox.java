package server;

import java.net.Socket;
import java.util.Observable;
import java.util.Stack;

public class SocketBox extends Observable 
{
	//Declare variables.
	private Stack<Socket> messageBox;
	//Constructors.
	public SocketBox()
	{
		messageBox = new Stack<Socket>();
	}
	public void addMessage(Socket m)
	{
		try 
		{
			messageBox.add(m);
			messageBoxChanged();
		} 
		catch (Exception e) 
		{
			// TODO: handle exception
			e.printStackTrace();
		}

	}
	public Socket pop()
	{
		return  messageBox.pop();
	}
	public Socket peek()
	{
		return messageBox.peek();
	}
	
	public void messageBoxChanged()
	{
		setChanged();
		notifyObservers();
	}
	public void clear()
	{
		messageBox.clear();
	}

}
