package ru.basted.bspbuitesting.steps;

import java.time.Duration;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class UiValidationSteps {
    public static void verifyCurrentUrlContains(WebDriver webDriver, String expectedUrlPart) {
        WebDriverWait webDriverWait = new WebDriverWait(webDriver, Duration.ofSeconds(5));

        webDriverWait.until(ExpectedConditions.urlContains(expectedUrlPart));

        Assertions.assertThat(webDriver.getCurrentUrl())
                .as("Браузер должен был перейти на страницу, содержащую URI: %s", expectedUrlPart)
                .contains(expectedUrlPart);
    }

    public static void verifyAllStatuses(
            SoftAssertions softAssertions,
            Map<Integer, Boolean> verificationResults,
            String errorMessage
    ) {
        verificationResults.forEach((elementIndex, isSuccess) ->
                softAssertions.assertThat(isSuccess)
                        .withFailMessage(errorMessage, elementIndex)
                        .isTrue());
    }
}
