package org.tomzqa.tests.scenarios;

import org.tomzqa.support.BaseScenarioTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.tomzqa.pages.CartPage;
import org.tomzqa.pages.InventoryPage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// These steps intentionally share one authenticated browser and cart state.
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class InventoryCartScenarioTest extends BaseScenarioTest {
    private static final String FIRST_PRODUCT = "Sauce Labs Backpack";
    private static final String SECOND_PRODUCT = "Sauce Labs Bike Light";
    private static final String THIRD_PRODUCT = "Sauce Labs Bolt T-Shirt";

    private InventoryPage inventoryPage;

    @BeforeAll
    void initializeInventoryPage() {
        inventoryPage = new InventoryPage(getWebDriver());
        inventoryPage.waitUntilLoaded();
    }

    @Test
    @Order(1)
    void addFirstProduct() {
        inventoryPage.addProductToCart(FIRST_PRODUCT);

        assertEquals(1, inventoryPage.getCartCount(), "Cart should contain the first product");
    }

    @Test
    @Order(2)
    void addSecondAndThirdProducts() {
        inventoryPage.addProductToCart(SECOND_PRODUCT);
        inventoryPage.addProductToCart(THIRD_PRODUCT);

        assertEquals(3, inventoryPage.getCartCount(), "Cart should retain all three products");
    }

    @Test
    @Order(3)
    void removeSecondProduct() {
        inventoryPage.removeProductFromCart(SECOND_PRODUCT);

        assertEquals(2, inventoryPage.getCartCount(), "Cart should contain two remaining products");
    }

    @Test
    @Order(4)
    void verifyRemainingProductsInCart() {
        CartPage cartPage = inventoryPage.openCart();
        List<String> productNames = cartPage.getProductNames();

        assertAll("Remaining cart products",
                () -> assertEquals(2, productNames.size(), "Cart should contain exactly two products"),
                () -> assertTrue(productNames.contains(FIRST_PRODUCT), "First product should remain"),
                () -> assertTrue(productNames.contains(THIRD_PRODUCT), "Third product should remain"));
    }
}
