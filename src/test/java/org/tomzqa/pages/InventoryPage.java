package org.tomzqa.pages;

import java.math.BigDecimal;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

public class InventoryPage extends BasePage {
    private final By items = By.cssSelector(".inventory_list .inventory_item");
    private final By addBackpackButton = By.id("add-to-cart-sauce-labs-backpack");
    private final By removeBackpackButton = By.id("remove-sauce-labs-backpack");

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

    public void addBackpackToCart() {
        wait.until(ExpectedConditions.elementToBeClickable(addBackpackButton)).click();
        wait.until(ExpectedConditions.elementToBeClickable(removeBackpackButton));
    }

    public void removeBackpackFromCart() {
        wait.until(ExpectedConditions.elementToBeClickable(removeBackpackButton)).click();
        wait.until(ExpectedConditions.elementToBeClickable(addBackpackButton));
    }

    public int getCartCount() {
        List<WebElement> badges = driver.findElements(By.className("shopping_cart_badge"));
        return badges.isEmpty() ? 0 : Integer.parseInt(badges.getFirst().getText());
    }

    public boolean canRemoveBackpack() {
        return driver.findElements(removeBackpackButton).stream()
                .anyMatch(button -> button.isDisplayed() && button.isEnabled());
    }

    public boolean canAddBackpack() {
        return driver.findElements(addBackpackButton).stream()
                .anyMatch(button -> button.isDisplayed() && button.isEnabled());
    }

    public ProductDetailsPage openBackpack() {
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Sauce Labs Backpack"))).click();
        ProductDetailsPage detailsPage = new ProductDetailsPage(driver);
        detailsPage.waitUntilLoaded();
        return detailsPage;
    }
}
