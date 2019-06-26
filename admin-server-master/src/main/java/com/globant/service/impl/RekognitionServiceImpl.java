package com.globant.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.model.ContentModerationDetection;
import com.amazonaws.services.rekognition.model.ContentModerationSortBy;
import com.amazonaws.services.rekognition.model.GetContentModerationRequest;
import com.amazonaws.services.rekognition.model.GetContentModerationResult;
import com.amazonaws.services.rekognition.model.NotificationChannel;
import com.amazonaws.services.rekognition.model.S3Object;
import com.amazonaws.services.rekognition.model.StartContentModerationRequest;
import com.amazonaws.services.rekognition.model.StartContentModerationResult;
import com.amazonaws.services.rekognition.model.Video;
import com.amazonaws.services.rekognition.model.VideoMetadata;
import com.amazonaws.services.sqs.AmazonSQS;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.globant.domain.VideoModerationJob;
import com.globant.repository.VideoModerationJobRepository;
import com.globant.repository.VideoRepository;
import com.globant.service.RekognitionService;

@Service
public class RekognitionServiceImpl implements RekognitionService {

	private static final String SQS_QUEUE_URL = "https://sqs.ap-south-1.amazonaws.com/589980169295/AmazonRekognitionQueue";
	private static final String ROLE_ARN = "arn:aws:iam::589980169295:role/AwsRekognitionRole";
	private static final String SNS_TOPIC_ARN = "arn:aws:sns:ap-south-1:589980169295:AmazonRekognitionTopic";

	private static final Logger logger = LoggerFactory.getLogger(RekognitionServiceImpl.class);
	private AmazonRekognition rekognitionClient;
	private NotificationChannel channel;
	private AmazonSQS sqsClient;
	private String awsS3VideoBucket;

	@Autowired
	private VideoModerationJobRepository moderationRepository;
	@Autowired
	VideoRepository videoRepository;

	@Autowired
	public RekognitionServiceImpl(AmazonRekognition rekognitionClient, AmazonSQS sqsClient, String awsS3VideoBucket) {
		this.rekognitionClient = rekognitionClient;
		this.sqsClient = sqsClient;
		this.awsS3VideoBucket = awsS3VideoBucket;
		this.channel = new NotificationChannel().withSNSTopicArn(SNS_TOPIC_ARN).withRoleArn(ROLE_ARN);
	}

	public String startRekognition(String videoName, String videoId) {
		logger.info("Started rekognition for video= " + videoName);
		String jobId = "";
		try {
			StartContentModerationRequest req = new StartContentModerationRequest().withMinConfidence(Float.valueOf(50))
					.withVideo(
							new Video().withS3Object(new S3Object().withBucket(awsS3VideoBucket).withName(videoName)))
					.withNotificationChannel(channel);

			StartContentModerationResult startModerationLabelDetectionResult = rekognitionClient
					.startContentModeration(req);
			jobId = startModerationLabelDetectionResult.getJobId();
			logger.info("JobId = " + jobId);
		} catch (AmazonServiceException ex) {
			logger.error("error [" + ex.getMessage() + "] occurred while uploading [" + videoName + "] ");
		}

		if (!StringUtils.isEmpty(jobId)) {
			// save this job to video mapping in database for later use.
			VideoModerationJob videoModerationJob = new VideoModerationJob();
			videoModerationJob.setJobId(jobId);
			videoModerationJob.setVideoId(videoId);
			moderationRepository.save(videoModerationJob);
		}

		return jobId;
	}

	@SqsListener(SQS_QUEUE_URL)
	public void receiveMessage(String message, @Header("SenderId") String senderId,
			@Header("ReceiptHandle") String receiptHandle) {
		logger.info("Recieved message: " + message);
		logger.info("Recieved senderId: " + senderId);
		logger.info("Recieved receiptHandle: " + receiptHandle);
		try {
			JsonNode jsonMessageTree = new ObjectMapper().readTree(message);
			JsonNode msg = jsonMessageTree.get("Message");
			JsonNode jsonResultTree = new ObjectMapper().readTree(msg.textValue());

			JsonNode msgJobId = jsonResultTree.get("JobId");
			String jobId = msgJobId.asText();
			JsonNode msgStatus = jsonResultTree.get("Status");

			logger.debug("Recieved job: " + jobId);
			List<VideoModerationJob> moderationJobList = moderationRepository.findByJobId(jobId);
			if (null != moderationJobList && moderationJobList.size() > 0) {
				logger.debug("Job received found in the videoModeration index: " + jobId);
				if (msgStatus.asText().equals("SUCCEEDED")) {
					List<String> moderationLabels = getResultsModerationLabels(msgJobId.asText());
					Optional<com.globant.domain.Video> existingVideoOptional = videoRepository
							.findById(moderationJobList.get(0).getVideoId());
					if (null != existingVideoOptional.get()) {
						com.globant.domain.Video video = existingVideoOptional.get();
						video.setMSoderationLabels(moderationLabels);
						videoRepository.save(video);
					}
					moderationRepository.delete(moderationJobList.get(0));

				} else {
					logger.debug("Video analysis did not succeed: " + msgStatus);
				}
				sqsClient.deleteMessage(SQS_QUEUE_URL, receiptHandle);
			} else {
				logger.debug("Job received was not found in the videoModeration " + jobId);
				sqsClient.deleteMessage(SQS_QUEUE_URL, receiptHandle);
			}
		} catch (IOException e) {
			logger.debug("Failed to parse message: " + e.getMessage());
		}
	}

	private List<String> getResultsModerationLabels(String jobId) {

		int maxResults = 10;
		String paginationToken = null;
		GetContentModerationResult moderationLabelDetectionResult = null;
		List<String> moderationLabels = new ArrayList<>();
		do {
			if (moderationLabelDetectionResult != null) {
				paginationToken = moderationLabelDetectionResult.getNextToken();
			}

			moderationLabelDetectionResult = rekognitionClient.getContentModeration(
					new GetContentModerationRequest().withJobId(jobId).withNextToken(paginationToken)
							.withSortBy(ContentModerationSortBy.TIMESTAMP).withMaxResults(maxResults));

			VideoMetadata videoMetaData = moderationLabelDetectionResult.getVideoMetadata();

			logger.debug("Format: " + videoMetaData.getFormat());
			logger.debug("Codec: " + videoMetaData.getCodec());
			logger.debug("Duration: " + videoMetaData.getDurationMillis());
			logger.debug("FrameRate: " + videoMetaData.getFrameRate());

			// Show moderated content labels, confidence and detection times
			List<ContentModerationDetection> moderationLabelsInFrames = moderationLabelDetectionResult
					.getModerationLabels();

			for (ContentModerationDetection label : moderationLabelsInFrames) {
				String strLabel = label.getModerationLabel().getName();
				if (!moderationLabels.contains(strLabel)) {
					moderationLabels.add(strLabel);
				}
				long seconds = label.getTimestamp() / 1000;
				String labelInfo = "Label: " + label.getModerationLabel().toString() + "Sec: " + seconds;
				logger.info(labelInfo);
			}
		} while (moderationLabelDetectionResult != null && moderationLabelDetectionResult.getNextToken() != null);

		return moderationLabels;
	}
}
