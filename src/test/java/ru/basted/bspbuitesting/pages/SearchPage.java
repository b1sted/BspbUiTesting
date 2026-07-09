package ru.basted.bspbuitesting.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import ru.basted.bspbuitesting.base.BasePage;

public class SearchPage extends BasePage<SearchPage> {
    private static final String PAGE_URL = "https://bspb.ru/search";

    private final By searchBoxLocator = By.cssSelector("input[placeholder]");
    private final By searchStatusLocator = By.xpath("//p[contains(normalize-space(), 'Найдено')]");

    public SearchPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    protected String getUrl() {
        return PAGE_URL;
    }

    @Step("Выполнение поискового запроса")
    public boolean isSearchSuccessful(String query) {
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
