package ru.basted.bspbuitesting.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import ru.basted.bspbuitesting.base.BasePage;

public class MainPage extends BasePage {
    private final By searchSvgLocator = By.cssSelector("nav a[href='/search'] svg.chakra-icon");
    private final By buyCurrencyLocator = By.xpath("//a[@href='/finance/exchange' and contains(text(),'валюту')]");

    public MainPage(WebDriver webDriver) {
        super(webDriver);
    }

    public void clickSearchButton() {
        clickOnElement(searchSvgLocator);
    }

    public void clickBuyCurrency() {
        clickOnElement(buyCurrencyLocator);
    }
}
