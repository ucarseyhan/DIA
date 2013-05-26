package messages;

import java.util.Observable;
import java.util.Stack;


public class MessageBox extends Observable 
{
	/**
	 * MessageBox class is a observable Message that each client has.
	 * When the listening port receives a message it push to this 
	 * box and inform the clients about the message. After getting the 
	 * message the client decode it and process by using its flag.
	 */
	//Declare variables.
	private Stack<Interactable> messageBox;
	//Constructors.
	public MessageBox()
	{
		messageBox = new Stack<Interactable>();
	}
	public void addMessage(Interactable m)
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
	public Interactable pop()
	{
		return  messageBox.pop();
	}
	public Interactable peek()
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