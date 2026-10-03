package org.tomzqa;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tomzqa.pages.InventoryPage;
import org.tomzqa.pages.LoginPage;
import org.tomzqa.pages.ProductDetailsPage;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InventoryPageTest extends BaseTest {
    private InventoryPage inventoryPage;

    @BeforeEach
    void logIn() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPage.logIn(TestConfig.STANDARD_USERNAME, TestConfig.STANDARD_PASSWORD);
        inventoryPage = new InventoryPage(driver);
        inventoryPage.waitUntilLoaded();
    }

    @Test
    void inventoryDisplaysProductsAndPrices() {
        assertAll("Inventory",
                () -> assertEquals("Products", inventoryPage.getHeading()),
                () -> assertEquals(6, inventoryPage.getProductNames().size(), "Expected demo product count"),
                () -> assertTrue(inventoryPage.getProductNames().contains("Sauce Labs Backpack")),
                () -> assertEquals(6, inventoryPage.getProductPrices().size()),
                () -> assertTrue(inventoryPage.getProductPrices().stream().allMatch(price -> price.signum() > 0)),
                () -> assertEquals(0, inventoryPage.getCartCount(), "Cart should start empty"));
    }

    @Test
    void productsCanBeSortedByNameAscending() {
        inventoryPage.sortByNameDescending();
        List<String> descendingNames = inventoryPage.getProductNames();
        List<String> expectedDescendingNames = descendingNames.stream().sorted(Comparator.reverseOrder()).toList();
        List<String> expectedAscendingNames = descendingNames.stream().sorted().toList();

        inventoryPage.sortByNameAscending();
        List<String> actualAscendingNames = inventoryPage.getProductNames();

        assertAll("Name ascending",
                () -> assertEquals(6, descendingNames.size()),
                () -> assertEquals(expectedDescendingNames, descendingNames, "Setup should be sorted descending"),
                () -> assertNotEquals(descendingNames, expectedAscendingNames, "Descending setup should differ from ascending order"),
                () -> assertEquals(expectedAscendingNames, actualAscendingNames, "Products should be sorted ascending"));
    }

    @Test
    void productsCanBeSortedByNameDescending() {
        inventoryPage.sortByNameAscending();
        List<String> ascendingNames = inventoryPage.getProductNames();
        List<String> expectedAscendingNames = ascendingNames.stream().sorted().toList();
        List<String> expectedDescendingNames = ascendingNames.stream().sorted(Comparator.reverseOrder()).toList();

        inventoryPage.sortByNameDescending();
        List<String> actualDescendingNames = inventoryPage.getProductNames();

        assertAll("Name descending",
                () -> assertEquals(6, ascendingNames.size()),
                () -> assertEquals(expectedAscendingNames, ascendingNames, "Setup should be sorted ascending"),
                () -> assertNotEquals(ascendingNames, expectedDescendingNames, "Ascending setup should differ from descending order"),
                () -> assertEquals(expectedDescendingNames, actualDescendingNames, "Products should be sorted descending"));
    }

    @Test
    void productsCanBeSortedByPriceAscending() {
        List<BigDecimal> prices = inventoryPage.getProductPrices();
        List<BigDecimal> expectedPrices = prices.stream().sorted().toList();

        inventoryPage.sortByPriceAscending();

        assertAll("Price ascending",
                () -> assertEquals(6, prices.size()),
                () -> assertEquals(expectedPrices, inventoryPage.getProductPrices()));
    }

    @Test
    void productsCanBeSortedByPriceDescending() {
        List<BigDecimal> prices = inventoryPage.getProductPrices();
        List<BigDecimal> expectedPrices = prices.stream().sorted(Comparator.reverseOrder()).toList();

        inventoryPage.sortByPriceDescending();

        assertAll("Price descending",
                () -> assertEquals(6, prices.size()),
                () -> assertEquals(expectedPrices, inventoryPage.getProductPrices()));
    }

    @Test
    void productCanBeAddedToCart() {
        String productName = "Sauce Labs Backpack";
        int initialCartCount = inventoryPage.getCartCount();

        inventoryPage.addProductToCart(productName);

        assertAll("Product added",
                () -> assertEquals(0, initialCartCount, "Cart should start empty"),
                () -> assertEquals(1, inventoryPage.getCartCount(), "Cart count after adding"),
                () -> assertTrue(inventoryPage.canRemoveProduct(productName), "Remove button after adding"));
    }

    @Test
    void productCanBeRemovedFromCart() {
        String productName = "Test.allTheThings() T-Shirt (Red)";
        inventoryPage.addProductToCart(productName);
        int initialCartCount = inventoryPage.getCartCount();

        inventoryPage.removeProductFromCart(productName);

        assertAll("Product removed",
                () -> assertEquals(1, initialCartCount, "Cart should contain the product before removal"),
                () -> assertEquals(0, inventoryPage.getCartCount(), "Cart count after removing"),
                () -> assertTrue(inventoryPage.canAddProduct(productName), "Add button after removing"));
    }

    @Test
    void removingOneProductKeepsTheOtherInCart() {
        String removedProduct = "Sauce Labs Backpack";
        String remainingProduct = "Sauce Labs Bike Light";
        int initialCartCount = inventoryPage.getCartCount();

        inventoryPage.addProductToCart(removedProduct);
        inventoryPage.addProductToCart(remainingProduct);
        inventoryPage.removeProductFromCart(removedProduct);

        assertAll("One product remains in cart",
                () -> assertEquals(0, initialCartCount, "Cart should start empty"),
                () -> assertEquals(1, inventoryPage.getCartCount(), "Cart should contain one product"),
                () -> assertTrue(inventoryPage.canAddProduct(removedProduct), "Removed product can be added again"),
                () -> assertTrue(inventoryPage.canRemoveProduct(remainingProduct), "Remaining product can still be removed"));
    }

    @Test
    void productDetailsCanBeOpened() {
        ProductDetailsPage detailsPage = inventoryPage.openBackpack();

        assertEquals("Sauce Labs Backpack", detailsPage.getProductName(), "Product details name");
    }

    @Test
    void productDetailsCanReturnToInventory() {
        ProductDetailsPage detailsPage = inventoryPage.openBackpack();

        inventoryPage = detailsPage.backToProducts();

        assertAll("Return to inventory",
                () -> assertEquals("Products", inventoryPage.getHeading()),
                () -> assertTrue(inventoryPage.hasVisibleItem()));
    }
}
