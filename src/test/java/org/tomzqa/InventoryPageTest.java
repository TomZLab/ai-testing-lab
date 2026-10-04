package org.tomzqa;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import org.tomzqa.pages.InventoryPage;
import org.tomzqa.pages.LoginPage;
import org.tomzqa.pages.ProductDetailsPage;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

import static org.testng.Assert.assertEquals;

public class InventoryPageTest extends BaseTest {
    private InventoryPage inventoryPage;

    @BeforeMethod
    public void logIn() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPage.logIn(TestConfig.STANDARD_USERNAME, TestConfig.STANDARD_PASSWORD);
        inventoryPage = new InventoryPage(driver);
        inventoryPage.waitUntilLoaded();
    }

    @Test
    public void inventoryDisplaysProductsAndPrices() {
        SoftAssert softly = new SoftAssert();
        softly.assertEquals(inventoryPage.getHeading(), "Products");
        softly.assertEquals(inventoryPage.getProductNames().size(), 6, "Expected demo product count");
        softly.assertTrue(inventoryPage.getProductNames().contains("Sauce Labs Backpack"));
        softly.assertEquals(inventoryPage.getProductPrices().size(), 6);
        softly.assertTrue(inventoryPage.getProductPrices().stream().allMatch(price -> price.signum() > 0));
        softly.assertEquals(inventoryPage.getCartCount(), 0, "Cart should start empty");
        softly.assertAll("Inventory");
    }

    @Test
    public void productsCanBeSortedByNameAscending() {
        inventoryPage.sortByNameDescending();
        List<String> descendingNames = inventoryPage.getProductNames();
        List<String> expectedDescendingNames = descendingNames.stream().sorted(Comparator.reverseOrder()).toList();
        List<String> expectedAscendingNames = descendingNames.stream().sorted().toList();

        inventoryPage.sortByNameAscending();
        List<String> actualAscendingNames = inventoryPage.getProductNames();

        SoftAssert softly = new SoftAssert();
        softly.assertEquals(descendingNames.size(), 6);
        softly.assertEquals(descendingNames, expectedDescendingNames, "Setup should be sorted descending");
        softly.assertNotEquals(descendingNames, expectedAscendingNames, "Descending setup should differ from ascending order");
        softly.assertEquals(actualAscendingNames, expectedAscendingNames, "Products should be sorted ascending");
        softly.assertAll("Name ascending");
    }

    @Test
    public void productsCanBeSortedByNameDescending() {
        inventoryPage.sortByNameAscending();
        List<String> ascendingNames = inventoryPage.getProductNames();
        List<String> expectedAscendingNames = ascendingNames.stream().sorted().toList();
        List<String> expectedDescendingNames = ascendingNames.stream().sorted(Comparator.reverseOrder()).toList();

        inventoryPage.sortByNameDescending();
        List<String> actualDescendingNames = inventoryPage.getProductNames();

        SoftAssert softly = new SoftAssert();
        softly.assertEquals(ascendingNames.size(), 6);
        softly.assertEquals(ascendingNames, expectedAscendingNames, "Setup should be sorted ascending");
        softly.assertNotEquals(ascendingNames, expectedDescendingNames, "Ascending setup should differ from descending order");
        softly.assertEquals(actualDescendingNames, expectedDescendingNames, "Products should be sorted descending");
        softly.assertAll("Name descending");
    }

    @Test
    public void productsCanBeSortedByPriceAscending() {
        List<BigDecimal> prices = inventoryPage.getProductPrices();
        List<BigDecimal> expectedPrices = prices.stream().sorted().toList();

        inventoryPage.sortByPriceAscending();

        SoftAssert softly = new SoftAssert();
        softly.assertEquals(prices.size(), 6);
        softly.assertEquals(inventoryPage.getProductPrices(), expectedPrices);
        softly.assertAll("Price ascending");
    }

    @Test
    public void productsCanBeSortedByPriceDescending() {
        List<BigDecimal> prices = inventoryPage.getProductPrices();
        List<BigDecimal> expectedPrices = prices.stream().sorted(Comparator.reverseOrder()).toList();

        inventoryPage.sortByPriceDescending();

        SoftAssert softly = new SoftAssert();
        softly.assertEquals(prices.size(), 6);
        softly.assertEquals(inventoryPage.getProductPrices(), expectedPrices);
        softly.assertAll("Price descending");
    }

    @Test
    public void productCanBeAddedToCart() {
        String productName = "Sauce Labs Backpack";
        int initialCartCount = inventoryPage.getCartCount();

        inventoryPage.addProductToCart(productName);

        SoftAssert softly = new SoftAssert();
        softly.assertEquals(initialCartCount, 0, "Cart should start empty");
        softly.assertEquals(inventoryPage.getCartCount(), 1, "Cart count after adding");
        softly.assertTrue(inventoryPage.canRemoveProduct(productName), "Remove button after adding");
        softly.assertAll("Product added");
    }

    @Test
    public void productCanBeRemovedFromCart() {
        String productName = "Test.allTheThings() T-Shirt (Red)";
        inventoryPage.addProductToCart(productName);
        int initialCartCount = inventoryPage.getCartCount();

        inventoryPage.removeProductFromCart(productName);

        SoftAssert softly = new SoftAssert();
        softly.assertEquals(initialCartCount, 1, "Cart should contain the product before removal");
        softly.assertEquals(inventoryPage.getCartCount(), 0, "Cart count after removing");
        softly.assertTrue(inventoryPage.canAddProduct(productName), "Add button after removing");
        softly.assertAll("Product removed");
    }

    @Test
    public void removingOneProductKeepsTheOtherInCart() {
        String removedProduct = "Sauce Labs Backpack";
        String remainingProduct = "Sauce Labs Bike Light";
        int initialCartCount = inventoryPage.getCartCount();

        inventoryPage.addProductToCart(removedProduct);
        inventoryPage.addProductToCart(remainingProduct);
        inventoryPage.removeProductFromCart(removedProduct);

        SoftAssert softly = new SoftAssert();
        softly.assertEquals(initialCartCount, 0, "Cart should start empty");
        softly.assertEquals(inventoryPage.getCartCount(), 1, "Cart should contain one product");
        softly.assertTrue(inventoryPage.canAddProduct(removedProduct), "Removed product can be added again");
        softly.assertTrue(inventoryPage.canRemoveProduct(remainingProduct), "Remaining product can still be removed");
        softly.assertAll("One product remains in cart");
    }

    @Test
    public void productDetailsCanBeOpened() {
        ProductDetailsPage detailsPage = inventoryPage.openBackpack();

        assertEquals(detailsPage.getProductName(), "Sauce Labs Backpack", "Product details name");
    }

    @Test
    public void productDetailsCanReturnToInventory() {
        ProductDetailsPage detailsPage = inventoryPage.openBackpack();

        inventoryPage = detailsPage.backToProducts();

        SoftAssert softly = new SoftAssert();
        softly.assertEquals(inventoryPage.getHeading(), "Products");
        softly.assertTrue(inventoryPage.hasVisibleItem());
        softly.assertAll("Return to inventory");
    }
}
