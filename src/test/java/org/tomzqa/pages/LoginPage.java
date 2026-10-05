package org.tomzqa.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.tomzqa.support.TestConfig;

public class LoginPage extends BasePage {
    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        driver.get(TestConfig.BASE_URL);
        visible(By.id("login-button"));
    }

    public void logIn(String username, String password) {
        WebElement usernameInput = visible(By.id("user-name"));
        WebElement passwordInput = visible(By.id("password"));
        usernameInput.clear();
        passwordInput.clear();
        usernameInput.sendKeys(username);
        passwordInput.sendKeys(password);
        wait.until(ExpectedConditions.elementToBeClickable(By.id("login-button"))).click();
    }

    public String getErrorMessage() {
        return visible(By.cssSelector("[data-test='error']")).getText();
    }

    public String getTitle() {
        return driver.getTitle();
    }

    public String getLogoText() {
        return visible(By.className("login_logo")).getText();
    }

    public boolean isUsernameEnabled() {
        return visible(By.id("user-name")).isEnabled();
    }

    public boolean isPasswordEnabled() {
        return visible(By.id("password")).isEnabled();
    }

    public String getUsernamePlaceholder() {
        return visible(By.id("user-name")).getDomAttribute("placeholder");
    }

    public String getPasswordPlaceholder() {
        return visible(By.id("password")).getDomAttribute("placeholder");
    }

    public String getPasswordType() {
        return visible(By.id("password")).getDomAttribute("type");
    }

    public String getLoginButtonLabel() {
        return visible(By.id("login-button")).getDomAttribute("value");
    }

    public boolean isLoginButtonEnabled() {
        return visible(By.id("login-button")).isEnabled();
    }

    public boolean isDisplayed() {
        return TestConfig.BASE_URL.equals(driver.getCurrentUrl())
                && driver.findElements(By.id("login-button")).stream().anyMatch(WebElement::isDisplayed);
    }
}
