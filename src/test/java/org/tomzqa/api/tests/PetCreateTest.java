package org.tomzqa.api.tests;

import io.restassured.http.ContentType;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.tomzqa.api.models.Pet;
import org.tomzqa.api.support.ApiRequestSpecs;
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
        String photoUrl = "https://example.com/pet.jpg";

        Pet pet = new Pet()
                .id(requestedPetId)
                .name(petName)
                .photoUrls(List.of(photoUrl))
                .status(Pet.StatusEnum.AVAILABLE);

        Pet createdPet = given()
                .spec(ApiRequestSpecs.json())
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
    // The OpenAPI schema marks id as optional,
    // but POST /pet returns 500 when id is omitted.
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
                .spec(ApiRequestSpecs.json())
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

    // Known Petstore defect:
    // The OpenAPI schema marks name as required,
    // but POST /pet returns 200 and creates a pet when name is omitted.
    // The request should be rejected with 400.
    @Test(enabled = false)
    public void petWithoutNameIsRejected() {
        petId = 0;
        long requestedPetId = ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE);
        String photoUrl = "https://example.com/pet.jpg";

        Pet pet = new Pet()
                .id(requestedPetId)
                .photoUrls(List.of(photoUrl))
                .status(Pet.StatusEnum.AVAILABLE);

        given()
                .spec(ApiRequestSpecs.json())
                .body(pet)
                .when()
                .post("/pet")
                .then()
                .statusCode(400);
    }

    @AfterMethod(alwaysRun = true)
    public void deletePet() {
        PetCleanup.deletePet(petId);
    }
}
