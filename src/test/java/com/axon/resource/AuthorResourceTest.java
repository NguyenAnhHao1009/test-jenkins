package com.axon.resource;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.axon.repository.AuthorRepository;

import io.quarkus.test.junit.QuarkusTest;
import static io.restassured.RestAssured.given;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@QuarkusTest
public class AuthorResourceTest {


    @Inject
    AuthorRepository authorRepository;



    @BeforeEach
    @Transactional
    void clearDataBase() {
        authorRepository.deleteAll();

    }

    @Test
    public void whenGetAllAuthorWithoutDataThenReturnNoContent(){
        given()
        .when()
            .get("/api/authors")
        .then()
            .statusCode(Response.Status.NO_CONTENT.getStatusCode());  
    }

    @Test
    public void whenGetAllAuthorWithDataThenReturnListOfAuthor(){
        String authorName = "Nguyen Anh";
        given()
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .formParam("name", authorName)
        .when()
            .post("/api/authors")
        .then()
            .statusCode(Response.Status.CREATED.getStatusCode())
            .body("name", equalTo(authorName))
            .body("id", notNullValue());

        given()
        .when()
            .get("/api/authors")
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .body("$", not(empty()))
            .body("[0].name", equalTo(authorName))
            .body("[0].id", notNullValue());
    }
    
    @Test
    @Transactional
    public void whenCreateAnAuthorWithValidNameThenReturnCreated(){

        String authorName = "Nguyen Anh";

        given()
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .formParam("name", authorName)
        .when()
            .post("/api/authors")
        .then()
            .statusCode(Response.Status.CREATED.getStatusCode())
            .body("name", equalTo(authorName))
            .body("id", notNullValue());
    }

    @Test
    @Transactional
    public void whenCreateAnAuthorWithNameContainsOnlySpaceThenRetunBadRequest(){
        given()
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .formParam("name", "   ")
        .when()
        .post("/api/authors")
        .then()
        .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    @Transactional
    public void whenCreateAnAuthorWithUnvalidNameThenRetunBadRequest(){
        given()
        .when()
        .post("/api/authors")
        .then()
        .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
    }

}


