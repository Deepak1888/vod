package com.globant.domain;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@Document(indexName = "video", type = "video", useServerConfiguration = true)
public class Video {

	@Id
	String id;
	private String title;
	private String description;
	private boolean isActive;
	private String videoUrl;
	private String thumbnailUrl;
	private List<String> tags;
	private List<String> categoryList;
	private List<String> moderationLabels;

	@NotNull
	@CreatedDate
	@DateTimeFormat(iso = ISO.DATE_TIME)
	private Date createdDate;

	@NotNull
	@LastModifiedDate
	@DateTimeFormat(iso = ISO.DATE_TIME)
	private Date lastUpdatedDate;

	public Video() {

	}

	public Video(String id, String title, String description, boolean isActive) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.isActive = isActive;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String color) {
		this.description = color;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getLastUpdatedDate() {
		return lastUpdatedDate;
	}

	public void setLastUpdatedDate(Date lastUpdatedDate) {
		this.lastUpdatedDate = lastUpdatedDate;
	}

	public String getVideoUrl() {
		return videoUrl;
	}

	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public List<String> getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(List<String> categoryList) {
		this.categoryList = categoryList;
	}

	public List<String> getModerationLabels() {
		return moderationLabels;
	}

	public void setMSoderationLabels(List<String> labels) {
		this.moderationLabels = labels;
	}

	@Override
	public String toString() {

		return "Video {" + "id='" + id + '\'' + ", title='" + title + '\'' + ", categoryList='" + categoryList
				+ '\'' + ", is_active='" + isActive + '\'' + ", videoURL='" + videoUrl + ", thumbnailURL='"
				+ thumbnailUrl + '\'' + '\'' + ", description='" + description + '\'' + ", tags='" + tags + '\''
				+ ", createdDate='" + createdDate + ", lastUpdatedDate='" + lastUpdatedDate + '\'' + '}';
	}

}
