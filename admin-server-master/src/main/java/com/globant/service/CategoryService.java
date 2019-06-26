/**
 * 
 */
package com.globant.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;

import com.globant.domain.Category;

/**
 * @author mangesh.pendhare
 *
 */
public interface CategoryService {

	Category createCategory(Category category);

	Category update(String id, Category category);

	void deleteAll();

	String deleteById(String id);

	Optional<Category> getById(String id);
	
	Iterable<Category> findAll(Pageable pageable);

	List<Category> createAll(List<Category> categories);
}
