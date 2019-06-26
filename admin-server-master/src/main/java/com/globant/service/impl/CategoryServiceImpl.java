/**
 * 
 */
package com.globant.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.globant.domain.Category;
import com.globant.repository.CategoryRepository;
import com.globant.service.CategoryService;

/**
 * @author mangesh.pendhare
 *
 */
@Service
public class CategoryServiceImpl implements CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;

	@Override
	public Category createCategory(Category category) {
		return categoryRepository.save(category);
	}

	@Override
	public List<Category> createAll(List<Category> categories) {
		return (List<Category>) categoryRepository.saveAll(categories);
	}

	@Override
	public Category update(String id, Category category) {
		category.setId(id);
		return categoryRepository.save(category);
	}

	@Override
	public void deleteAll() {
		categoryRepository.deleteAll();
	}

	@Override
	public String deleteById(String id) {
		categoryRepository.deleteById(id);
		return "Category deleted.";
	}

	@Override
	public Optional<Category> getById(String id) {
		Optional<Category> existingCategory = categoryRepository.findById(id);
		return existingCategory;
	}

	@Override
	public Iterable<Category> findAll(Pageable pageable) {
		return categoryRepository.findAll(pageable).getContent();
	}

}
