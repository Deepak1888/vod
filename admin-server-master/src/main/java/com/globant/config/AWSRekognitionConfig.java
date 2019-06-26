/**
 * 
 */
package com.globant.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;

@Configuration
public class AWSRekognitionConfig {

	@Autowired
	AWSCredentialsProvider awsCredentialsProvider;
	
	@Autowired
	Region region;

	@Bean(name = "rekognitionClient")
	public AmazonRekognition createRekognitionClient() {
		ClientConfiguration clientConfig = createClientConfiguration();
		return AmazonRekognitionClientBuilder.standard().withClientConfiguration(clientConfig)
				.withCredentials(awsCredentialsProvider).withRegion(region.getName()).build();
	}

	@Bean(name = "sqsClient")
	public AmazonSQS createSQSClient() {
		ClientConfiguration clientConfig = createClientConfiguration();

		return AmazonSQSClientBuilder.standard().withClientConfiguration(clientConfig)
				.withCredentials(awsCredentialsProvider).withRegion(region.getName()).build();
	}

	private static ClientConfiguration createClientConfiguration() {
		ClientConfiguration clientConfig = new ClientConfiguration();
		clientConfig.setConnectionTimeout(30000);
		clientConfig.setRequestTimeout(60000);
		clientConfig.setProtocol(Protocol.HTTPS);
		return clientConfig;
	}

}
