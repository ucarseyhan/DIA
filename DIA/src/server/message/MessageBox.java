package server.message;

import java.util.Observable;
import java.util.concurrent.LinkedBlockingQueue;


public class MessageBox extends Observable {
	/**
	 * MessageBox class is a observable Message that each client has.
	 * When the listening port receives a message it push to this 
	 * box and inform the clients about the message. After getting the 
	 * message the client decode it and process by using its flag.
	 */
	//Declare variables.
	private LinkedBlockingQueue<Interactable> messageBox;
	
	//Constructors.
	public MessageBox(){
		messageBox = new LinkedBlockingQueue<Interactable>(1);
	}
	public void addMessage(Interactable m){
		try {
			messageBox.add(m);
			messageBoxChanged();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}
	public Interactable pop(){
		return  messageBox.poll();
	}
	public Interactable peek(){
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