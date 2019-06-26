package com.globant.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.globant.domain.Video;
import com.globant.model.CommonResponse;
import com.globant.service.AmazonS3ClientService;
import com.globant.service.RekognitionService;
import com.globant.service.VideoService;

@RestController
@RequestMapping("/admin")
public class VideoController {

	@Autowired
	private VideoService videoService;

	@Autowired
	private AmazonS3ClientService amazonS3ClientService;

	@Autowired
	private RekognitionService rekognitionService;

	@PostMapping(value = "/video")
	public ResponseEntity<CommonResponse> create(@RequestParam String title, @RequestParam String description,
			@RequestParam List<String> tags, @RequestParam List<String> categoryList,
			@RequestPart(value = "file") MultipartFile file) {
		Video video = new Video();
		video.setTitle(title);
		video.setDescription(description);
		video.setIsActive(false);
		video.setTags(tags);
		video.setCategoryList(categoryList);
		String videoUrl = this.amazonS3ClientService.uploadFileToS3Bucket(file);
		// TODO: create thumbnail image as well
		video.setVideoUrl(videoUrl);
		Video createdVideo = videoService.createVideo(video);

		// submit video to rekognition to get content moderation data
		String fileName = videoUrl.substring(videoUrl.lastIndexOf("/") + 1, videoUrl.length());
		rekognitionService.startRekognition(fileName, createdVideo.getId());

		return new ResponseEntity<CommonResponse>(new CommonResponse(CommonResponse.SUCCESS, createdVideo),
				HttpStatus.CREATED);
	}

	// For create without video upload
	@PostMapping(value = "/video/create")
	public ResponseEntity<CommonResponse> create(@RequestBody Video video) {
		Video createdVideo = videoService.createVideo(video);
		return new ResponseEntity<CommonResponse>(new CommonResponse(CommonResponse.SUCCESS, createdVideo),
				HttpStatus.OK);
	}

	// For create without video upload
	@PostMapping(value = "/video/createAll")
	public ResponseEntity<CommonResponse> createAll(@RequestBody List<Video> videos) {
		List<Video> newVideos = videoService.createAll(videos);
		return new ResponseEntity<CommonResponse>(new CommonResponse(CommonResponse.SUCCESS, newVideos), HttpStatus.OK);
	}

	@PutMapping(value = "/video/{id}/update")
	public ResponseEntity<CommonResponse> update(@PathVariable("id") String id, @RequestParam String title,
			@RequestParam(required = false) boolean isActive, @RequestParam String description,
			@RequestParam List<String> tags, @RequestParam List<String> categoryList,
			@RequestPart(value = "file") MultipartFile file) {
		Optional<Video> videoOpt = videoService.getById(id);
		if (null != videoOpt.get()) {
			Video video = videoOpt.get();
			video.setTitle(title);
			video.setDescription(description);
			video.setIsActive(isActive);
			video.setTags(tags);
			video.setCategoryList(categoryList);
			String videoUrl = this.amazonS3ClientService.uploadFileToS3Bucket(file);
			// TODO: create thumbnail image as well
			video.setVideoUrl(videoUrl);
			// reset moderation labels
			video.setMSoderationLabels(null);
			Video createdVideo = videoService.update(id, video);
			// submit video to rekognition to get content moderation data
			String fileName = videoUrl.substring(videoUrl.lastIndexOf("/") + 1, videoUrl.length());
			rekognitionService.startRekognition(fileName, id);
			return new ResponseEntity<CommonResponse>(new CommonResponse(CommonResponse.SUCCESS, createdVideo),
					HttpStatus.CREATED);
		} else {
			return new ResponseEntity<CommonResponse>(
					new CommonResponse(CommonResponse.FAILURE, "No video found for the update."), HttpStatus.NOT_FOUND);
		}

	}

	@PutMapping(value = "/video/{id}")
	public ResponseEntity<CommonResponse> update(@PathVariable("id") String id, @RequestBody Video Video) {
		return new ResponseEntity<CommonResponse>(
				new CommonResponse(CommonResponse.SUCCESS, videoService.update(id, Video)), HttpStatus.OK);
	}

	@PutMapping(value = "/video/{id}/approve")
	public ResponseEntity<CommonResponse> approve(@PathVariable("id") String id) {
		return new ResponseEntity<CommonResponse>(new CommonResponse(CommonResponse.SUCCESS, videoService.approve(id)),
				HttpStatus.OK);
	}

	@PutMapping(value = "/video/{id}/unapprove")
	public ResponseEntity<CommonResponse> unapprove(@PathVariable("id") String id) {
		return new ResponseEntity<CommonResponse>(
				new CommonResponse(CommonResponse.SUCCESS, videoService.unapprove(id)), HttpStatus.OK);
	}

	@DeleteMapping(value = "/video/{id}")
	public ResponseEntity<CommonResponse> delete(@PathVariable("id") String id) {
		Optional<Video> existingVideo = videoService.getById(id);
		String videoUrl = existingVideo.get().getVideoUrl();
		if (!StringUtils.isEmpty(videoUrl)) {
			String fileName = videoUrl.substring(videoUrl.lastIndexOf("/") + 1, videoUrl.length());
			// amazonS3ClientService.deleteFileFromS3Bucket(fileName);
		}
		String msg = videoService.deleteById(id);

		return new ResponseEntity<CommonResponse>(new CommonResponse(CommonResponse.SUCCESS, msg), HttpStatus.OK);
	}

	@DeleteMapping(value = "/video")
	public ResponseEntity<CommonResponse> deleteAll() {
		String msg = "Deleted all videos";
		videoService.deleteAll();
		return new ResponseEntity<CommonResponse>(new CommonResponse(CommonResponse.SUCCESS, msg), HttpStatus.OK);
	}

	@GetMapping(value = "/video/{id}")
	public ResponseEntity<CommonResponse> getVideoById(@PathVariable("id") String id) {
		return new ResponseEntity<CommonResponse>(new CommonResponse(CommonResponse.SUCCESS, videoService.getById(id)),
				HttpStatus.OK);
	}

	/**
	 * Method to start aws video rekognition to get the moderation labels.
	 * 
	 * @param id
	 * @return
	 */
	@GetMapping(value = "/video/{id}/analyze")
	public ResponseEntity<CommonResponse> analyzeVideoById(@PathVariable("id") String id) {
		Optional<Video> existingVideo = videoService.getById(id);
		String videoUrl = existingVideo.get().getVideoUrl();
		if (!StringUtils.isEmpty(videoUrl)) {
			String fileName = videoUrl.substring(videoUrl.lastIndexOf("/") + 1, videoUrl.length());
			rekognitionService.startRekognition(fileName, id);
		}

		return new ResponseEntity<CommonResponse>(new CommonResponse(CommonResponse.SUCCESS, "Analysis Started."),
				HttpStatus.OK);
	}

	/**
	 * Get all videos
	 * 
	 * @param pageable
	 * @return
	 */
	@GetMapping(value = "/video")
	public ResponseEntity<CommonResponse> getAll(Pageable pageable) {
		return new ResponseEntity<CommonResponse>(
				new CommonResponse(CommonResponse.SUCCESS, videoService.findAll(pageable)), HttpStatus.OK);
	}

	/**
	 * Get all active videos to show to end user
	 * 
	 * @param pageable
	 * @return
	 */
	@GetMapping(value = "/video/_active")
	public ResponseEntity<CommonResponse> getAllActive(Pageable pageable) {
		return new ResponseEntity<CommonResponse>(
				new CommonResponse(CommonResponse.SUCCESS, videoService.findAllActive(pageable)), HttpStatus.OK);
	}

	/**
	 * Get all inactive videos to be approved by admin approver
	 * 
	 * @param pageable
	 * @return
	 */
	@GetMapping(value = "/video/_inactive")
	public ResponseEntity<CommonResponse> getAllInActive(Pageable pageable) {
		return new ResponseEntity<CommonResponse>(
				new CommonResponse(CommonResponse.SUCCESS, videoService.findAllInActive(pageable)), HttpStatus.OK);
	}

	@PostMapping(value = "/search")
	public ResponseEntity<CommonResponse> search(@RequestParam String searchTerm, Pageable pageable) {
		return new ResponseEntity<CommonResponse>(
				new CommonResponse(CommonResponse.SUCCESS, videoService.search(searchTerm, pageable)), HttpStatus.OK);
	}

}