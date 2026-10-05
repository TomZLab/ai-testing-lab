package org.tomzqa.support;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.time.Duration;

public abstract class BaseTest implements WebDriverProvider {
    protected WebDriver driver;

    @Override
    public WebDriver getWebDriver() {
        return driver;
    }

    @BeforeMethod
    public void openChrome() {
        driver = new ChromeDriver(ChromeOptionsHelper.create());
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
    }

    @AfterMethod(alwaysRun = true)
    public void closeChrome() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}
