package com.axon.dto;

import com.axon.model.Author;

public class AuthorDto {
    private Long id;
    private String name;

    public AuthorDto(Author author){
        this.id = author.getId();
        this.name = author.getName();
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



}
