package com.globant.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import com.globant.domain.Video;

@Repository
public interface VideoRepository extends ElasticsearchRepository<Video, String> {

	public Optional<Video> findById(String id);

	public List<Video> findByCategoryList(String categoryList);

	// @Query(sort = "{lastUpdatedDate : -1}")
	public List<Video> findAllByOrderByLastUpdatedDateDesc();
	
	public List<Video> findAllByIsActiveOrderByLastUpdatedDateDesc(boolean isActive);
}
