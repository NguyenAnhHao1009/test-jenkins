package com.axon.dto;

import com.axon.model.Category;

public class CategoryDto {
    private Long id;
    private String categoryName;

    public CategoryDto(Category category) {
        this.id = category.getId();
        this.categoryName = category.getName();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }



    
}
