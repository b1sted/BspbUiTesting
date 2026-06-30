package ru.basted.bspbuitesting.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import ru.basted.bspbuitesting.base.BasePage;

public class SearchPage extends BasePage {
    private final By searchBoxLocator = By.cssSelector("input[placeholder]");
    private final By searchStatusLocator = By.xpath("//p[contains(normalize-space(), 'Найдено')]");

    public SearchPage(WebDriver webDriver) {
        super(webDriver);
    }

    public boolean isSearchPageOpened() {
        try {
            return webDriverWait.until(ExpectedConditions.urlContains("/search"));
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean searchAndVerifyResults(String query) {
        WebElement searchBox = webDriverWait.until(ExpectedConditions.elementToBeClickable(searchBoxLocator));

        searchBox.sendKeys(query);
        searchBox.sendKeys(Keys.ENTER);

        try {
            return webDriverWait.until(ExpectedConditions.not(
                    ExpectedConditions.textToBePresentInElementLocated(searchStatusLocator, "0")
            ));
        } catch (TimeoutException ex) {
            return false;
        }
    }
}
