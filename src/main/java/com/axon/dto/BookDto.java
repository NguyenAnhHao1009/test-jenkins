package com.axon.dto;

import java.util.Set;
import java.util.stream.Collectors;

import com.axon.model.Book;

public class BookDto {
    private Long id;
    private String name;
    private Set<AuthorDto> authors;
    private CategoryDto category;

    public BookDto(Book book) {
        this.id = book.getId();
        this.name = book.getName();
        if(book.getAuthors() != null){
            this.authors = book.getAuthors()
                   .stream()
                   .map(AuthorDto::new)
                   .collect(Collectors.toSet());
        }

        if(book.getCategory() != null){
            this.category = new CategoryDto(book.getCategory());
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

   
    public CategoryDto getCategory() {
        return category;
    }

    public void setCategory(CategoryDto category) {
        this.category = category;
    }

    public Set<AuthorDto> getAuthors() {
        return authors;
    }

    public void setAuthors(Set<AuthorDto> authors) {
        this.authors = authors;
    }

    
    
}
