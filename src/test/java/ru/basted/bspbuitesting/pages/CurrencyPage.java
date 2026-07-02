package ru.basted.bspbuitesting.pages;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import ru.basted.bspbuitesting.base.BasePage;

public class CurrencyPage extends BasePage {
    private final By questionsTabLocator = By.xpath("//button[normalize-space()='Вопросы']");
    private final By questionsAccordionButtonsLocator = By.cssSelector(
            "div.chakra-tabs__tab-panel:not([hidden]) button.chakra-accordion__button"
    );
    private final By accordionCollapsePanelLocator = By.xpath(
            "following-sibling::div[contains(@class, 'chakra-collapse')]"
    );

    public CurrencyPage(WebDriver webDriver) {
        super(webDriver);
    }

    public boolean isCurrencyPageOpened() {
        return isPageOpened("/finance/exchange");
    }

    public Map<Integer, Boolean> unfoldQuestionsAndVerify() {
        Map<Integer, Boolean> results = new HashMap<>();
        clickOnElement(questionsTabLocator);

        List<WebElement> accordionButtons =
                webDriverWait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(questionsAccordionButtonsLocator));
        for (int i = 0; i < accordionButtons.size(); i++) {
            WebElement accordionButton = accordionButtons.get(i);
            try {
                webDriverWait.until(ExpectedConditions.elementToBeClickable(accordionButton));
                accordionButton.click();

                WebElement collapsePanel = accordionButton.findElement(accordionCollapsePanelLocator);
                webDriverWait.until(ExpectedConditions.attributeContains(collapsePanel, "opacity", "1"));
            } catch (TimeoutException ex) {
                results.put(i, false);
            }
        }

        return results;
    }
}
