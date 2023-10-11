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
import static com.demoqa.tests.TestData.password;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class LoginTests extends TestBase {
    @DisplayName("LOGIN-SUCCESSFUL WITH UI")
    @Test
    void successfulLoginWithUITest() {
    open("/login");
    $("#userName").setValue(login);
    $("#password").setValue(password).pressEnter();
    $("#userName-value").shouldHave(Condition.text(login));
    }

    @DisplayName("LOGIN-SUCCESSFUL WITH API")
    @Test
    void successfulLoginWithAPITest() {
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

        open("/favicon.ico");
        getWebDriver().manage().addCookie(new Cookie("userID",authResponse.path("userId")));
        getWebDriver().manage().addCookie(new Cookie("expires",authResponse.path("expires")));
        getWebDriver().manage().addCookie(new Cookie("token",authResponse.path("token ")));

        open("/profile");
        $("#userName-value").shouldHave(Condition.text(login));
    }


}
