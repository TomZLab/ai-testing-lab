package org.tomzqa.support;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.tomzqa.pages.InventoryPage;
import org.tomzqa.pages.LoginPage;

import java.time.Duration;

public abstract class BaseScenarioTest implements WebDriverProvider {
    private WebDriver driver;
    protected InventoryPage inventoryPage;

    @Override
    public WebDriver getWebDriver() {
        return driver;
    }

    @BeforeClass
    public void openChromeAndLogIn() {
        driver = new ChromeDriver(ChromeOptionsHelper.create());
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));

        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPage.logIn(TestConfig.STANDARD_USERNAME, TestConfig.STANDARD_PASSWORD);
        inventoryPage = new InventoryPage(driver);
        inventoryPage.waitUntilLoaded();
    }

    @AfterClass(alwaysRun = true)
    public void closeChrome() {
        try {
            if (driver != null) {
                driver.quit();
            }
        } finally {
            driver = null;
            inventoryPage = null;
        }
    }
}
