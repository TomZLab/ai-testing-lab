package org.tomzqa.api.tests;

import io.restassured.http.ContentType;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.tomzqa.api.models.Pet;
import org.tomzqa.api.support.ApiRequestSpecs;
import org.tomzqa.api.support.PetCleanup;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class PetGetByIdTest {
    private long petId;
    private String petName;

    @BeforeClass
    public void createPet() {
        petId = 0;
        petName = "REST Assured Pet";

        long requestedPetId = ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE);

        Pet pet = new Pet()
                .id(requestedPetId)
                .name(petName)
                .photoUrls(List.of("https://example.com/pet.jpg"))
                .status(Pet.StatusEnum.AVAILABLE);

        Pet createdPet = given()
                .spec(ApiRequestSpecs.json())
                .body(pet)
                .when()
                .post("/pet")
                .then()
                .statusCode(200)
                .extract()
                .as(Pet.class);

        petId = createdPet.getId();
    }

    @Test
    public void petCanBeRetrievedById() {
        Pet returnedPet = given()
                .spec(ApiRequestSpecs.base())
                .pathParam("petId", petId)
                .when()
                .get("/pet/{petId}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .as(Pet.class);

        assertThat(returnedPet.getId(), equalTo(petId));
        assertThat(returnedPet.getName(), equalTo(petName));
        assertThat(returnedPet.getStatus(), equalTo(Pet.StatusEnum.AVAILABLE));
    }

    @Test
    public void getNonExistingPetReturnsNotFound() {
        long nonExistingPetId = 0L;

        given()
                .spec(ApiRequestSpecs.base())
                .pathParam("petId", nonExistingPetId)
                .when()
                .get("/pet/{petId}")
                .then()
                .statusCode(404);
    }

    @AfterClass(alwaysRun = true)
    public void deletePet() {
        PetCleanup.deletePet(petId);
    }
}
