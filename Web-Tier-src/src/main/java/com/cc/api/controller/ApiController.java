package com.cc.api.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.Message;
import com.cc.api.service.SqsServices;

@RestController
public class ApiController {

	final AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
    final String responseQueueUrl = "https://sqs.us-west-1.amazonaws.com/087303647010/cc_proj_receiver2";
    final String bucket_name = "mykeyvaluebucket";
    final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
    @GetMapping("/cloudimagerecognition.php")
	public String getUrl(@RequestParam String input) throws UnsupportedEncodingException {
		// System.out.println(url);
		
		// String input = java.net.URLDecoder.decode(url, "UTF-8");
		// System.out.println(input);
		
		String output = "Timed Out";
		try {
            SqsServices.sendMsg(input);
           
            String[] url_split = input.split("\\/");
            String image = url_split[url_split.length - 1];
            
            long startTime = System.currentTimeMillis(); //fetch starting time
            while((System.currentTimeMillis()-startTime)<240000)
            {
                List<Message> messages = sqs.receiveMessage(responseQueueUrl).getMessages();

                for (Message m : messages) {
                    String response = m.getBody().toString();
                    String responseSplit[] = response.split("\\|\\|");
                    if (response.split("\\|\\|")[0].equals(image)) {
                        sqs.deleteMessage(responseQueueUrl, m.getReceiptHandle());
                        if (responseSplit.length <=1){
                        	return " ";
                        }
                        else {
                        return response.split("\\|\\|")[1];
                    }
                    }
                }/*
            	try {
            		S3Object object = s3.getObject(bucket_name, image);
            		if(object!=null) {
                		//InputStream objectData = object.getObjectContent();
                		BufferedReader reader = new BufferedReader(new InputStreamReader(object.getObjectContent()));
                	    String line;
                	    while((line = reader.readLine()) != null) {
                	      // can copy the content locally as well
                	      // using a buffered writer
                	      return line;
                	    }
            		}
            	}catch(AmazonS3Exception e){
            		TimeUnit.SECONDS.sleep(1);
            	}*/
                    	
               	// Process the objectData stream.
                TimeUnit.SECONDS.sleep(1);
                
            }
        } catch (Exception e) {
            System.out.println(e);
           
        }
		return output;
	}
	

}
