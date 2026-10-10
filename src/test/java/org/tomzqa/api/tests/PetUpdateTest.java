package org.tomzqa.api.tests;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.tomzqa.api.support.ApiConfig;
import org.tomzqa.api.support.PetCleanup;

import java.util.concurrent.ThreadLocalRandom;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class PetUpdateTest {
    private long petId;

    @BeforeMethod
    public void createPet() {
        petId = 0;
        long requestedPetId = ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE);
        String requestBody = """
                {
                  "id": %d,
                  "name": "REST Assured Pet",
                  "photoUrls": ["https://example.com/pet.jpg"],
                  "status": "available"
                }
                """.formatted(requestedPetId);

        Response creationResponse = given()
                .baseUri(ApiConfig.BASE_URI)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/pet")
                .then()
                .statusCode(200)
                .extract()
                .response();

        petId = creationResponse.jsonPath().getLong("id");
    }

    @Test
    public void petCanBeUpdated() {
        String requestBody = """
                {
                  "id": %d,
                  "name": "Updated REST Assured Pet",
                  "photoUrls": ["https://example.com/pet.jpg"],
                  "status": "sold"
                }
                """.formatted(petId);

        given()
                .baseUri(ApiConfig.BASE_URI)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/pet")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(petId))
                .body("name", equalTo("Updated REST Assured Pet"))
                .body("status", equalTo("sold"));

        given()
                .baseUri(ApiConfig.BASE_URI)
                .pathParam("petId", petId)
                .when()
                .get("/pet/{petId}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(petId))
                .body("name", equalTo("Updated REST Assured Pet"))
                .body("status", equalTo("sold"));
    }

    @AfterMethod(alwaysRun = true)
    public void deletePet() {
        PetCleanup.deletePet(petId);
    }
}
