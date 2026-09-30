package org.tomzqa.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class ProductDetailsPage extends BasePage {

    public ProductDetailsPage(WebDriver driver) {
        super(driver);
    }

    public void waitUntilLoaded() {
        visible(By.cssSelector(".inventory_details .inventory_details_name"));
        wait.until(ExpectedConditions.elementToBeClickable(By.id("back-to-products")));
    }

    public String getProductName() {
        return visible(By.cssSelector(".inventory_details .inventory_details_name")).getText();
    }

    public InventoryPage backToProducts() {
        wait.until(ExpectedConditions.elementToBeClickable(By.id("back-to-products"))).click();
        InventoryPage inventoryPage = new InventoryPage(driver);
        inventoryPage.waitUntilLoaded();
        return inventoryPage;
    }
}
