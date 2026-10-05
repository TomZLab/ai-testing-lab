package org.tomzqa.support;

import org.openqa.selenium.chrome.ChromeOptions;

import java.util.HashMap;
import java.util.Map;

final class ChromeOptionsHelper {
    private ChromeOptionsHelper() {
    }

    static ChromeOptions create() {
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        prefs.put("profile.password_manager_leak_detection", false);
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("prefs", prefs);
        options.addArguments("--headless=new", "--window-size=1280,800");
        return options;
    }
}
