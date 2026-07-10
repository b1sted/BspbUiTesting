package ru.basted.bspbuitesting.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import ru.basted.bspbuitesting.base.BasePage;

public class FeedbackPage extends BasePage<FeedbackPage> {
    private static final String PAGE_URL = "https://bspb.ru/retail/feedback/fl";

    private final By formApplicationLocator = By.xpath("//div[h2[contains(normalize-space(), 'обратн')]]");

    public FeedbackPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    protected String getUrl() {
        return PAGE_URL;
    }

    public WebElement getContactFormContainer() {
        return webDriver.findElement(formApplicationLocator);
    }
}
