/**
 * 
 */
package com.globant.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.globant.service.AmazonS3ClientService;

/**
 * @author mangesh.pendhare
 *
 */
@RestController
@RequestMapping("/admin/content/")
public class FileUploadController {

	@Autowired
	private AmazonS3ClientService amazonS3ClientService;

	@PostMapping("/upload")
	public Map<String, String> uploadFile(@RequestPart(value = "file") MultipartFile file) {
		this.amazonS3ClientService.uploadFileToS3Bucket(file);

		Map<String, String> response = new HashMap<>();
		response.put("message", "file [" + file.getOriginalFilename() + "] uploading request submitted successfully.");

		return response;
	}

	@DeleteMapping
	public Map<String, String> deleteFile(@RequestParam("file_name") String fileName) {
		this.amazonS3ClientService.deleteFileFromS3Bucket(fileName);

		Map<String, String> response = new HashMap<>();
		response.put("message", "file [" + fileName + "] removing request submitted successfully.");

		return response;
	}
}