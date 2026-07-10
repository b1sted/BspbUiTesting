package ru.basted.bspbuitesting.pages;

import java.util.HashMap;
import java.util.Map;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import ru.basted.bspbuitesting.base.BasePage;

public class MainPage extends BasePage<MainPage> {
    private static final String PAGE_URL = "https://bspb.ru";

    private final By pageWrapperLocator = By.xpath("//div[@id='app-wrapper']");

    private final By logoSvgLocator = By.xpath(
            "//div[contains(@class, 'chakra-container')]//a[@href=\"/\"]//*[name()='svg']"
    );

    private final By searchSvgLocator = By.cssSelector("nav a[href='/search']");

    private final By adsLocator = By.xpath("(//main//div[contains(@class, 'container')])[1]//a");
    private static final String AD_XPATH_TEMPLATE = "((//main//div[contains(@class, 'container')])[1]//a)[%d]";

    private final By processButtonLocator = By.xpath(
            "//a[contains(@href, 'deposits') and contains(normalize-space(), 'Оформить')]"
    );

    private final By buyCurrencyLocator = By.xpath("//a[@href='/finance/exchange' and contains(text(),'валюту')]");

    public MainPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    protected String getUrl() {
        return PAGE_URL;
    }

    public WebElement getWrapper() {
        return webDriver.findElement(pageWrapperLocator);
    }

    public WebElement getLogo() {
        return webDriver.findElement(logoSvgLocator);
    }

    public WebElement getProcessButton() {
        return webDriver.findElement(processButtonLocator);
    }

    @Step("Нажатие на кнопку поиска")
    public void clickSearchButton() {
        WebElement searchIcon = webDriverWait.until(ExpectedConditions.presenceOfElementLocated(searchSvgLocator));
        ((JavascriptExecutor) webDriver).executeScript("arguments[0].click();", searchIcon);
    }

    @Step("Нажатие на все рекламные элементы")
    public Map<Integer, String> clickOnAllAds() {
        Map<Integer, String> results = new HashMap<>();
        String baseUrl = webDriver.getCurrentUrl();

        int adsItemsCount = webDriverWait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(adsLocator)).size();
        for (int i = 0; i < adsItemsCount; i++) {
            String adXpath = String.format(AD_XPATH_TEMPLATE, i + 1);
            By adLocator = By.xpath(adXpath);
            WebElement adElement = webDriverWait.until(ExpectedConditions.presenceOfElementLocated(adLocator));

            try {
                webDriverShortWait.until(ExpectedConditions.elementToBeClickable(adElement));

                clickOnElement(adElement);
                webDriverShortWait.until(ExpectedConditions.not(ExpectedConditions.urlToBe(baseUrl)));
                results.put(i, webDriver.getCurrentUrl());

                webDriver.navigate().back();
                webDriver.navigate().refresh();
            } catch (RuntimeException ex) {
                results.put(i, "");
            }
        }

        return results;
    }

    @Step("Нажатие на кнопку переадресации на страницу обмена валют")
    public void clickBuyCurrency() {
        clickOnElement(buyCurrencyLocator);
    }
}
