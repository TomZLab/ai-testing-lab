package org.tomzqa;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.tomzqa.pages.CartPage;
import org.tomzqa.pages.InventoryPage;
import org.tomzqa.pages.LoginPage;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// These steps intentionally share one authenticated browser and cart state.
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class InventoryCartScenarioTest {
    private static final String FIRST_PRODUCT = "Sauce Labs Backpack";
    private static final String SECOND_PRODUCT = "Sauce Labs Bike Light";
    private static final String THIRD_PRODUCT = "Sauce Labs Bolt T-Shirt";

    private WebDriver driver;
    private InventoryPage inventoryPage;

    @BeforeAll
    void openChromeAndLogIn() {
        driver = new ChromeDriver(ChromeOptionsHelper.create());
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));

        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPage.logIn("standard_user", "secret_sauce");
        inventoryPage = new InventoryPage(driver);
        inventoryPage.waitUntilLoaded();
    }

    @AfterAll
    void closeChrome() {
        if (driver != null) {
            driver.quit();
        }
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
