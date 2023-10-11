package com.demoqa.tests;

import com.codeborne.selenide.Condition;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.demoqa.tests.TestData.login;
import static io.restassured.RestAssured.given;
import static java.lang.String.format;
import static org.hamcrest.Matchers.is;

public class CollectionTests extends TestBase {

    @Test
    void addBookToCollection() {
        String authData = "{\"userName\":\"marina\",\"password\":\"Marina19@\"}";

        Response authResponse = given()
                .log().uri()
                .log().method()
                .log().body()
                .contentType(ContentType.JSON)
                .body(authData)
                .when()
                .post("/Account/v1/Login")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .extract().response();

        given()
                .log().uri()
                .log().method()
                .log().body()
                .contentType(ContentType.JSON)
                .header("Authorization","Bearer " + authResponse.path("token"))
                .queryParams("UserId", authResponse.path("userId"))
                .when()
                .delete("/BookStore/v1/Books")
                .then()
                .log().status()
                .log().body()
                .statusCode(204);

        String isbn = "9781449331818";
        String bookData = format("{\"userId\":\"%s\",\"collectionOfIsbns\":[{\"isbn\":\"%s\"}]}",
                authResponse.path("userId"),isbn);

        given()
                .log().uri()
                .log().method()
                .log().body()
                .contentType(ContentType.JSON)
                .header("Authorization","Bearer " + authResponse.path("token"))
                .body(bookData)
                .when()
                .post("/BookStore/v1/Books")
                .then()
                .log().status()
                .log().body()
                .statusCode(201);


        open("/favicon.ico");
        getWebDriver().manage().addCookie(new Cookie("userID",authResponse.path("userId")));
        getWebDriver().manage().addCookie(new Cookie("expires",authResponse.path("expires")));
        getWebDriver().manage().addCookie(new Cookie("token",authResponse.path("token ")));

        open("/profile");
        $(".ReactTable").shouldHave(Condition.text("Learning JavaScript Design Patterns"));
    }

    @DisplayName("LOGIN-SUCCESSFUL WITH API")
    @Test
    void negative401AddBookToCollectionTest() {



        String userId = "862cbbea-c3e8-4a39-be64-3b053aedeef2";
        String isbn = "9781449331818";
        String bookData = format("{\"userId\":\"%s\",\"collectionOfIsbns\":[{\"isbn\":\"%s\"}]}",
                userId,isbn);

        given()
                .log().uri()
                .log().method()
                .log().body()
                .contentType(ContentType.JSON)
                .body(bookData)
                .when()
                .post("/BookStore/v1/Books")
                .then()
                .log().status()
                .log().body()
                .statusCode(401)
                .body("code",is("1200"))
                .body("message", is("User not authorized!"));

    }

    @Test
    void negative400BookCollectionTest() {
        String authData = "{\"userName\":\"test123456\",\"password\":\"Test123456@\"}";

        Response authResponse = given()
                .log().uri()
                .log().method()
                .log().body()
                .contentType(ContentType.JSON)
                .body(authData)
                .when()
                .post("/Account/v1/Login")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .extract().response();


        String isbn = "9781449331818";
        String bookData = format("{\"userId\":\"%s\",\"collectionOfIsbns\":[{\"isbn\":\"%s\"}]}",
                authResponse.path("userId"),isbn);

       given()
                .log().uri()
                .log().method()
                .log().body()
                .contentType(ContentType.JSON)
                .header("Authorization","Bearer " + authResponse.path("token"))
                .body(bookData)
                .when()
                .post("/BookStore/v1/Books")
                .then()
                .log().status()
                .log().body()
                .statusCode(400)
                .body("code",is("1210"))
                .body("message", is("ISBN already present in the User's Collection!"));

    }

    @Test
    void addBookToCollection_withDelete1BookTest() {
        String authData = "{\"userName\":\"marina\",\"password\":\"Marina19@\"}";

        Response authResponse = given()
                .log().uri()
                .log().method()
                .log().body()
                .contentType(ContentType.JSON)
                .body(authData)
                .when()
                .post("/Account/v1/Login")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .extract().response();

        String isbn = "9781449325862";

        String deleteBookData = format("{\"userId\":\"%s\",\"isbn\":\"%s\"}",
                authResponse.path("userId"),isbn);

        given()
                .log().uri()
                .log().method()
                .log().body()
                .contentType(ContentType.JSON)
                .header("Authorization","Bearer " + authResponse.path("token"))
                .body(deleteBookData)
                .when()
                .delete("/BookStore/v1/Book")
                .then()
                .log().status()
                .log().body()
                .statusCode(204);


        String bookData = format("{\"userId\":\"%s\",\"collectionOfIsbns\":[{\"isbn\":\"%s\"}]}",
                authResponse.path("userId"),isbn);

        given()
                .log().uri()
                .log().method()
                .log().body()
                .contentType(ContentType.JSON)
                .header("Authorization","Bearer " + authResponse.path("token"))
                .body(bookData)
                .when()
                .post("/BookStore/v1/Books")
                .then()
                .log().status()
                .log().body()
                .statusCode(201);


        open("/favicon.ico");
        getWebDriver().manage().addCookie(new Cookie("userID",authResponse.path("userId")));
        getWebDriver().manage().addCookie(new Cookie("expires",authResponse.path("expires")));
        getWebDriver().manage().addCookie(new Cookie("token",authResponse.path("token ")));


        open("/profile");
        $(".ReactTable").shouldHave(Condition.text("Git Pocket Guide"));
    }
}