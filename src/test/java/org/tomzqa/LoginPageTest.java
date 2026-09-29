package org.tomzqa;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginPageTest {
    private static final String BASE_URL = "https://www.saucedemo.com/";
    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(10);

    private WebDriver driver;

    @BeforeEach
    void openChrome() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--window-size=1280,800");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
    }

    @AfterEach
    void closeChrome() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void validUserCanLogIn() {
        logIn("standard_user", "secret_sauce");

        WebDriverWait wait = new WebDriverWait(driver, WAIT_TIMEOUT);
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "inventory.html"));
        WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("title")));
        WebElement inventoryItem = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".inventory_list .inventory_item")));

        assertAll("Successful login",
                () -> assertEquals("Products", title.getText(), "Inventory page heading"),
                () -> assertTrue(inventoryItem.isDisplayed(), "At least one inventory item should be visible"));
    }

    @Test
    void invalidPasswordShowsError() {
        logIn("standard_user", "invalid_password");

        assertLoginError("Epic sadface: Username and password do not match any user in this service");
    }

    @Test
    void emptyUsernameShowsError() {
        logIn("", "secret_sauce");

        assertLoginError("Epic sadface: Username is required");
    }

    @Test
    void emptyPasswordShowsError() {
        logIn("standard_user", "");

        assertLoginError("Epic sadface: Password is required");
    }

    private void logIn(String username, String password) {
        driver.get(BASE_URL);

        WebDriverWait wait = new WebDriverWait(driver, WAIT_TIMEOUT);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user-name"))).sendKeys(username);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("password"))).sendKeys(password);
        wait.until(ExpectedConditions.elementToBeClickable(By.id("login-button"))).click();
    }

    private void assertLoginError(String expectedMessage) {
        WebDriverWait wait = new WebDriverWait(driver, WAIT_TIMEOUT);
        WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-test='error']")));

        assertAll("Rejected login",
                () -> assertEquals(expectedMessage, error.getText(), "Login error message"),
                () -> assertEquals(BASE_URL, driver.getCurrentUrl(),
                        "User should remain on the login page"),
                () -> assertTrue(driver.findElement(By.id("login-button")).isDisplayed(),
                        "Login form should remain visible"));
    }

    @Test
    void loginPageDisplaysRequiredElements() {
        driver.get(BASE_URL);

        WebDriverWait wait = new WebDriverWait(driver, WAIT_TIMEOUT);
        WebElement logo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("login_logo")));
        WebElement username = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user-name")));
        WebElement password = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("password")));
        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("login-button")));

        assertAll("Login page",
                () -> assertEquals("Swag Labs", driver.getTitle(), "Page title"),
                () -> assertEquals("Swag Labs", logo.getText(), "Visible branding"),
                () -> assertTrue(username.isEnabled(), "Username field should be enabled"),
                () -> assertEquals("Username", username.getDomAttribute("placeholder")),
                () -> assertTrue(password.isEnabled(), "Password field should be enabled"),
                () -> assertEquals("Password", password.getDomAttribute("placeholder")),
                () -> assertEquals("password", password.getDomAttribute("type"), "Password should be masked"),
                () -> assertEquals("Login", loginButton.getDomAttribute("value")));
    }
}
