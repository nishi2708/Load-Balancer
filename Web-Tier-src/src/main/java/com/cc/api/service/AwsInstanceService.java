package com.cc.api.service;
import org.apache.commons.codec.binary.Base64;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.springframework.stereotype.Component;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.IamInstanceProfileSpecification;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
//import com.amazonaws.services.ec2.model.StartInstancesRequest;
//import com.amazonaws.services.ec2.model.StopInstancesRequest;
//import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
//import java.util.Properties;




@Component
public class AwsInstanceService {
	
	public static Instance createinstance() {
		
final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();	
		
		System.out.println("create an instance");
		Instance instance = null;

		String imageId = "ami-a8baacc8";  //image id of the instance
		int minInstanceCount = 1; //create 1 instance
		int maxInstanceCount = 1;

		try {
			IamInstanceProfileSpecification prof = new IamInstanceProfileSpecification();
			prof.setName("sqs_s3");
			RunInstancesRequest rir = new RunInstancesRequest(imageId, 
					minInstanceCount, maxInstanceCount);
			rir.setInstanceType("t2.micro"); //set instance type
			rir.setKeyName("CC-key");
			rir.withIamInstanceProfile(prof);
			rir.setUserData(getECSuserData());
			RunInstancesResult result = ec2.runInstances(rir);
			
			List<Instance> resultInstance = 
					result.getReservation().getInstances();
			
			for(Instance ins : resultInstance) {
				
				instance = ins;
			}
		} catch (Exception e) {
			System.out.println("Error in creating instance............");
			e.printStackTrace();
		}
		return instance;
	}
	
//	public static void startinstance(String instanceId) {
//		final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();	
//		StartInstancesRequest request = new StartInstancesRequest().
//				withInstanceIds(instanceId);//start instance using the instance id
//		ec2.startInstances(request);
//
//	}
//	
//	public static void stopinstance(String instanceId) {
//		final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();	
//		StopInstancesRequest request = new StopInstancesRequest().
//				withInstanceIds(instanceId);//stop instance using the instance id
//		ec2.stopInstances(request);
//
//	}
//	
//	public static void terminateinstance(String instanceId) {
//		final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();	
//		TerminateInstancesRequest request = new TerminateInstancesRequest().
//				withInstanceIds(instanceId);//terminate instance using the instance id
//		ec2.terminateInstances(request);

//	}
	
	private static String getECSuserData() {
		   String userData = "#!/bin/bash\n";
		   userData = userData + "java -jar /home/ubuntu/receiverNew1.jar &";
		   String base64UserData = null;
		   try {
		       base64UserData = new String( Base64.encodeBase64( userData.getBytes( "UTF-8" )), "UTF-8" );
		   } catch (UnsupportedEncodingException e) {
		       e.printStackTrace();
		   }
		   return base64UserData;
		}
	
//	public static void main(String[] args) {
//		PropertiesService prop = new PropertiesService();
//		System.out.println(prop.getUser());
//		
//}
}
