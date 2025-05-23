package com.axon.resource;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.jboss.logging.Logger;

import com.axon.dto.BookDto;
import com.axon.model.Author;
import com.axon.model.Book;
import com.axon.model.Category;
import com.axon.repository.AuthorRepository;
import com.axon.repository.BookRepository;
import com.axon.repository.CategoryRepository;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/books")
@Produces(MediaType.APPLICATION_JSON)
public class BookResource {
    

    @Inject
    BookRepository bookRepository;

    @Inject
    CategoryRepository categoryRepository;

    @Inject
    AuthorRepository authorRepository;

    @Inject
    Logger logger;

    @GET
    public Response getAllBook() {

        List<BookDto> allBooks = bookRepository.listAll()
            .stream()
            .map(book -> new BookDto(book))
            .collect(Collectors.toList());

        if(allBooks.isEmpty()){
            return Response.status(Response.Status.NO_CONTENT).build();
        }
        
        logger.info(allBooks.get(0));

        if(allBooks.isEmpty()){
            return Response.status(Response.Status.NO_CONTENT).build();
        }

        return Response.ok(allBooks).build();

    }

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response createANewBook(
        @FormParam("name") String name, 
        @FormParam("authorIds") String authorIds,
        @FormParam("categoryId") Long categoryId
        ){

            if(name == null || authorIds==null || categoryId==null){
                 return Response.status(Response.Status.BAD_REQUEST).entity("Missing field").build();
            }

        
        Category category = categoryRepository.findById(categoryId);

        if(category == null){
            return Response.status(Response.Status.NOT_FOUND).entity("Not found category").build();
        }


        if(name == null || name.trim().isEmpty() || authorIds == null || authorIds.isEmpty()){
            return Response.status(Response.Status.BAD_REQUEST)
            .entity("Name and author cannot be empty")
            .build();
        }

        Set<Author> authors = getAuthorsFromAuthorIds(authorIds);

        if(authors==null){
            return Response.status(Response.Status.BAD_REQUEST)
            .entity("Some problem with the author Ids")
            .build();
        }
        
        Book book = new Book();
        book.setName(name);
        book.setCategory(category);
        book.setAuthors(authors);
        bookRepository.persist(book);

        return Response.status(Response.Status.CREATED)
        .entity(new BookDto(book))
        .build();
    }


    @GET
    @Path("/{id}")
    public Response getABook(@PathParam("id") Long id){

        Book book = bookRepository.findById(id);

        if(book == null){
            return Response.status(Response.Status.NO_CONTENT).entity("Not found Book").build();
        }

        return Response.ok(new BookDto(book)).build();
    }

    @DELETE
    @Transactional
    @Path("/{id}")
    public Response deleteABook(@PathParam("id") Long id){
        
        Book book = bookRepository.findById(id);

        if(book == null){
            return Response.status(Response.Status.NOT_FOUND).entity("Book not found").build();
        }

        boolean deleted = bookRepository.deleteById(id);

        if(deleted){
            return Response.ok("Successfull deleted").build();
        }else{
            return Response.serverError().entity("Failed to delete book").build();
        }   
    }

    @PATCH
    @Transactional
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response editABook(
        @PathParam("id") Long id, 
        @FormParam("name") String name, 
        @FormParam("authorIds") String authorIds,
        @FormParam("categoryId") Long categoryId){

        Book book = bookRepository.findById(id);

        if(book == null){
            return Response.status(Response.Status.NOT_FOUND).entity("Book not found").build();
        }

        if(categoryId != null){
            Category category = categoryRepository.findById(categoryId);

            if(category == null){
                return Response.status(Response.Status.NOT_FOUND).entity("Category not found").build();
            }else{
                book.setCategory(category);
            }
        
        }

        if(name != null){
            book.setName(name);
        }

        if(authorIds != null){
            Set<Author> authors = getAuthorsFromAuthorIds(authorIds);

            if(authors==null){
                return Response.status(Response.Status.BAD_REQUEST)
                .entity("Some problem with the author Ids")
                .build();
            }else{
                book.setAuthors(authors);
            }


        }


        return Response.ok(new BookDto(book)).build();


    }

    Set<Author> getAuthorsFromAuthorIds(String authorIds){
        Set<Author> authors = new HashSet<>();

        for(String idStr : authorIds.split(",")){
             try {
            Long authorId = Long.valueOf(idStr.trim());
            Author author = authorRepository.findById(authorId);
            if (author != null) {
                authors.add(author);
            } else {
                return null;
            }
            } catch (NumberFormatException e) {
                return null;
            }
        }

        return authors;
    }

}
