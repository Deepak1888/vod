package com.globant.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import com.globant.domain.Video;
import com.globant.repository.VideoRepository;
import com.globant.service.VideoService;

@Service
public class VideoServiceImpl implements VideoService {

	@Autowired
	private VideoRepository videoRepository;

	// Create Video operation
	public Video createVideo(Video video) {
		video.setCreatedDate(new Date());
		video.setLastUpdatedDate(new Date());
		return videoRepository.save(video);
	}

	public List<Video> createAll(List<Video> videos) {
		if(null!=videos && videos.size()>0) {
			for(Video video:videos) {
				video.setCreatedDate(new Date());
				video.setLastUpdatedDate(new Date());
			}
		}
		return (List<Video>) videoRepository.saveAll(videos);
	}
	
	public Video update(String id, Video updatedVideo) {
		Optional<Video> existingVideo = videoRepository.findById(id);
		// set createdDate if not present in new video
		if (null != existingVideo && null != existingVideo.get()) {
			updatedVideo.setId(id);
			// preserve the date created
			updatedVideo.setCreatedDate(existingVideo.get().getCreatedDate());
			updatedVideo.setLastUpdatedDate(new Date());
		}
		return videoRepository.save(updatedVideo);
	}
	
	@Override
	public Video approve(String id) {
		Optional<Video> existingVideo = videoRepository.findById(id);
		if (null != existingVideo && null != existingVideo.get()) {
			existingVideo.get().setLastUpdatedDate(new Date());
			existingVideo.get().setIsActive(true);
			videoRepository.save(existingVideo.get());
		}
		return existingVideo.get();
	}
	
	@Override
	public Video unapprove(String id) {
		Optional<Video> existingVideo = videoRepository.findById(id);
		if (null != existingVideo && null != existingVideo.get()) {
			existingVideo.get().setLastUpdatedDate(new Date());
			existingVideo.get().setIsActive(false);
			videoRepository.save(existingVideo.get());
		}
		return existingVideo.get();
	}

	// Delete operation
	public void deleteAll() {
		videoRepository.deleteAll();
	}

	public String deleteById(String id) {
		videoRepository.deleteById(id);
		return "Video deleted.";
	}

	public Optional<Video> getById(String id) {
		Optional<Video> existingVideo = videoRepository.findById(id);
		return existingVideo;
	}

	@Override
	public Iterable<Video> findAll(Pageable pageable) {
		PageRequest pageRequest = new PageRequest(0, 40);
		return videoRepository.findAll(pageRequest ).getContent();
	}

	@Override
	public Iterable<Video> findAllActive(Pageable pageable) {
		return videoRepository.findAllByIsActiveOrderByLastUpdatedDateDesc(true);
	}

	@Override
	public Iterable<Video> findAllInActive(Pageable pageable) {
		return videoRepository.findAllByIsActiveOrderByLastUpdatedDateDesc(false);
	}

	@Override
	public Page<Video> search(String searchTerm, Pageable pageable) {
		SearchQuery searchQuery = new NativeSearchQueryBuilder().withIndices("video").withQuery(
				QueryBuilders.matchQuery("title",searchTerm)).build();
		// new NativeSearchQueryBuilder().withIndices(INDEX)
		return videoRepository.search(searchQuery);
		// return videoRepository.search(searchQuery);
	}

}
