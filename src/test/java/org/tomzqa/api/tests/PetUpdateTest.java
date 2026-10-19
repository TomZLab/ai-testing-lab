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

public class PetUpdateTest {
    private long petId;

    @BeforeMethod
    public void createPet() {
        petId = 0;
        long requestedPetId = ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE);
        Pet pet = new Pet()
                .id(requestedPetId)
                .name("REST Assured Pet")
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
    public void petCanBeUpdated() {
        Pet pet = new Pet()
                .id(petId)
                .name("Updated REST Assured Pet")
                .photoUrls(List.of("https://example.com/pet.jpg"))
                .status(Pet.StatusEnum.SOLD);

        Pet returnedPet = given()
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

        assertThat(returnedPet.getId(), equalTo(petId));
        assertThat(returnedPet.getName(), equalTo("Updated REST Assured Pet"));
        assertThat(returnedPet.getStatus(), equalTo(Pet.StatusEnum.SOLD));

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
        assertThat(retrievedPet.getName(), equalTo("Updated REST Assured Pet"));
        assertThat(retrievedPet.getStatus(), equalTo(Pet.StatusEnum.SOLD));
    }

    @AfterMethod(alwaysRun = true)
    public void deletePet() {
        PetCleanup.deletePet(petId);
    }
}
