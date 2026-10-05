package org.tomzqa.tests.scenarios;

import org.tomzqa.support.BaseScenarioTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.tomzqa.pages.InventoryPage;
import org.tomzqa.pages.ProductDetailsPage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// These steps intentionally share one authenticated browser and navigation state.
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductDetailsScenarioTest extends BaseScenarioTest {
    private InventoryPage inventoryPage;
    private ProductDetailsPage detailsPage;

    @BeforeAll
    void initializeInventoryPage() {
        inventoryPage = new InventoryPage(getWebDriver());
        inventoryPage.waitUntilLoaded();
    }

    @Test
    @Order(1)
    void openBackpackDetails() {
        detailsPage = inventoryPage.openBackpack();

        assertEquals("Sauce Labs Backpack", detailsPage.getProductName(), "Product details name");
    }

    @Test
    @Order(2)
    void returnToInventory() {
        inventoryPage = detailsPage.backToProducts();

        assertEquals("Products", inventoryPage.getHeading(), "Inventory heading after returning");
    }

    @Test
    @Order(3)
    void verifyInventoryRestored() {
        inventoryPage.waitUntilLoaded();

        List<String> productNames = inventoryPage.getProductNames();

        assertAll("Inventory restored",
                () -> assertTrue(inventoryPage.hasVisibleItem(), "Inventory should display products"),
                () -> assertEquals(6, productNames.size(), "Expected demo product count"),
                () -> assertTrue(productNames.contains("Sauce Labs Backpack"),
                        "Backpack should be available in inventory"));
    }
}
