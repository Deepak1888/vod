/**
 * 
 */
package com.globant.domain;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * @author mangesh.pendhare
 *
 */
@Document(indexName = "category")
public class Category {
	@Id
	String id;
	private String name;
	private String description;
	//private List<String> childrenCategoryIds;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

//	public List<String> getChildrenCategoryIds() {
//		return childrenCategoryIds;
//	}
//
//	public void setChildrenCategoryIds(List<String> childrenCategoryIds) {
//		this.childrenCategoryIds = childrenCategoryIds;
//	}

}
