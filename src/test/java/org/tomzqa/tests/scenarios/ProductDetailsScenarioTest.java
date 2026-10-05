package org.tomzqa.tests.scenarios;

import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import org.tomzqa.pages.ProductDetailsPage;
import org.tomzqa.support.BaseScenarioTest;

import java.util.List;

import static org.testng.Assert.assertEquals;

// These steps intentionally share one authenticated browser and navigation state.
public class ProductDetailsScenarioTest extends BaseScenarioTest {
    private ProductDetailsPage detailsPage;

    @Test
    public void openBackpackDetails() {
        detailsPage = inventoryPage.openBackpack();

        assertEquals(
                detailsPage.getProductName(),
                "Sauce Labs Backpack",
                "Product details should open for Backpack"
        );
    }

    @Test(dependsOnMethods = "openBackpackDetails")
    public void returnToInventory() {
        inventoryPage = detailsPage.backToProducts();

        assertEquals(
                inventoryPage.getHeading(),
                "Products",
                "Should return to inventory page"
        );
    }

    @Test(dependsOnMethods = "returnToInventory")
    public void verifyInventoryRestored() {
        inventoryPage.waitUntilLoaded();

        List<String> productNames = inventoryPage.getProductNames();

        SoftAssert softly = new SoftAssert();
        softly.assertTrue(
                inventoryPage.hasVisibleItem(),
                "Inventory should contain visible products"
        );
        softly.assertTrue(
                productNames.contains("Sauce Labs Backpack"),
                "Backpack should be available after returning"
        );
        softly.assertEquals(
                productNames.size(),
                6,
                "All inventory products should be available"
        );
        softly.assertAll("Inventory restored");
    }
}
