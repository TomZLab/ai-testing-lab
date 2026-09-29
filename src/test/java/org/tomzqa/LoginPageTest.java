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
    void loginPageDisplaysRequiredElements() {
        driver.get("https://www.saucedemo.com/");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
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
