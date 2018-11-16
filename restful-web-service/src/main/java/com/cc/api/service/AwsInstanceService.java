package com.cc.api.service;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.stereotype.Component;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import java.util.Properties;




@Component
public class AwsInstanceService {
	
	public static Instance createinstance() {
		
		final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();	
		
		System.out.println("create an instance");
		
		String imageId = "ami-07303b67";  //image id of the instance
		int minInstanceCount = 1; //create 1 instance
		int maxInstanceCount = 1;
		
		RunInstancesRequest rir = new RunInstancesRequest(imageId, 
				minInstanceCount, maxInstanceCount);
		rir.setInstanceType("t2.micro"); //set instance type
		rir.setKeyName("test1");
		
		RunInstancesResult result = ec2.runInstances(rir);
		
		List<Instance> resultInstance = 
				result.getReservation().getInstances();
		Instance instance = null;
		for(  Instance ins : resultInstance) {
			
			instance = ins;
		}
		return instance;
	}
	
	public static void startinstance(String instanceId) {
		final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();	
		StartInstancesRequest request = new StartInstancesRequest().
				withInstanceIds(instanceId);//start instance using the instance id
		ec2.startInstances(request);

	}
	
	public static void stopinstance(String instanceId) {
		final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();	
		StopInstancesRequest request = new StopInstancesRequest().
				withInstanceIds(instanceId);//stop instance using the instance id
		ec2.stopInstances(request);

	}
	
	public static void terminateinstance(String instanceId) {
		final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();	
		TerminateInstancesRequest request = new TerminateInstancesRequest().
				withInstanceIds(instanceId);//terminate instance using the instance id
		ec2.terminateInstances(request);

	}
	
	public static void main(String[] args) {
		PropertiesService prop = new PropertiesService();
		System.out.println(prop.getUser());
		
}
}
