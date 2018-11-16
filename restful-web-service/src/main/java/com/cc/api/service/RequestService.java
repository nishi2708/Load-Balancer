package com.cc.api.service;

import static org.assertj.core.api.Assertions.setLenientDateParsing;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import com.jcraft.jsch.*;
@Component
public class RequestService{
//  public static void main(String[] arg){
//	  
//	  PropertiesService prop = new PropertiesService();
//	  
//	  String user = prop.getUser();
//	  String host = prop.getHost(1);
//	  String command = prop.getCmd("https://www.w3schools.com/html/img_girl.jpg");
//      String privateKey = prop.getKeyfile();
//      int port = 22;
//  
//    try{
//      JSch jsch=new JSch();
//      jsch.addIdentity(privateKey);
//      Session session = jsch.getSession(user, host, port);
//      System.out.println("session created.");
//      
//      java.util.Properties config = new java.util.Properties();
//      config.put("StrictHostKeyChecking", "no");
//      session.setConfig(config);
//      session.connect();
//
//      ChannelExec channel = (ChannelExec) session.openChannel("exec");
//      channel.setCommand(command);
////      OutputStream out = channel.getOutputStream();
//      channel.connect(3*1000);
//      final ByteArrayOutputStream baos = new ByteArrayOutputStream();
//      channel.setOutputStream(baos);
////      channel.setOutputStream(System.out);
//      TimeUnit.SECONDS.sleep(5);// <------- TO-DO Find a better alternative
//      
//      String output = new String(baos.toByteArray());
//      System.out.println("here:" + output);
//      
//  
//    }
//    catch(Exception e){
//      System.out.println(e);
//    }
//  }
//  
  
  
  public String getImage(int instance_id, String url ) {
	  System.out.println("Function called");
	  String output = "";
	  PropertiesService prop = new PropertiesService();
	  String user = prop.getUser();
	  String host = prop.getHost(instance_id);
	  String command = prop.getCmd(url);
      String privateKey = prop.getKeyfile();
      int port = 22;
  
    try{
      JSch jsch=new JSch();
      jsch.addIdentity(privateKey);
      Session session = jsch.getSession(user, host, port);
      System.out.println("session created.");
      
      java.util.Properties config = new java.util.Properties();
      config.put("StrictHostKeyChecking", "no");
      session.setConfig(config);
      session.connect();

      ChannelExec channel = (ChannelExec) session.openChannel("exec");
      channel.setCommand(command);
      channel.connect(3*1000);
      final ByteArrayOutputStream baos = new ByteArrayOutputStream();
      channel.setOutputStream(baos);
      TimeUnit.SECONDS.sleep(5);// <------- TO-DO Find a better alternative
      
      output = new String(baos.toByteArray());
      System.out.println("here:" + output);
      
  
    }
    catch(Exception e){
      System.out.println(e);
    }
    return output;
  }
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
}