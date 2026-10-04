package org.tomzqa;

import org.testng.annotations.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.tomzqa.pages.CartPage;
import org.tomzqa.pages.InventoryPage;
import org.tomzqa.pages.LoginPage;

import java.time.Duration;
import java.util.List;

import org.testng.asserts.SoftAssert;

import static org.testng.Assert.*;

// These steps intentionally share one authenticated browser and cart state.
public class InventoryCartScenarioTest {
    private static final String FIRST_PRODUCT = "Sauce Labs Backpack";
    private static final String SECOND_PRODUCT = "Sauce Labs Bike Light";
    private static final String THIRD_PRODUCT = "Sauce Labs Bolt T-Shirt";

    private WebDriver driver;
    private InventoryPage inventoryPage;

    @BeforeClass
    public void openChromeAndLogIn() {
        driver = new ChromeDriver(ChromeOptionsHelper.create());
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));

        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPage.logIn(TestConfig.STANDARD_USERNAME, TestConfig.STANDARD_PASSWORD);
        inventoryPage = new InventoryPage(driver);
        inventoryPage.waitUntilLoaded();
    }

    @AfterClass(alwaysRun = true)
    public void closeChrome() {
        if (driver != null) {
            driver.quit();
        }
    }

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
