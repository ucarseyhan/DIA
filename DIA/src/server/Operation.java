package server;

public enum Operation {
	ADD, 			//Add client
	DELETE, 		//Delete client
	SUMMARY,		//Request server summary
	HELLO,  		//HELLO Messages
	DEFAULT, 		//Default operation
	WAITINGLIST;	//Add into waiting list
	@Override
	public String toString() {
		// only capitalize the first letter
		String s = super.toString();
		return s.substring(0, 1) + s.substring(1).toLowerCase();
	}

}
