package org.tomzqa.api.tests.scenarios;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import org.tomzqa.api.support.ApiConfig;
import org.tomzqa.api.support.PetCleanup;

import java.util.concurrent.ThreadLocalRandom;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class PetCrudScenarioTest {
    private static final String PET_NAME = "REST Assured Pet";
    private static final String UPDATED_PET_NAME = "Updated REST Assured Pet";

    private long petId;

    @Test
    public void createPet() {
        petId = 0;
        long requestedPetId = ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE);

        String createBody = """
                {
                  "id": %d,
                  "name": "%s",
                  "photoUrls": ["https://example.com/pet.jpg"],
                  "status": "available"
                }
                """.formatted(requestedPetId, PET_NAME);

        Response creationResponse = given()
                .baseUri(ApiConfig.BASE_URI)
                .contentType(ContentType.JSON)
                .body(createBody)
                .when()
                .post("/pet")
                .then()
                .statusCode(200)
                .extract()
                .response();

        petId = requestedPetId;

        creationResponse.then()
                .contentType(ContentType.JSON)
                .body("id", equalTo(requestedPetId))
                .body("name", equalTo(PET_NAME))
                .body("status", equalTo("available"));
    }

    @Test(dependsOnMethods = "createPet")
    public void readCreatedPet() {
        given()
                .baseUri(ApiConfig.BASE_URI)
                .pathParam("petId", petId)
                .when()
                .get("/pet/{petId}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(petId))
                .body("name", equalTo(PET_NAME))
                .body("status", equalTo("available"));
    }

    @Test(dependsOnMethods = "readCreatedPet")
    public void updatePet() {
        String updateBody = """
                {
                  "id": %d,
                  "name": "%s",
                  "photoUrls": ["https://example.com/pet.jpg"],
                  "status": "sold"
                }
                """.formatted(petId, UPDATED_PET_NAME);

        given()
                .baseUri(ApiConfig.BASE_URI)
                .contentType(ContentType.JSON)
                .body(updateBody)
                .when()
                .put("/pet")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(petId))
                .body("name", equalTo(UPDATED_PET_NAME))
                .body("status", equalTo("sold"));
    }

    @Test(dependsOnMethods = "updatePet")
    public void verifyUpdatedPet() {
        given()
                .baseUri(ApiConfig.BASE_URI)
                .pathParam("petId", petId)
                .when()
                .get("/pet/{petId}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(petId))
                .body("name", equalTo(UPDATED_PET_NAME))
                .body("status", equalTo("sold"));
    }

    @Test(dependsOnMethods = "verifyUpdatedPet")
    public void deletePet() {
        given()
                .baseUri(ApiConfig.BASE_URI)
                .pathParam("petId", petId)
                .when()
                .delete("/pet/{petId}")
                .then()
                .statusCode(200);
    }

    @Test(dependsOnMethods = "deletePet")
    public void verifyPetIsDeleted() {
        given()
                .baseUri(ApiConfig.BASE_URI)
                .pathParam("petId", petId)
                .when()
                .get("/pet/{petId}")
                .then()
                .statusCode(404);

        petId = 0;
    }

    @AfterClass(alwaysRun = true)
    public void cleanupPet() {
        PetCleanup.deletePet(petId);
    }
}
