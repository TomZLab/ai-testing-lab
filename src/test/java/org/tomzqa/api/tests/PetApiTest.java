package org.tomzqa.api.tests;

import io.restassured.http.ContentType;
import org.testng.annotations.Test;
import org.tomzqa.api.support.ApiConfig;

import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.instanceOf;

public class PetApiTest {
    @Test
    public void petCanBeCreated() {
        long petId = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
        String petName = "REST Assured Pet";
        String requestBody = """
                {
                  "id": %d,
                  "name": "%s",
                  "photoUrls": ["https://example.com/pet.jpg"],
                  "status": "available"
                }
                """.formatted(petId, petName);

        given()
                .baseUri(ApiConfig.BASE_URI)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/pet")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(petId))
                .body("name", equalTo(petName))
                .body("status", equalTo("available"));
    }

    @Test
    public void availablePetsCanBeRetrieved() {
        given()
                .baseUri(ApiConfig.BASE_URI)
                .accept(ContentType.JSON)
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
