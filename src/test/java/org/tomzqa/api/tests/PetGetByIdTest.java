package org.tomzqa.api.tests;

import io.restassured.http.ContentType;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.tomzqa.api.models.Pet;
import org.tomzqa.api.support.ApiConfig;
import org.tomzqa.api.support.PetCleanup;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class PetGetByIdTest {
    private long petId;
    private String petName;

    @BeforeMethod
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
                .baseUri(ApiConfig.BASE_URI)
                .contentType(ContentType.JSON)
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
        assertThat(returnedPet.getName(), equalTo(petName));
        assertThat(returnedPet.getStatus(), equalTo(Pet.StatusEnum.AVAILABLE));
    }

    @AfterMethod(alwaysRun = true)
    public void deletePet() {
        PetCleanup.deletePet(petId);
    }
}
