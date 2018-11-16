package com.cc.api.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.Message;

import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import java.util.logging.LogManager;

public class receiver {
	
	// Instance terminates automatically once request Q is empty
	public static void terminateInstance() {
		
		final String command_getInstanceId[] = { "/bin/bash", "-c", "wget -q -O - http://instance-data/latest/meta-data/instance-id;" };
		final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();
		String Ins_output = "";
		String s = "";
		
		try {
			
			// command to get current Instance ID
			Process t = Runtime.getRuntime().exec(command_getInstanceId);
			t.waitFor();
			BufferedReader br = new BufferedReader(new InputStreamReader(t.getInputStream()));
			while ((s = br.readLine()) != null) {
				Ins_output = s;
			}
			t.destroy();
			
			// terminate current instance from self
			TerminateInstancesRequest tir = new TerminateInstancesRequest().withInstanceIds(Ins_output);
			ec2.terminateInstances(tir);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws InterruptedException {

		final String cmd1 = "source /home/ubuntu/tensorflow/bin/activate;";
		final String cmd2 = "cd /home/ubuntu/tensorflow/models/tutorials/image/imagenet;";
		final String cmd3 = "python classify_image.py --image_file ";
		final String cmd4 = " --num_top_predictions 1;";
		final AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
		final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();	
		final String bucket_name = "mykeyvaluebucket";
		String s;
		String output = "";

		String requestQueueUrl = "https://sqs.us-west-1.amazonaws.com/087303647010/cc_proj_sender1";
		String responseQueueUrl = "https://sqs.us-west-1.amazonaws.com/087303647010/cc_proj_receiver2";
		
		// Log manager
		{
			LogManager.getLogManager().reset();
		    Set<String> loggers = new HashSet<>(Arrays.asList("org.apache.http", "groovyx.net.http","com.amazonaws"));
		       
		    for(String log:loggers) {
		       Logger logger = (Logger)LoggerFactory.getLogger(log);
		       logger.setLevel(Level.WARN);
		       logger.setAdditive(false);
		    }
		}
		
		// output from image recognition script
		String imageRecognitionOutput = "";
		
		// URL picked from request Q
		String Url = "";
		
		// get image name from given Url
		String[] s3ProcessedUrl;

		while (true) {
			
			List<Message> messages = sqs.receiveMessage(requestQueueUrl).getMessages();
			
			// if no message received, terminate the instance
			if(messages.isEmpty()) {
				terminateInstance();
			}
			
			// do below for each message received from request Q
			for (Message m : messages) {
				
				Url = m.getBody().toString();
				
				// command to run image recognition
				String command_img_recog[] = { "/bin/bash", "-c", cmd1 + cmd2 + cmd3 + Url + cmd4};
				
				// App Instance to run image recognition script and return output
				try {
		            Process p = Runtime.getRuntime().exec(command_img_recog);
		            p.waitFor();
		            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
		            while ((s = br.readLine()) != null) {
		            		output = s;
		            }
		            p.destroy();
		        } catch (Exception e) {
		        		terminateInstance();
		        }
				
				// get image name
				s3ProcessedUrl = Url.split("\\/");

				// get output without score value
				imageRecognitionOutput = output.split("\\(")[0];
				
				// put [imageID, output] into S3
				try {
					s3.putObject(bucket_name, s3ProcessedUrl[s3ProcessedUrl.length - 1], imageRecognitionOutput);
				} catch (Exception e) {
					terminateInstance();
				}
				
				// delete message from request Q
				try {
					sqs.deleteMessage(requestQueueUrl, m.getReceiptHandle());
				} catch (Exception e){
					terminateInstance();
				}
				
				// publish message to response Q
				try {
					SendMessageRequest send_msg_request = new SendMessageRequest()
							.withQueueUrl(responseQueueUrl)
							.withMessageBody(s3ProcessedUrl[s3ProcessedUrl.length - 1] + "||" + imageRecognitionOutput);
					
					sqs.sendMessage(send_msg_request);
				} catch (Exception e) {
					terminateInstance();
				}	
			}
		}
	}
}
