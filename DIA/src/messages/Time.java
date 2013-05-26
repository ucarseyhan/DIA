package messages;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Time class is used for time purposes jobs. For
 * example if server does not get any messages from 
 * connected clients in given amount of time then 
 * server comes to an agreement that client goes down.
 * 
 * This procedure is same as for servers as well. If one of
 * the server does not get any messages from another server
 * in given amount of time. It understands that other server
 * goes down.
 * 
 * @author seyhan
 *
 */
public class Time implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Calendar calendar;
	private String dateFormat = "H:mm:ss:SSS";
	private SimpleDateFormat sdf;
	private long startTime;
	
	public Time()
	{
		sdf = new SimpleDateFormat(dateFormat);
		calendar = Calendar.getInstance();
		startTime = System.currentTimeMillis();
		
	}
	/**
	 * Compare the time object with the coming parameter 
	 * time. Return the result millisecond difference.
	 * @param t
	 * @return
	 */
	public int dayDifference(Time t)
	{

		try 
		{
			Calendar c = t.getCalendar();
			int hour = c.get(Calendar.HOUR);
			int min = c.get(Calendar.MINUTE);
			int sec = c.get(Calendar.SECOND);
			int mil = c.get(Calendar.MILLISECOND);
			//Compute the difference 
			int hourDifference = (calendar.get(Calendar.HOUR) - hour) * 3600;
			int minDifference  = (calendar.get(Calendar.MINUTE) - min) * 60; 
			int secDifference  = (calendar.get(Calendar.SECOND) - sec);
			int milDifference =  (calendar.get(Calendar.MILLISECOND) - mil);
			return (hourDifference+minDifference+secDifference+milDifference);
		} 
		catch (Exception e) 
		{
			// TODO: handle exception
			System.out.println("Time.difference()");
			e.printStackTrace();
			return 0;
		}
	}
	/**
	 * Return the long elapsed time by using the parameter 
	 * time and current time. This method is used by server 
	 * to validate the server if it is still functioning.
	 * 
	 * @param t
	 * @return
	 */
	public long timeDifference(Time t)
	{

		try 
		{
			long startTime = t.getStartTime();
			return System.currentTimeMillis() - startTime;
		} 
		catch (Exception e) 
		{
			// TODO: handle exception
			System.out.println("Time.difference()");
			e.printStackTrace();
			return 0;
		}
	}
	
	
	/**
	 * Print the date in string format.
	 */
	public String toString()
	{
		return sdf.format(calendar.getTime());
	}
	/////////////////////////////////////////////////////////////////
	public Calendar getCalendar() 
	{
		return calendar;
	}

	public void setCalendar(Calendar calendar) 
	{
		this.calendar = calendar;
	}
	
	
	public long getStartTime() 
	{
		return startTime;
	}
	public void setStartTime(long startTime) 
	{
		this.startTime = startTime;
	}
	
//	public static void main(String[] args)
//	{
//		Time t = new Time();
//		System.out.println(t.toString());
//		try {
//			Thread.sleep(5);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		Time t1 = new Time();
//		System.out.println(t1.toString());
//		System.out.println(t.dayDifference(t1));
//		
//	}
	
	
	
	
	

}
