package org.tomzqa.api.tests;

import io.restassured.http.ContentType;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.instanceOf;

public class PetApiTest {
    @Test
    public void availablePetsCanBeRetrieved() {
        given()
                .baseUri("https://petstore3.swagger.io/api/v3")
                .accept(ContentType.JSON)
                .queryParam("status", "available")
        .when()
                .get("/pet/findByStatus")
        .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("", instanceOf(List.class));
    }
}
