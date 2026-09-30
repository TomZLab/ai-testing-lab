package org.tomzqa;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tomzqa.pages.InventoryPage;
import org.tomzqa.pages.LoginPage;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginPageTest extends BaseTest {
    private LoginPage loginPage;

    @BeforeEach
    void openLoginPage() {
        loginPage = new LoginPage(driver);
        loginPage.open();
    }

    @Test
    void validUserCanLogIn() {
        loginPage.logIn("standard_user", "secret_sauce");
        InventoryPage inventoryPage = new InventoryPage(driver);
        inventoryPage.waitUntilLoaded();

        assertAll("Successful login",
                () -> assertEquals("Products", inventoryPage.getHeading(), "Inventory page heading"),
                () -> assertTrue(inventoryPage.hasVisibleItem(), "At least one inventory item should be visible"));
    }

    @Test
    void invalidPasswordShowsError() {
        loginPage.logIn("standard_user", "invalid_password");
        assertLoginError("Epic sadface: Username and password do not match any user in this service");
    }

    @Test
    void emptyUsernameShowsError() {
        loginPage.logIn("", "secret_sauce");
        assertLoginError("Epic sadface: Username is required");
    }

    @Test
    void emptyPasswordShowsError() {
        loginPage.logIn("standard_user", "");
        assertLoginError("Epic sadface: Password is required");
    }

    private void assertLoginError(String expectedMessage) {
        assertAll("Rejected login",
                () -> assertEquals(expectedMessage, loginPage.getErrorMessage(), "Login error message"),
                () -> assertTrue(loginPage.isDisplayed(), "User should remain on the login page"));
    }

    @Test
    void loginPageDisplaysRequiredElements() {
        assertAll("Login page",
                () -> assertEquals("Swag Labs", loginPage.getTitle(), "Page title"),
                () -> assertEquals("Swag Labs", loginPage.getLogoText(), "Visible branding"),
                () -> assertTrue(loginPage.isUsernameEnabled(), "Username field should be enabled"),
                () -> assertEquals("Username", loginPage.getUsernamePlaceholder()),
                () -> assertTrue(loginPage.isPasswordEnabled(), "Password field should be enabled"),
                () -> assertEquals("Password", loginPage.getPasswordPlaceholder()),
                () -> assertEquals("password", loginPage.getPasswordType(), "Password should be masked"),
                () -> assertTrue(loginPage.isLoginButtonEnabled(), "Login button should be enabled"),
                () -> assertEquals("Login", loginPage.getLoginButtonLabel()));
    }
}
