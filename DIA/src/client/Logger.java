package client;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class Logger 
{
	public static void print(String message)
	{
		if(Constants.LOG)
		{
			System.out.println(message);
		}
	}
	public static void log(String message)
	{
		try 
		{
	          File file = new File(Constants.RESULTFILE);
	          BufferedWriter output = new BufferedWriter(new FileWriter(file));
	          output.write(message+"\n");
	          output.close();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

}
