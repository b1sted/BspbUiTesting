package ru.basted.bspbuitesting.base;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class BasePage {
    protected final WebDriver webDriver;
    protected final WebDriverWait webDriverWait;

    public BasePage(WebDriver webDriver) {
        this.webDriver = webDriver;
        this.webDriverWait = new WebDriverWait(webDriver, Duration.ofSeconds(10));
    }

    public void clickOnElement(By webElementLocator) {
        WebElement webElement = webDriverWait.until(ExpectedConditions.elementToBeClickable(webElementLocator));

        try {
            webElement.click();
        } catch (ElementClickInterceptedException e) {
            new Actions(webDriver)
                    .moveToElement(webElement)
                    .click()
                    .perform();
        }
    }

    public boolean isPageOpened(String fraction) {
        try {
            return webDriverWait.until(ExpectedConditions.urlContains(fraction));
        } catch (TimeoutException ex) {
            return false;
        }
    }
}
