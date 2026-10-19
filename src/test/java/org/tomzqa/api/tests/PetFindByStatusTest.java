package org.tomzqa.api.tests;

import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.tomzqa.api.models.Pet;
import org.tomzqa.api.support.ApiConfig;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;

public class PetFindByStatusTest {

    @Test(dataProvider = "petStatuses")
    public void petsCanBeRetrievedByStatus(String status) {
        List<Pet> pets = given()
                .baseUri(ApiConfig.BASE_URI)
                .queryParam("status", status)
                .when()
                .get("/pet/findByStatus")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .as(new TypeRef<List<Pet>>() {});

        assertThat(pets.stream().map(Pet::getStatus).toList(),
                everyItem(equalTo(Pet.StatusEnum.fromValue(status))));
    }

    @DataProvider(name = "petStatuses")
    public Object[][] petStatuses() {
        return new Object[][]{
                {"available"},
                {"pending"},
                {"sold"}
        };
    }
}
