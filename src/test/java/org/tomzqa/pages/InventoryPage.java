package org.tomzqa.pages;

import java.math.BigDecimal;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Quotes;
import org.openqa.selenium.support.ui.Select;

public class InventoryPage extends BasePage {
    private final By items = By.cssSelector(".inventory_list .inventory_item");

    public InventoryPage(WebDriver driver) {
        super(driver);
    }

    public void waitUntilLoaded() {
        wait.until(ExpectedConditions.urlToBe(LoginPage.BASE_URL + "inventory.html"));
        visible(items);
    }

    public String getHeading() {
        return visible(By.className("title")).getText();
    }

    public boolean hasVisibleItem() {
        return driver.findElements(items).stream().anyMatch(WebElement::isDisplayed);
    }

    public List<String> getProductNames() {
        return driver.findElements(By.className("inventory_item_name")).stream().map(WebElement::getText).toList();
    }

    public List<BigDecimal> getProductPrices() {
        return driver.findElements(By.className("inventory_item_price")).stream()
                .map(element -> new BigDecimal(element.getText().replace("$", ""))).toList();
    }

    public void sortByNameAscending() {
        sortBy("az");
    }

    public void sortByNameDescending() {
        sortBy("za");
    }

    public void sortByPriceAscending() {
        sortBy("lohi");
    }

    public void sortByPriceDescending() {
        sortBy("hilo");
    }

    private void sortBy(String value) {
        new Select(wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("[data-test='product-sort-container']")))).selectByValue(value);
    }

    public void addProductToCart(String productName) {
        waitForProductButton(productName, "Add to cart").click();
        waitForProductButton(productName, "Remove");
    }

    public void removeProductFromCart(String productName) {
        waitForProductButton(productName, "Remove").click();
        waitForProductButton(productName, "Add to cart");
    }

    private WebElement waitForProductButton(String productName, String buttonText) {
        return wait.until(ExpectedConditions.refreshed(
                ExpectedConditions.elementToBeClickable(findProductButton(productName, buttonText))));
    }

    private By findProductButton(String productName, String buttonText) {
        return By.xpath("//*[@data-test='inventory-item']"
                + "[.//*[@data-test='inventory-item-name' and normalize-space(.)=" + Quotes.escape(productName) + "]]"
                + "//button[normalize-space(.)=" + Quotes.escape(buttonText) + "]");
    }

    public int getCartCount() {
        List<WebElement> badges = driver.findElements(By.className("shopping_cart_badge"));
        return badges.isEmpty() ? 0 : Integer.parseInt(badges.getFirst().getText());
    }

    public boolean canRemoveProduct(String productName) {
        return ExpectedConditions.elementToBeClickable(findProductButton(productName, "Remove")).apply(driver) != null;
    }

    public boolean canAddProduct(String productName) {
        return ExpectedConditions.elementToBeClickable(findProductButton(productName, "Add to cart")).apply(driver) != null;
    }

    public ProductDetailsPage openBackpack() {
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Sauce Labs Backpack"))).click();
        ProductDetailsPage detailsPage = new ProductDetailsPage(driver);
        detailsPage.waitUntilLoaded();
        return detailsPage;
    }
}
