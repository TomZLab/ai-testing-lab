package org.tomzqa.support;

import io.qameta.allure.Allure;
import io.qameta.allure.AttachmentOptions;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

public class AllureFailureListener implements ITestListener {
    @Override
    public void onTestFailure(ITestResult result) {
        if (!(result.getInstance() instanceof WebDriverProvider provider)) {
            return;
        }

        WebDriver driver = provider.getWebDriver();
        if (driver == null) {
            return;
        }

        // Capture independently so one unavailable attachment does not prevent the other.
        try {
            Allure.getLifecycle().addAttachment("Current browser URL", "text/plain",
                    new ByteArrayInputStream(driver.getCurrentUrl().getBytes(StandardCharsets.UTF_8)),
                    AttachmentOptions.empty());
        } catch (RuntimeException exception) {
            System.err.println("Could not attach browser URL: " + exception.getMessage());
        }

        try {
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Allure.getLifecycle().addAttachment("Failure screenshot", "image/png",
                    new ByteArrayInputStream(screenshot), AttachmentOptions.empty());
        } catch (RuntimeException exception) {
            System.err.println("Could not attach failure screenshot: " + exception.getMessage());
        }
    }
}
