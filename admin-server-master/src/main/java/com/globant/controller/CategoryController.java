/**
 * 
 */
package com.globant.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.globant.domain.Category;
import com.globant.model.CommonResponse;
import com.globant.service.CategoryService;

/**
 * @author mangesh.pendhare
 *
 */
@RestController
@RequestMapping("/admin")
public class CategoryController {
	@Autowired
	private CategoryService categoryService;

	@PostMapping(value = "/category")
	public ResponseEntity<CommonResponse> create(@RequestBody Category category) {
		Category newCategory = categoryService.createCategory(category);
		return new ResponseEntity<CommonResponse>(
				new CommonResponse(CommonResponse.SUCCESS, categoryService.update(newCategory.getId(), newCategory)),
				HttpStatus.CREATED);
	}

	// Creating bulk categories
	@PostMapping(value = "/category/createAll")
	public ResponseEntity<CommonResponse> createAll(@RequestBody List<Category> categories) {
		List<Category> newCategories = categoryService.createAll(categories);
		return new ResponseEntity<CommonResponse>(new CommonResponse(CommonResponse.SUCCESS, newCategories),
				HttpStatus.OK);
	}

	@PutMapping(value = "/category/{id}")
	public ResponseEntity<CommonResponse> update(@PathVariable("id") String id, @RequestBody Category Category) {
		return new ResponseEntity<CommonResponse>(
				new CommonResponse(CommonResponse.SUCCESS, categoryService.update(id, Category)), HttpStatus.OK);
	}

	@DeleteMapping(value = "/category/{id}")
	public ResponseEntity<CommonResponse> delete(@PathVariable("id") String id) {
		String msg = categoryService.deleteById(id);
		return new ResponseEntity<CommonResponse>(new CommonResponse(CommonResponse.SUCCESS, msg), HttpStatus.OK);
	}

	@DeleteMapping(value = "/category")
	public ResponseEntity<CommonResponse> deleteAll() {
		categoryService.deleteAll();
		return new ResponseEntity<CommonResponse>(new CommonResponse(CommonResponse.SUCCESS, "Deleted all records"),
				HttpStatus.OK);
	}

	@GetMapping(value = "/category/{id}")
	public ResponseEntity<CommonResponse> getAdById(@PathVariable("id") String id) {
		return new ResponseEntity<CommonResponse>(
				new CommonResponse(CommonResponse.SUCCESS, categoryService.getById(id)), HttpStatus.OK);
	}

	@GetMapping(value = "/category")
	public ResponseEntity<CommonResponse> getAll(Pageable pageable) {
		return new ResponseEntity<CommonResponse>(
				new CommonResponse(CommonResponse.SUCCESS, categoryService.findAll(pageable)), HttpStatus.OK);
	}

}
