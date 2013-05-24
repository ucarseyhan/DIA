package server;

public class Logger 
{
	public static void print(String message)
	{
		if(Constants.LOG)
		{
			System.out.println(message);
		}
	}

}
