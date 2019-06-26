package com.globant.repository;

import java.util.Optional;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import com.globant.domain.Category;

@Repository
public interface CategoryRepository extends ElasticsearchRepository<Category, String> {

	public Optional<Category> findById(String id);
}
