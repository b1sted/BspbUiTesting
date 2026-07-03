package ru.basted.bspbuitesting.base;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class BasePage {
    protected final WebDriver webDriver;
    protected final WebDriverWait webDriverWait;
    protected final WebDriverWait webDriverShortWait;

    public BasePage(WebDriver webDriver) {
        this.webDriver = webDriver;
        this.webDriverWait = new WebDriverWait(webDriver, Duration.ofSeconds(10));
        this.webDriverShortWait = new WebDriverWait(webDriver, Duration.ofSeconds(2));
    }

    public void clickOnElement(WebElement webElement) {
        try {
            webElement.click();
        } catch (ElementClickInterceptedException e) {
            new Actions(webDriver)
                    .moveToElement(webElement)
                    .click()
                    .perform();
        }
    }

    public void clickOnElement(By webElementLocator) {
        WebElement webElement = webDriverWait.until(ExpectedConditions.elementToBeClickable(webElementLocator));
        clickOnElement(webElement);
    }
}
