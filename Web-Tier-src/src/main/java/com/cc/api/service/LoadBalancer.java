package com.cc.api.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import org.springframework.stereotype.Component;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.GetQueueAttributesRequest;

@Component
public class LoadBalancer implements Runnable {

	
	final AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
	// private static int currentRunningInstances = 1;
	private static String requestQueueUrl = "https://sqs.us-west-1.amazonaws.com/087303647010/cc_proj_sender1";
	private int requestQueueLength = 0;

	public LoadBalancer() {

		// AwsInstanceService.startinstance(instanceID);
	}

	public void run() {
		System.out.println("#######################   Load Balancer Started #######################");

		while (true) {
			try {

				GetQueueAttributesRequest request = new GetQueueAttributesRequest(requestQueueUrl)
						.withAttributeNames("ApproximateNumberOfMessages");
				Map<String, String> attributes = sqs.getQueueAttributes(request).getAttributes();
				requestQueueLength = Integer.parseInt(attributes.get("ApproximateNumberOfMessages"));
				System.out.println("#######################  Number of msg Aprox #######################");
				System.out.println(requestQueueLength);
				

				Queue<String> runningInstances = new LinkedList<>();
				// Queue<String> stoppedInstances = new LinkedList<>();
				final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();
				DescribeInstancesResult describeInstancesResult = ec2.describeInstances();
				List<Reservation> reservations = describeInstancesResult.getReservations();

				List<Instance> listOfInstances = new ArrayList<>();
				for (Reservation reservation : reservations)
					listOfInstances.addAll(reservation.getInstances());

				for (Instance instance : listOfInstances) {
					if (instance.getInstanceId().equals("i-046121b4e494562ed")) {
						System.out.println("#############################Cont###################################");
						continue;

					}

					if (instance.getState().getName().equals("running")
							|| instance.getState().getName().equals("pending") || instance.getState().getName().equals("shutting-down"))
						runningInstances.add(instance.getInstanceId());

				}

				System.out.println("############################# Run inst" + Integer.toString(runningInstances.size())
						+ "#############################################");

				if (runningInstances.size() < requestQueueLength) {
					for (int i = runningInstances.size(); i < Math.min(requestQueueLength, 19); i++) {
						// String instanceID = stoppedInstances.poll();
						System.out.println(
								"###############################Creating Instance ##################################");
						Instance instance = AwsInstanceService.createinstance();
						
						try {
							Collection<Tag> tags = new ArrayList<Tag>();
							Tag t = new Tag();
							t.setKey("Name");
							t.setValue("App-Instance-" + String.valueOf(System.currentTimeMillis()));
							tags.add(t);
							CreateTagsRequest createTagsRequest = new CreateTagsRequest();
							createTagsRequest.withTags(tags);
							createTagsRequest.withResources(instance.getInstanceId());
							ec2.createTags(createTagsRequest);
							//Thread.sleep(10 * 1000);
							String instanceID = instance.getInstanceId();
							runningInstances.add(instanceID);
						} catch (Exception e) {
							System.out.println("Error in setting name tags");
							e.printStackTrace();
						}
					}

				} /*
					 * if(currentRunningInstances>requestQueueLength) {
					 * 
					 * String instanceID = prop.getHost(currentRunningInstances);
					 * AwsInstanceService.stopinstance(instanceID); currentRunningInstances--; }
					 */
				Thread.sleep(3 * 1000);
			} catch (Exception e) {
				System.out.println("################################ Error in load balancer ");
				e.printStackTrace();
			}
		}

	}

}
