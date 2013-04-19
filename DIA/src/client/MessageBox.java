package client;

import java.util.Observable;
import java.util.Stack;

public class MessageBox extends Observable {
	/**
	 * MessageBox class is a observable Message that each client has.
	 * When the listening port receives a message it push to this 
	 * box and inform the clients about the message. After getting the 
	 * message the client decode it and process by using its flag.
	 */
	//Declare variables.
	private Stack<Message> messageBox;
	
	//Constructors.
	public MessageBox(){
		messageBox = new Stack<Message>();
	}
	public void addMessage(Message m){
		try {
			messageBox.push(m);
			messageBoxChanged();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}
	public Message pop(){
		return  messageBox.pop();
	}
	public Message peek(){
		return messageBox.peek();
	}
	
	public void messageBoxChanged(){
		setChanged();
		notifyObservers();
	}
	public void clear(){
		messageBox.clear();
	}

}