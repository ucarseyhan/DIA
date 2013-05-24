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
}
