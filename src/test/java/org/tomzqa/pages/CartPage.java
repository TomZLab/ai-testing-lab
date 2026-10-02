package org.tomzqa.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class CartPage extends BasePage {
    public CartPage(WebDriver driver) {
        super(driver);
    }

    public void waitUntilLoaded() {
        wait.until(ExpectedConditions.urlToBe(LoginPage.BASE_URL + "cart.html"));
        visible(By.className("cart_list"));
    }

    public List<String> getProductNames() {
        return driver.findElements(By.cssSelector(".cart_item .inventory_item_name")).stream()
                .map(WebElement::getText).toList();
    }
}
