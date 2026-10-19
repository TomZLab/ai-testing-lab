package org.tomzqa.api.tests.scenarios;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import org.tomzqa.api.models.Pet;
import org.tomzqa.api.support.ApiConfig;
import org.tomzqa.api.support.PetCleanup;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class PetCrudScenarioTest {
    private static final String PET_NAME = "REST Assured Pet";
    private static final String UPDATED_PET_NAME = "Updated REST Assured Pet";

    private long petId;

    @Test
    public void createPet() {
        petId = 0;
        long requestedPetId = ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE);

        Pet pet = new Pet()
                .id(requestedPetId)
                .name(PET_NAME)
                .photoUrls(List.of("https://example.com/pet.jpg"))
                .status(Pet.StatusEnum.AVAILABLE);

        Response creationResponse = given()
                .baseUri(ApiConfig.BASE_URI)
                .contentType(ContentType.JSON)
                .body(pet)
                .when()
                .post("/pet")
                .then()
                .statusCode(200)
                .extract()
                .response();

        petId = requestedPetId;

        Pet createdPet = creationResponse.then()
                .contentType(ContentType.JSON)
                .extract()
                .as(Pet.class);

        assertThat(createdPet.getId(), equalTo(requestedPetId));
        assertThat(createdPet.getName(), equalTo(PET_NAME));
        assertThat(createdPet.getStatus(), equalTo(Pet.StatusEnum.AVAILABLE));
    }

    @Test(dependsOnMethods = "createPet")
    public void readCreatedPet() {
        Pet returnedPet = given()
                .baseUri(ApiConfig.BASE_URI)
                .pathParam("petId", petId)
                .when()
                .get("/pet/{petId}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .as(Pet.class);

        assertThat(returnedPet.getId(), equalTo(petId));
        assertThat(returnedPet.getName(), equalTo(PET_NAME));
        assertThat(returnedPet.getStatus(), equalTo(Pet.StatusEnum.AVAILABLE));
    }

    @Test(dependsOnMethods = "readCreatedPet")
    public void updatePet() {
        Pet pet = new Pet()
                .id(petId)
                .name(UPDATED_PET_NAME)
                .photoUrls(List.of("https://example.com/pet.jpg"))
                .status(Pet.StatusEnum.SOLD);

        Pet updatedPet = given()
                .baseUri(ApiConfig.BASE_URI)
                .contentType(ContentType.JSON)
                .body(pet)
                .when()
                .put("/pet")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .as(Pet.class);

        assertThat(updatedPet.getId(), equalTo(petId));
        assertThat(updatedPet.getName(), equalTo(UPDATED_PET_NAME));
        assertThat(updatedPet.getStatus(), equalTo(Pet.StatusEnum.SOLD));
    }

    @Test(dependsOnMethods = "updatePet")
    public void verifyUpdatedPet() {
        Pet retrievedPet = given()
                .baseUri(ApiConfig.BASE_URI)
                .pathParam("petId", petId)
                .when()
                .get("/pet/{petId}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .as(Pet.class);

        assertThat(retrievedPet.getId(), equalTo(petId));
        assertThat(retrievedPet.getName(), equalTo(UPDATED_PET_NAME));
        assertThat(retrievedPet.getStatus(), equalTo(Pet.StatusEnum.SOLD));
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
