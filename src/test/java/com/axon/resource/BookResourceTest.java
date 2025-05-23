package com.axon.resource;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.axon.repository.AuthorRepository;
import com.axon.repository.BookRepository;
import com.axon.repository.CategoryRepository;

import io.quarkus.test.junit.QuarkusTest;
import static io.restassured.RestAssured.given;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@QuarkusTest
public class BookResourceTest {

    @Inject
    BookRepository bookRepository;

    @Inject
    AuthorRepository authorRepository;

    @Inject
    CategoryRepository categoryRepository;

    @BeforeEach
    @Transactional
    void clearDatabase() {
        bookRepository.deleteAll();
        authorRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    public void whenGetAllBookWithoutDataThenReturnNoContent() {
        given()
        .when()
            .get("/api/books")
        .then()
            .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }

    @Test
    @Transactional
    public void whenCreateBookWithValidDataThenReturnCreatedBook() {
      
        Number idNumberCategory = given()
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .formParam("categoryName", "Science")
        .when()
            .post("/api/categories")
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .body("name", equalTo("Science"))
            .extract().path("id");

        Long categoryId = idNumberCategory.longValue();
    
        Number idNumberAuthor = given()
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .formParam("name", "Author One")
        .when()
            .post("/api/authors")
        .then()
            .statusCode(Response.Status.CREATED.getStatusCode())
            .body("name", equalTo("Author One"))
            .extract().path("id");

        Long authorId = idNumberAuthor.longValue();
  
        given()
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .formParam("name", "Book A")
            .formParam("authorIds", authorId.toString())
            .formParam("categoryId", categoryId)
        .when()
            .post("/api/books")
        .then()
            .statusCode(Response.Status.CREATED.getStatusCode())
            .body("name", equalTo("Book A"))
            .body("id", notNullValue())
            .body("authors[0].id", equalTo(authorId.intValue()))
            .body("category.id", equalTo(categoryId.intValue()));
    }

    @Test
    @Transactional
    public void whenCreateBookWithMissingFieldsThenReturnBadRequest() {
        given()
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .formParam("name", "")
        .when()
            .post("/api/books")
        .then()
            .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    @Transactional
    public void whenGetABookThatExistsThenReturnBookDetails() {
 
        Number idNumberCategory = given()
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .formParam("categoryName", "Literature")
        .when()
            .post("/api/categories")
        .then()
            .extract().path("id");

        Long categoryId = idNumberCategory.longValue();

   
        Number idNumberAuthor = given()
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .formParam("name", "Author X")
        .when()
            .post("/api/authors")
        .then()
            .extract().path("id");

        Long authorId = idNumberAuthor.longValue();
      
        Number idNumberBook = given()
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .formParam("name", "My Book")
            .formParam("authorIds", authorId.toString())
            .formParam("categoryId", categoryId)
        .when()
            .post("/api/books")
        .then()
            .extract().path("id");

        Long bookId = idNumberBook.longValue();

        given()
        .when()
            .get("/api/books/" + bookId)
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .body("id", equalTo(bookId.intValue()))
            .body("name", equalTo("My Book"));
    }

    @Test
    @Transactional
    public void whenDeleteBookThatExistsThenReturnSuccessMessage() {

        Number idNumberCategory = given()
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .formParam("categoryName", "Tech")
        .when()
            .post("/api/categories")
        .then().extract().path("id");

        Long categoryId = idNumberCategory.longValue();

        Number idNumberAuthor = given()
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .formParam("name", "Author Del")
        .when()
            .post("/api/authors")
        .then().extract().path("id");

        Long authorId = idNumberAuthor.longValue();

        Number idNumberBook = given()
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .formParam("name", "Delete Me")
            .formParam("authorIds", authorId.toString())
            .formParam("categoryId", categoryId)
        .when()
            .post("/api/books")
        .then()
            .extract().path("id");

        Long bookId = idNumberBook.longValue();

    
        given()
        .when()
            .delete("/api/books/" + bookId)
        .then()
            .statusCode(Response.Status.OK.getStatusCode())
            .body(equalTo("Successfull deleted"));
    }
}
