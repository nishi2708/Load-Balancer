package com.cc.api.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.stereotype.Component;
@Component
public class PropertiesService {
	static String user = "";
	static String password = "";
	static String host = "";
	static String cmd1 = "";
	static String cmd2 = "";
	static String keyfile = "";
	
	
	
	static {
		Properties prop = new Properties();
		
    	InputStream input = null;
    	try {

    		String filename = "config.properties";
    		input = PropertiesService.class.getClassLoader().getResourceAsStream(filename);
    		if(input==null){
    	            System.out.println("Sorry, unable to find " + filename);
    		    
    		}

    		//load a properties file from class path, inside static method
    		prop.load(input);

                //get the property value and print it out
                user = prop.getProperty("user");
    	        password = prop.getProperty("password");
    	        cmd1 = prop.getProperty("cmd-part1");
    	        cmd2 = prop.getProperty("cmd-part2");
    	        keyfile = prop.getProperty("keyfile");

    	        

    	} catch (IOException ex) {
    		ex.printStackTrace();
        } finally{
        	if(input!=null){
        		try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        	}
        }

	}
	
	public String getUser() {
		return user;
	}
	public String getPassword() {
		return password;
	}
	public  String  getCmd(String url) {
		// TODO Auto-generated method stub
		return cmd1 + " " + url + " " + cmd2;
	}
	public String getHost(int i) {
		String host = "";
Properties prop = new Properties();
		
    	InputStream input = null;
    	try {

    		String filename = "config.properties";
    		input = PropertiesService.class.getClassLoader().getResourceAsStream(filename);
    		if(input==null){
    	            System.out.println("Sorry, unable to find " + filename);
    		    
    		}
    		prop.load(input);
    		host  = prop.getProperty("instance"+i);

              
                
    	        
    	        

    	} catch (IOException ex) {
    		ex.printStackTrace();
        } finally{
        	if(input!=null){
        		try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        	}
        	
        }
		return host;
		
		
	}
	public String getKeyfile() {
		// TODO Auto-generated method stub
		return keyfile;
	}
	
	
}

