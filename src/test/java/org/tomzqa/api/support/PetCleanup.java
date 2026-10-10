package org.tomzqa.api.support;

import io.qameta.allure.Allure;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public final class PetCleanup {
    private PetCleanup() {
    }

    public static void deletePet(long petId) {
        if (petId <= 0) {
            return;
        }

        String diagnostics;
        try {
            Response response = given()
                    .baseUri(ApiConfig.BASE_URI)
                    .pathParam("petId", petId)
                    .when()
                    .delete("/pet/{petId}");

            if (response.statusCode() == 200) {
                return;
            }

            diagnostics = """
                    Cleanup failed for petId=%d
                    HTTP status: %d
                    Response body:
                    %s
                    """.formatted(petId, response.statusCode(), response.asString());
        } catch (Exception exception) {
            diagnostics = """
                    Cleanup request failed for petId=%d
                    Exception: %s
                    """.formatted(petId, exception);
        }

        reportWarning(diagnostics);
    }

    private static void reportWarning(String diagnostics) {
        System.err.println("WARNING: " + diagnostics);
        try {
            Allure.attachment("Pet cleanup diagnostics", diagnostics);
        } catch (Exception exception) {
            System.err.println("WARNING: Could not attach cleanup diagnostics: " + exception);
        }
    }
}
