/**
 * 
 */
package com.globant.repository;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import com.globant.domain.VideoModerationJob;

/**
 * @author mangesh.pendhare
 *
 */
@Repository
public interface VideoModerationJobRepository  extends ElasticsearchRepository<VideoModerationJob, String>{
	public List<VideoModerationJob> findByJobId(String jobId);
	public void deleteByVideoId(String videoId);
}
