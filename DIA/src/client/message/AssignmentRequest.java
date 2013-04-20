package client.message;


/**
 * 
 * @author seyhan
 * THe class is used for
 *
 */
public class AssignmentRequest extends Message {
	private int port = 4000;
	private String clientIp = "";
	
	public AssignmentRequest(int port, String clientIp) {
		this.port = port;
		this.clientIp = clientIp;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}
	
	
	

}
