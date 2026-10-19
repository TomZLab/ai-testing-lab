package org.tomzqa.api.tests;

import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.tomzqa.api.models.Pet;
import org.tomzqa.api.models.Tag;
import org.tomzqa.api.support.ApiConfig;
import org.tomzqa.api.support.PetCleanup;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

public class PetFindByTagsTest {
    private long petId;
    private String tagName;

    @BeforeMethod
    public void createPet() {
        petId = 0;
        long requestedPetId = ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE);
        tagName = "test-tag-" + requestedPetId;
        Pet pet = new Pet()
                .id(requestedPetId)
                .name("REST Assured Pet")
                .photoUrls(List.of("https://example.com/pet.jpg"))
                .status(Pet.StatusEnum.AVAILABLE)
                .tags(List.of(new Tag().id(1L).name(tagName)));

        given()
                .baseUri(ApiConfig.BASE_URI)
                .contentType(ContentType.JSON)
                .body(pet)
                .when()
                .post("/pet")
                .then()
                .statusCode(200);

        petId = requestedPetId;
    }

    @Test
    public void petCanBeFoundByTag() {
        List<Pet> pets = given()
                .baseUri(ApiConfig.BASE_URI)
                .queryParam("tags", tagName)
                .when()
                .get("/pet/findByTags")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .as(new TypeRef<List<Pet>>() {
                });

        boolean allPetsContainTag = pets.stream()
                .allMatch(pet -> pet.getTags().stream()
                        .anyMatch(tag -> tagName.equals(tag.getName())));

        assertThat(allPetsContainTag, equalTo(true));
        assertThat(
                pets.stream().map(Pet::getId).toList(),
                hasItem(petId)
        );
    }

    @AfterMethod(alwaysRun = true)
    public void deletePet() {
        PetCleanup.deletePet(petId);
    }
}
