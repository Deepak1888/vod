/**
 * 
 */
package com.globant.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.globant.domain.Video;

/**
 * @author mangesh.pendhare
 *
 */
public interface VideoService {

	Video createVideo(Video video);
	
	List<Video> createAll(List<Video> videos);

	Video update(String id, Video video);
	
	Video approve(String id);
	
	Video unapprove(String id);

	void deleteAll();

	String deleteById(String id);

	Optional<Video> getById(String id);

	Iterable<Video> findAll(Pageable pageable);

	Iterable<Video> findAllActive(Pageable pageable);

	Iterable<Video> findAllInActive(Pageable pageable);

	Page<Video> search(String searchTerm, Pageable pageable);
}
