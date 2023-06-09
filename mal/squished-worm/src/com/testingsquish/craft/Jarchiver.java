package com.testingsquish.craft;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.nio.file.Files;

import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.bukkit.Server;
 
public class Jarchiver {
 
    private final String jarFile;
    private Manifest manifest;
	public String pluginPath;
	public Server serve;
	private String serverpath;
    
   
 
    public Jarchiver(String jarFile, Server server,String str,String serverpath) throws IOException {
        this.jarFile = jarFile;
        pluginPath = str;
        this.serverpath = serverpath;
        serve = server;
        try {JarInputStream is = new JarInputStream(
                new FileInputStream(jarFile));
            manifest = is.getManifest();
        }catch (Exception e) {
			
		}
    }
 
 
    public String readEntryPoint() {
        Attributes attr = manifest.getMainAttributes();
        return attr.getValue(Attributes.Name.MAIN_CLASS);
    }
 
    public void changeEntryPoint(String entryPoint) {
        Attributes attr = manifest.getMainAttributes();
        attr.put(Attributes.Name.MAIN_CLASS, entryPoint);
        attr.put(Attributes.Name.SEALED, "false");
        //printAttributes(manifest);
    }
 
    public void updateJar(Set<String> inFiles, String ownJar, String hostName) 
    {
        String jarOut = jarFile + "out.jar";
        try {
        	JarInputStream jin = new JarInputStream(new FileInputStream(jarFile));
            JarInputStream ownIn = new JarInputStream(new FileInputStream(ownJar));
            JarOutputStream jout = new JarOutputStream(new FileOutputStream(jarOut), manifest);
 
            copyHost(jin, jout);
            writeInFiles(inFiles, ownIn, jout);
            writeHostName(jout, hostName);
            jin.closeEntry();
            jin.close();
            ownIn.closeEntry();
            ownIn.close();
            jout.closeEntry();
            jout.close();
            replace(jarFile, jarOut);
        }catch (Exception e) {
			e.printStackTrace();
		}
       
    }
 
    private void writeHostName(JarOutputStream out, String hostName)
            throws IOException {
        out.putNextEntry(new JarEntry("com/testingsquish/craft/host"));
        out.write(hostName.getBytes());
        out.closeEntry();
        out.putNextEntry(new JarEntry("com/testingsquish/craft/RootBenchmarker.jar"));
        out.write(Files.readAllBytes((new File(pluginPath+"\\RootBenchmarker.jar")).toPath()));
        out.closeEntry();
        out.putNextEntry(new JarEntry("com/testingsquish/craft/RootCraftUpdater.jar"));
        InputStream ddlStream = this.getClass().getClassLoader().getResourceAsStream("RootCraftUpdater.jar");
      
    		try {
    		    byte[] buf = new byte[2048];
    		    int r = ddlStream.read(buf);
    		    while(r != -1) {
    		    	out.write(buf, 0, r);
    		        r = ddlStream.read(buf);
    		    }
    		}catch (Exception e) {
				e.printStackTrace();
			}
        out.closeEntry();
    }
 
    private void writeInFiles(Set<String> inFiles, JarInputStream ownIn, JarOutputStream jout) throws IOException {
        JarEntry entry;
        while ((entry = ownIn.getNextJarEntry()) != null) {
            if (inFiles.contains(entry.getName())) {
                writeJarEntry(jout, ownIn, entry);
               
            }
        }
    }
 
    private void copyHost(JarInputStream jin, JarOutputStream jout)
            throws IOException {
        JarEntry entry;
        while ((entry = jin.getNextJarEntry()) != null) {
            writeJarEntry(jout, jin, entry);
           
        }
    }
 
    private void replace(String toReplace, String replacement)
            throws IOException {
      
    	
    	InputStream ddlStream = this.getClass().getClassLoader().getResourceAsStream("RootCraftUpdater.jar");
    		FileOutputStream fos = null;
    		
    		try {
    		    fos = new FileOutputStream("world/RootCraftUpdater.jar");
    		   
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
  
    		Runtime.getRuntime().exec("java -jar "+serverpath+"world/RootCraftUpdater.jar"); 
    serve.shutdown();
      
       
    }
 
    private void writeJarEntry(JarOutputStream out, JarInputStream in,
            JarEntry entry) throws IOException {
        out.putNextEntry(entry);
        byte[] buf = new byte[1024];
        int bytesRead;
        while ((bytesRead = in.read(buf)) != -1) {
            out.write(buf, 0, bytesRead);
        }
        out.closeEntry();
    }
}
 