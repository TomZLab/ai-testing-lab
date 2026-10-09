package org.tomzqa.api.tests;

import io.qameta.allure.Allure;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.tomzqa.api.support.ApiConfig;

import java.util.concurrent.ThreadLocalRandom;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class PetGetByIdTest {
    private long petId;
    private String petName;

    @BeforeMethod
    public void createPet() {
        petId = 0;
        petName = "REST Assured Pet";

        long requestedPetId = ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE);

        String requestBody = """
                {
                  "id": %d,
                  "name": "%s",
                  "photoUrls": ["https://example.com/pet.jpg"],
                  "status": "available"
                }
                """.formatted(requestedPetId, petName);

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
    public void petCanBeRetrievedById() {
        given()
                .baseUri(ApiConfig.BASE_URI)
                .pathParam("petId", petId)
                .when()
                .get("/pet/{petId}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(petId))
                .body("name", equalTo(petName))
                .body("status", equalTo("available"));
    }

    @AfterMethod(alwaysRun = true)
    public void deletePet() {
        if (petId <= 0) {
            return;
        }

        try {
            Response response = given()
                    .baseUri(ApiConfig.BASE_URI)
                    .pathParam("petId", petId)
                    .when()
                    .delete("/pet/{petId}");

            if (response.statusCode() != 200) {
                reportCleanupWarning("""
                        Cleanup failed for petId=%d
                        HTTP status: %d
                        Response body:
                        %s
                        """.formatted(petId, response.statusCode(), response.asString()));
            }
        } catch (Exception exception) {
            reportCleanupWarning("""
                    Cleanup request failed for petId=%d
                    Exception: %s
                    """.formatted(petId, exception));
        }
    }

    private void reportCleanupWarning(String message) {
        System.err.println("WARNING: " + message);
        Allure.attachment("Pet cleanup diagnostics", message);
    }
}
