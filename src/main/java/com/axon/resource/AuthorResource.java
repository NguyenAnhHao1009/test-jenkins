package com.axon.resource;

import java.util.List;
import java.util.stream.Collectors;

import com.axon.dto.AuthorDto;
import com.axon.model.Author;
import com.axon.repository.AuthorRepository;

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

@Path("/api/authors")
@Produces(MediaType.APPLICATION_JSON)
public class AuthorResource {

    @Inject
    AuthorRepository authorRepository;
    
    @GET
    public Response getAllAuthors(){

        List<AuthorDto> allAuthors = authorRepository.listAll()
            .stream()
            .map(author -> new AuthorDto(author))
            .collect(Collectors.toList());

        if(allAuthors.isEmpty()){
            return Response.status(Response.Status.NO_CONTENT).build();
        }

        return Response.ok(allAuthors).build();
    }


    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response createAnAuthor(@FormParam("name") String name){

        if(name == null || name.isEmpty() || name.isBlank()){
            return Response.status(Response.Status.BAD_REQUEST).entity("The name must not be empty").build();
        }

        Author author = new Author();
        author.setName(name);

        authorRepository.persist(author);

        return Response.status(Response.Status.CREATED).entity(new AuthorDto(author)).build();
    }


}
