package org.tomzqa.api.tests;

import io.restassured.http.ContentType;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.tomzqa.api.models.Pet;
import org.tomzqa.api.support.ApiConfig;
import org.tomzqa.api.support.PetCleanup;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;

public class PetCreateTest {
    private long petId;

    @Test
    public void petCanBeCreated() {
        petId = 0;
        long requestedPetId = ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE);
        String petName = "REST Assured Pet";
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
                .contentType(ContentType.JSON)
                .extract()
                .as(Pet.class);

        petId = createdPet.getId();

        assertThat(createdPet.getId(), equalTo(requestedPetId));
        assertThat(createdPet.getName(), equalTo(petName));
        assertThat(createdPet.getStatus(), equalTo(Pet.StatusEnum.AVAILABLE));
    }

    // Known Petstore defect:
    // POST /pet returns 500 when id is omitted,
    // although the OpenAPI schema marks id as optional.
    // Backend throws NullPointerException while accessing Pet.getId().
    @Test(enabled = false)
    public void petCanBeCreatedWithRequiredFieldsOnly() {
        petId = 0;
        String petName = "Minimal REST Assured Pet";
        String photoUrl = "https://example.com/pet.jpg";
        Pet pet = new Pet()
                .name(petName)
                .photoUrls(List.of(photoUrl));

        Pet createdPet = given()
                .baseUri(ApiConfig.BASE_URI)
                .contentType(ContentType.JSON)
                .body(pet)
                .when()
                .post("/pet")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .as(Pet.class);

        assertThat(createdPet.getId(), notNullValue());
        assertThat(createdPet.getName(), equalTo(petName));
        assertThat(createdPet.getPhotoUrls(), hasItem(photoUrl));

        petId = createdPet.getId();
    }

    @AfterMethod(alwaysRun = true)
    public void deletePet() {
        PetCleanup.deletePet(petId);
    }
}
