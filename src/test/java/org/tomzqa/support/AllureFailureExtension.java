package org.tomzqa.support;

import io.qameta.allure.Allure;
import io.qameta.allure.AttachmentOptions;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.opentest4j.TestAbortedException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.ByteArrayInputStream;

public class AllureFailureExtension implements TestExecutionExceptionHandler {
    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        // Aborted tests are not failures and should not receive failure attachments.
        if (!(throwable instanceof TestAbortedException)
                && context.getRequiredTestInstance() instanceof WebDriverProvider provider) {
            WebDriver driver = provider.getWebDriver();
            if (driver != null) {
                // Capture independently so one unavailable attachment does not prevent the other.
                try {
                    Allure.attachment("Current browser URL", "text/plain", driver.getCurrentUrl(),
                            AttachmentOptions.empty());
                } catch (RuntimeException exception) {
                    System.err.println("Could not attach browser URL: " + exception.getMessage());
                }

                try {
                    byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                    Allure.attachment("Failure screenshot", "image/png", new ByteArrayInputStream(screenshot),
                            AttachmentOptions.empty());
                } catch (RuntimeException exception) {
                    System.err.println("Could not attach failure screenshot: " + exception.getMessage());
                }
            }
        }

        throw throwable;
    }
}
