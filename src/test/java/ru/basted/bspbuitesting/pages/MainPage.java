package ru.basted.bspbuitesting.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

import ru.basted.bspbuitesting.base.BasePage;

public class MainPage extends BasePage {
    private final By searchSvgLocator = By.cssSelector("nav a[href='/search'] svg.chakra-icon");

    public MainPage(WebDriver webDriver) {
        super(webDriver);
    }

    public void clickSearchButton() {
        WebElement svgElement = webDriverWait.until(ExpectedConditions.presenceOfElementLocated(searchSvgLocator));

        new Actions(webDriver)
                .moveToElement(svgElement)
                .click()
                .perform();
    }
}
