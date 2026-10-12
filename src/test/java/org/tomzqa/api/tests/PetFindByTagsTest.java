package org.tomzqa.api.tests;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.tomzqa.api.support.ApiConfig;
import org.tomzqa.api.support.PetCleanup;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.instanceOf;

public class PetFindByTagsTest {
    private long petId;
    private String tagName;

    @BeforeMethod
    public void createPet() {
        petId = 0;
        long requestedPetId = ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE);
        tagName = "test-tag-" + requestedPetId;
        String requestBody = """
                {
                  "id": %d,
                  "name": "REST Assured Pet",
                  "photoUrls": ["https://example.com/pet.jpg"],
                  "status": "available",
                  "tags": [{"id": 1, "name": "%s"}]
                }
                """.formatted(requestedPetId, tagName);

        given()
                .baseUri(ApiConfig.BASE_URI)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/pet")
                .then()
                .statusCode(200);

        petId = requestedPetId;
    }

    @Test
    public void petCanBeFoundByTag() {
        Response response = given()
                .baseUri(ApiConfig.BASE_URI)
                .queryParam("tags", tagName)
                .when()
                .get("/pet/findByTags")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("", instanceOf(List.class))
                .body("id", hasItem(petId))
                .extract()
                .response();

        List<String> tagNames = response.jsonPath()
                .getList("find { it.id == %dL }.tags.name".formatted(petId));

        assertThat(tagNames, hasItem(tagName));
    }

    @AfterMethod(alwaysRun = true)
    public void deletePet() {
        PetCleanup.deletePet(petId);
    }
}
