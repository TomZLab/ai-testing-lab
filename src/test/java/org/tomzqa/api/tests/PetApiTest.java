package org.tomzqa.api.tests;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import org.tomzqa.api.support.ApiConfig;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.instanceOf;

public class PetApiTest {
    @Test
    public void petCanBeCreatedAndRetrieved() {
        long petId = ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE);
        String petName = "REST Assured Pet";
        String requestBody = """
                {
                  "id": %d,
                  "name": "%s",
                  "photoUrls": ["https://example.com/pet.jpg"],
                  "status": "available"
                }
                """.formatted(petId, petName);

        Response creationResponse = given()
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
                .body("status", equalTo("available"))
                .extract()
                .response();

        long createdPetId = creationResponse.jsonPath().getLong("id");

        given()
                .baseUri(ApiConfig.BASE_URI)
                .accept(ContentType.JSON)
                .pathParam("petId", createdPetId)
                .when()
                .get("/pet/{petId}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(createdPetId))
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
