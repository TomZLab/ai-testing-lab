package org.tomzqa.api.tests;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.tomzqa.api.models.Pet;
import org.tomzqa.api.support.ApiConfig;
import org.tomzqa.api.support.PetCleanup;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static io.restassured.RestAssured.given;
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

        Response response = given()
                .baseUri(ApiConfig.BASE_URI)
                .contentType(ContentType.JSON)
                .body(pet)
                .when()
                .post("/pet")
                .then()
                .statusCode(200)
                .extract()
                .response();

        petId = response.jsonPath().getLong("id");

        response.then()
                .contentType(ContentType.JSON)
                .body("id", equalTo(requestedPetId))
                .body("name", equalTo(petName))
                .body("status", equalTo("available"));
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

        Response response = given()
                .baseUri(ApiConfig.BASE_URI)
                .contentType(ContentType.JSON)
                .body(pet)
                .when()
                .post("/pet")
                .then()
                .statusCode(200)
                .extract()
                .response();

        response.then()
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .body("name", equalTo(petName))
                .body("photoUrls", hasItem(photoUrl));

        petId = response.jsonPath().getLong("id");
    }

    @AfterMethod(alwaysRun = true)
    public void deletePet() {
        PetCleanup.deletePet(petId);
    }
}
