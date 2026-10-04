package org.tomzqa;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.tomzqa.pages.InventoryPage;
import org.tomzqa.pages.LoginPage;

import org.testng.asserts.SoftAssert;

public class LoginPageTest extends BaseTest {
    private LoginPage loginPage;

    @BeforeMethod
    public void openLoginPage() {
        loginPage = new LoginPage(driver);
        loginPage.open();
    }

    @Test
    public void validUserCanLogIn() {
        loginPage.logIn(TestConfig.STANDARD_USERNAME, TestConfig.STANDARD_PASSWORD);
        InventoryPage inventoryPage = new InventoryPage(driver);
        inventoryPage.waitUntilLoaded();

        SoftAssert softly = new SoftAssert();
        softly.assertEquals(inventoryPage.getHeading(), "Products", "Inventory page heading");
        softly.assertTrue(inventoryPage.hasVisibleItem(), "At least one inventory item should be visible");
        softly.assertAll("Successful login");
    }

    @Test
    public void invalidPasswordShowsError() {
        loginPage.logIn(TestConfig.STANDARD_USERNAME, "invalid_password");
        assertLoginError("Epic sadface: Username and password do not match any user in this service");
    }

    @Test
    public void emptyUsernameShowsError() {
        loginPage.logIn("", TestConfig.STANDARD_PASSWORD);
        assertLoginError("Epic sadface: Username is required");
    }

    @Test
    public void emptyPasswordShowsError() {
        loginPage.logIn(TestConfig.STANDARD_USERNAME, "");
        assertLoginError("Epic sadface: Password is required");
    }

    private void assertLoginError(String expectedMessage) {
        SoftAssert softly = new SoftAssert();
        softly.assertEquals(loginPage.getErrorMessage(), expectedMessage, "Login error message");
        softly.assertTrue(loginPage.isDisplayed(), "User should remain on the login page");
        softly.assertAll("Rejected login");
    }

    @Test
    public void loginPageDisplaysRequiredElements() {
        SoftAssert softly = new SoftAssert();
        softly.assertEquals(loginPage.getTitle(), "Swag Labs", "Page title");
        softly.assertEquals(loginPage.getLogoText(), "Swag Labs", "Visible branding");
        softly.assertTrue(loginPage.isUsernameEnabled(), "Username field should be enabled");
        softly.assertEquals(loginPage.getUsernamePlaceholder(), "Username");
        softly.assertTrue(loginPage.isPasswordEnabled(), "Password field should be enabled");
        softly.assertEquals(loginPage.getPasswordPlaceholder(), "Password");
        softly.assertEquals(loginPage.getPasswordType(), "password", "Password should be masked");
        softly.assertTrue(loginPage.isLoginButtonEnabled(), "Login button should be enabled");
        softly.assertEquals(loginPage.getLoginButtonLabel(), "Login");
        softly.assertAll("Login page");
    }
}
