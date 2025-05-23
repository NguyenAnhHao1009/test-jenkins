package com.axon.resource;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.axon.repository.BookRepository;
import com.axon.repository.CategoryRepository;

import io.quarkus.test.junit.QuarkusTest;
import static io.restassured.RestAssured.given;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import static jakarta.ws.rs.core.Response.Status.NO_CONTENT;
import static jakarta.ws.rs.core.Response.Status.OK;

@QuarkusTest
public class CategoryResourceTest {

    @Inject
    CategoryRepository categoryRepository;

    @Inject
    BookRepository bookRepository;

    @BeforeEach
    @Transactional
    void clearDb() {
        bookRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    public void whenGetAllCategoryWithoutDataThenReturnNoContent() {
        given()
        .when()
            .get("/api/categories")
        .then()
            .statusCode(NO_CONTENT.getStatusCode());
    }
   
    @Test
    public void whenGetAllCategoryWithDataThenReturnListOfCategory() {
        String categoryName = "Science";
        given()
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .formParam("categoryName", categoryName)
        .when()
            .post("/api/categories")
        .then()
            .statusCode(OK.getStatusCode());

    
        given()
        .when()
            .get("/api/categories")
        .then()
            .statusCode(OK.getStatusCode())
            .body("$", not(empty()))
            .body("[0].name", equalTo(categoryName))
            .body("[0].id", notNullValue());
    }

    @Test
    public void givenCategoryNameWithAllOfSpaceWhenCreateACategoryThenReturnBadRequest(){
        given()
        .formParam("categoryName", "       ")
        .when()
        .post("/api/categories")
        .then()
        .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void notGivenCategoryNameWhenCreateACategoryThenReturnBadRequest(){
        given()
        .when()
        .post("/api/categories")
        .then()
        .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void whenCreateCategoryWithValidCategoryNameThenReturnCreatedCategory() {
        String categoryName = "Science";

        given()
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .formParam("categoryName", categoryName)
        .when()
            .post("/api/categories")
        .then()
            .statusCode(OK.getStatusCode())
            .body("name", equalTo(categoryName))
            .body("id", notNullValue());
    }

}
