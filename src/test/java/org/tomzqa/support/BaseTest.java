package org.tomzqa.support;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

@ExtendWith(AllureFailureExtension.class)
public abstract class BaseTest implements WebDriverProvider {
    protected WebDriver driver;

    @Override
    public WebDriver getWebDriver() {
        return driver;
    }

    @BeforeEach
    protected void openChrome() {
        driver = new ChromeDriver(ChromeOptionsHelper.create());
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
    }

    @AfterEach
    protected void closeChrome() {
        if (driver != null) {
            driver.quit();
        }
    }
}
