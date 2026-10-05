package org.tomzqa.support;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.tomzqa.pages.LoginPage;

import java.time.Duration;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(AllureFailureExtension.class)
public abstract class BaseScenarioTest implements WebDriverProvider {
    private WebDriver driver;

    @Override
    public WebDriver getWebDriver() {
        return driver;
    }

    @BeforeAll
    protected void openChromeAndLogIn() {
        driver = new ChromeDriver(ChromeOptionsHelper.create());
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));

        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPage.logIn(TestConfig.STANDARD_USERNAME, TestConfig.STANDARD_PASSWORD);
    }

    @AfterAll
    protected void closeChrome() {
        if (driver != null) {
            try {
                driver.quit();
            } finally {
                driver = null;
            }
        }
    }
}
