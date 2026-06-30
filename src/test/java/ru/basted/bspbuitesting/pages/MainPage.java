package ru.basted.bspbuitesting.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import ru.basted.bspbuitesting.base.BasePage;

public class MainPage extends BasePage {
    private final By searchSvgLocator = By.cssSelector("nav a[href='/search'] svg.chakra-icon");
    private final By buyCurrencyLocator = By.xpath("//a[@href='/finance/exchange' and contains(text(),'валюту')]");

    public MainPage(WebDriver webDriver) {
        super(webDriver);
    }

    public void clickSearchButton() {
        WebElement svgElement = webDriverWait.until(ExpectedConditions.presenceOfElementLocated(searchSvgLocator));
        clickOnElement(svgElement);
    }

    public void clickBuyCurrency() {
        WebElement currencyButton = webDriverWait.until(ExpectedConditions.presenceOfElementLocated(buyCurrencyLocator));
        clickOnElement(currencyButton);
    }
}
