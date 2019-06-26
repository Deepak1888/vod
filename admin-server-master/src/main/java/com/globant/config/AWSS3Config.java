/**
 * 
 */
package com.globant.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

/**
 * @author mangesh.pendhare
 *
 */

@Configuration
public class AWSS3Config {

	@Value("${amazonProperties.bucketName}")
	private String awsS3VideoBucket;

	@Bean(name = "awsS3VideoBucket")
	public String getAwsS3VideoBucket() {
		return awsS3VideoBucket;
	}

	@Bean(name = "awsS3Client")
	@Autowired
	public AmazonS3 getAwsS3Client(AWSCredentialsProvider awsCredentialsProvider, Region awsRegion) {
		return AmazonS3ClientBuilder.standard().withCredentials(awsCredentialsProvider).withRegion(awsRegion.getName())
				.build();
	}
}
