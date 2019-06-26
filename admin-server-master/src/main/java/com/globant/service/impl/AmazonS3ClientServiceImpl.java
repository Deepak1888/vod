/**
 * 
 */
package com.globant.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.globant.service.AmazonS3ClientService;

/**
 * @author mangesh.pendhare
 *
 */

@Component
public class AmazonS3ClientServiceImpl implements AmazonS3ClientService {
	private String awsS3VideoBucket;
	private AmazonS3 amazonS3;
	private static final Logger logger = LoggerFactory.getLogger(AmazonS3ClientServiceImpl.class);

	@Autowired
	public AmazonS3ClientServiceImpl(Region awsRegion, AWSCredentialsProvider awsCredentialsProvider,
			String awsS3VideoBucket) {
		this.amazonS3 = AmazonS3ClientBuilder.standard().withCredentials(awsCredentialsProvider)
				.withRegion(awsRegion.getName()).build();
		this.awsS3VideoBucket = awsS3VideoBucket;
	}

	@Async
	public String uploadFileToS3Bucket(MultipartFile multipartFile) {
		String fileName = multipartFile.getOriginalFilename();//"_" + System.currentTimeMillis() + "_" + 
		String s3FileUrl = "";
		try {
			// creating the file in the server (temporarily)
			File file = new File(fileName);
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(multipartFile.getBytes());
			fos.close();

			PutObjectRequest putObjectRequest = new PutObjectRequest(this.awsS3VideoBucket, fileName, file);
			putObjectRequest.withCannedAcl(CannedAccessControlList.PublicRead);
			PutObjectResult putObjectResponse = this.amazonS3.putObject(putObjectRequest);
			ObjectMetadata metadata = putObjectResponse.getMetadata();
			// removing the file created in the server
			file.delete();
			s3FileUrl = "https://s3." + this.amazonS3.getRegion() + ".amazonaws.com/" + this.awsS3VideoBucket + "/"
					+ fileName;
		} catch (IOException | AmazonServiceException ex) {
			logger.error("error [" + ex.getMessage() + "] occurred while uploading [" + fileName + "] ");
		}
		return s3FileUrl;
	}

	@Async
	public void deleteFileFromS3Bucket(String fileName) {
		try {
			amazonS3.deleteObject(new DeleteObjectRequest(awsS3VideoBucket, fileName));
		} catch (AmazonServiceException ex) {
			logger.error("error [" + ex.getMessage() + "] occurred while removing [" + fileName + "] ");
		}
	}
}
