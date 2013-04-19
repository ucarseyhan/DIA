package server;

import java.util.ArrayList;

/**
 * Class is used for keeping the server meta data in each
 * server. So by using these server list it can disseminate the 
 * data to other nodes.
 * @author seyhan
 *
 */
public class ServerMetaData {
	private int port = 4000;
	private String serverIp = "";
	private ArrayList<ClientMetaData> connectedClient;
	
	public ServerMetaData(){
		
	}

	public ServerMetaData(int port, String serverIp) {
		this.port = port;
		this.serverIp = serverIp;
		connectedClient = new ArrayList<ClientMetaData>();
	}
	public boolean containClient(ClientMetaData cM){
		try {
			for (int i = 0; i < connectedClient.size(); i++) {
				ClientMetaData cMData = connectedClient.get(i);
				if(cM.getIpAddress().equals(cMData.getIpAddress()))
					return true;
			}
			return false;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
	}
	public void addClient(ClientMetaData cM){
		connectedClient.add(cM);
	}
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}
	
	

}
