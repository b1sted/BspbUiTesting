package ru.basted.bspbuitesting.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import ru.basted.bspbuitesting.base.BasePage;

public class CurrencyPage extends BasePage {
    private final By questionsLocator = By.xpath("//button[normalize-space()='Вопросы']");
    private final By targetAccordionButtonsLocator = By.cssSelector(
            "div.chakra-tabs__tab-panel:not([hidden]) button.chakra-accordion__button"
    );
    private final By collapsePanelLocator = By.xpath("following-sibling::div[contains(@class, 'chakra-collapse')]");

    public CurrencyPage(WebDriver webDriver) {
        super(webDriver);
    }

    public boolean isCurrencyPageOpened() {
        try {
            return webDriverWait.until(ExpectedConditions.urlContains("/finance/exchange"));
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean unfoldQuestionsAndVerify() {
        clickOnElement(questionsLocator);

        try {
            webDriverWait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(targetAccordionButtonsLocator));

            List<WebElement> accordionButtons = webDriver.findElements(targetAccordionButtonsLocator);
            for (WebElement accordionButton : accordionButtons) {
                accordionButton.click();

                WebElement collapsePanel = accordionButton.findElement(collapsePanelLocator);
                webDriverWait.until(ExpectedConditions.attributeContains(collapsePanel, "opacity", "1"));
            }

            return true;
        } catch (TimeoutException ex) {
            return false;
        }
    }
}
