package client.state;

import client.Client;

/**
 * Class TimeOut is used for state time-out mechanism.
 * @author seyhan
 *
 */
public class TimeOut implements Runnable{
	//Declare variable.
	private Client client;
	private State nextState;
	private int delay;
	private String message = "";
	
	public TimeOut(){
		
	}
	public TimeOut(int d,String m,Client c,State next){
		this.delay = d;
		this.message = m;
		this.client = c;
		this.nextState = next;
	}
	public void run() {
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
		}
		System.out.println(message);
		changeState();
	}
	public void changeState(){
		client.setState(nextState);
		client.setAssignedServerIpAdress("");
		client.setControllerIpAddress("");
		client.initialTimerTask();
	}
}
