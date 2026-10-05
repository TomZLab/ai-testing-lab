package org.tomzqa.tests.scenarios;

import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import org.tomzqa.pages.CartPage;
import org.tomzqa.support.BaseScenarioTest;

import java.util.List;

import static org.testng.Assert.assertEquals;

// These steps intentionally share one authenticated browser and cart state.
public class InventoryCartScenarioTest extends BaseScenarioTest {
    private static final String FIRST_PRODUCT = "Sauce Labs Backpack";
    private static final String SECOND_PRODUCT = "Sauce Labs Bike Light";
    private static final String THIRD_PRODUCT = "Sauce Labs Bolt T-Shirt";

    @Test
    public void addFirstProduct() {
        inventoryPage.addProductToCart(FIRST_PRODUCT);

        assertEquals(inventoryPage.getCartCount(), 1, "Cart should contain the first product");
    }

    @Test(dependsOnMethods = "addFirstProduct")
    public void addSecondAndThirdProducts() {
        inventoryPage.addProductToCart(SECOND_PRODUCT);
        inventoryPage.addProductToCart(THIRD_PRODUCT);

        assertEquals(inventoryPage.getCartCount(), 3, "Cart should retain all three products");
    }

    @Test(dependsOnMethods = "addSecondAndThirdProducts")
    public void removeSecondProduct() {
        inventoryPage.removeProductFromCart(SECOND_PRODUCT);

        assertEquals(inventoryPage.getCartCount(), 2, "Cart should contain two remaining products");
    }

    @Test(dependsOnMethods = "removeSecondProduct")
    public void verifyRemainingProductsInCart() {
        CartPage cartPage = inventoryPage.openCart();
        List<String> productNames = cartPage.getProductNames();

        SoftAssert softly = new SoftAssert();
        softly.assertEquals(productNames.size(), 2, "Cart should contain exactly two products");
        softly.assertTrue(productNames.contains(FIRST_PRODUCT), "First product should remain");
        softly.assertTrue(productNames.contains(THIRD_PRODUCT), "Third product should remain");
        softly.assertAll("Remaining cart products");
    }
}
