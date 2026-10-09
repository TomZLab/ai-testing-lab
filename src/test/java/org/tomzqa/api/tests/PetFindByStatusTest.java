package org.tomzqa.api.tests;

import io.restassured.http.ContentType;
import org.testng.annotations.Test;
import org.tomzqa.api.support.ApiConfig;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.instanceOf;

public class PetFindByStatusTest {
    @Test
    public void availablePetsCanBeRetrieved() {
        given()
                .baseUri(ApiConfig.BASE_URI)
                .queryParam("status", "available")
                .when()
                .get("/pet/findByStatus")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("", instanceOf(List.class))
                .body("status", everyItem(equalTo("available")));
    }
}
