package server;

import java.util.LinkedList;
/**
 * 
 * @author seyhan
 *
 */
public class WaitingList extends LinkedList<ClientMetaData> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public WaitingList()
	{
		super();
	}
	
	public boolean add(ClientMetaData cmd) 
	{
		int index = 0;
		for (; index < size(); index++) 
		{
			ClientMetaData currentData = get(index);
			if (currentData.compareTo(cmd) == 1)  break;
			
		}
		add(index, cmd);
		return true;
	}
	
//	public static void main(String[] args){
//		WaitingList w = new WaitingList();
//		ClientMetaData c1 = new ClientMetaData();
//		c1.setLatencyToQoSServer(111126);
//		
//		ClientMetaData c2 = new ClientMetaData();
//		c2.setLatencyToQoSServer(908765);
//		
//		
//		ClientMetaData c3 = new ClientMetaData();
//		c3.setLatencyToQoSServer(608765);
//		w.add(c2);
//		w.add(c3);
//		w.add(c1);
//		
//
//		
//		
//		
//		
//        for (Iterator<ClientMetaData> iterator = w.iterator(); iterator.hasNext();) {
//            ClientMetaData c = (ClientMetaData) iterator.next();
//            System.out.println(c.getLatencyToQoSServer());
//        }
//	}

}
