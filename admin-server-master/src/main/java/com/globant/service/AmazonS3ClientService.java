/**
 * 
 */
package com.globant.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author mangesh.pendhare
 *
 */
public interface AmazonS3ClientService {
	String uploadFileToS3Bucket(MultipartFile multipartFile);

	void deleteFileFromS3Bucket(String fileName);
}