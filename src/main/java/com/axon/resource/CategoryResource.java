package com.axon.resource;

import java.util.List;

import com.axon.model.Category;
import com.axon.repository.CategoryRepository;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/categories")
public class CategoryResource {
    
    @Inject
    CategoryRepository categoryRepository;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCategory(){
        List<Category> allCategories = categoryRepository.listAll();
        
        if(allCategories.isEmpty()){
            return Response.status(Response.Status.NO_CONTENT).build();
        }
        return Response.ok(allCategories).build();
    }

    @POST
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response createACategory(@FormParam("categoryName") String categoryName){

        if(categoryName == null || categoryName.isEmpty() || categoryName.isBlank()){
            return Response.status(Response.Status.BAD_REQUEST).entity("The name must not be empty").build();
        }


        Category category = new Category();
        category.setName(categoryName);

        categoryRepository.persist(category);

        return Response.ok(category).build();
    }
}
