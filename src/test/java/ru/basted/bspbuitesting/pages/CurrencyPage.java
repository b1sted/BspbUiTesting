package ru.basted.bspbuitesting.pages;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import ru.basted.bspbuitesting.base.BasePage;

public class CurrencyPage extends BasePage<CurrencyPage> {
    private static final String PAGE_URL = "https://bspb.ru/finance/exchange";

    private final By officeSelectLocator = By.cssSelector("div.chakra-tabs__tab-panel:not([hidden]) select");

    private final By exchangeAmountInputsLocator = By.cssSelector("div.chakra-tabs__tab-panel:not([hidden]) input");

    private final By currencyChoiceButtonsLocator = By.cssSelector(
            "div.chakra-tabs__tab-panel:not([hidden]) button[id*='menu-button']"
    );
    private final By euroOptionInCurrencyMenuLocator = By.xpath(
            "//div[contains(@id, 'menu-list') and not(@hidden)]//button[contains(text(), 'Евро')]"
    );
    private final By euroLabelInCurrencyChoiceMenuLocator = By.xpath(".//p[contains(text(), 'Евро')]");

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

    @Override
    protected String getUrl() {
        return PAGE_URL;
    }

    @Step("Выбор офиса-обменника")
    public String selectOffice(String officeName) {
        WebElement selectField = webDriverWait.until(ExpectedConditions.elementToBeClickable(officeSelectLocator));
        Select dropdown = new Select(selectField);

        dropdown.selectByVisibleText(officeName);
        return dropdown.getFirstSelectedOption().getText();
    }

    @Step("Ввод количества валюты")
    public Map<Integer, String> inputCurrency(String query) {
        Map<Integer, String> results = new HashMap<>();

        List<WebElement> currencyInputBoxes =
                webDriverWait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(exchangeAmountInputsLocator));
        for (int i = 0; i < currencyInputBoxes.size(); i++) {
            WebElement currencyInputBox = currencyInputBoxes.get(i);

            try {
                webDriverShortWait.until(ExpectedConditions.elementToBeClickable(currencyInputBox));

                currencyInputBox.sendKeys(Keys.chord(Keys.CONTROL, "a"));
                currencyInputBox.sendKeys(query);
                currencyInputBox.sendKeys(Keys.ENTER);

                results.put(i, currencyInputBox.getAttribute("value"));
            } catch (TimeoutException ex) {
                results.put(i, null);
            }
        }

        return results;
    }

    @Step("Выбор валют")
    public Map<Integer, String> selectCurrencies() {
        Map<Integer, String> results = new HashMap<>();

        List<WebElement> currencyChoiceMenus =
                webDriverWait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(currencyChoiceButtonsLocator));
        List<WebElement> euroInChoiceMenus = webDriver.findElements(euroOptionInCurrencyMenuLocator);
        for (int i = 0; i < currencyChoiceMenus.size(); i++) {
            WebElement currencyChoiceMenu = currencyChoiceMenus.get(i);
            try {
                webDriverShortWait.until(ExpectedConditions.elementToBeClickable(currencyChoiceMenu));
                clickOnElement(currencyChoiceMenu);

                WebElement euroInCurrentCurrencyChoiceMenu =
                        webDriverShortWait.until(ExpectedConditions.elementToBeClickable(euroInChoiceMenus.get(i)));
                clickOnElement(euroInCurrentCurrencyChoiceMenu);

                String actualText = currencyChoiceMenu.findElement(euroLabelInCurrencyChoiceMenuLocator).getText();
                results.put(i, actualText);
            } catch (TimeoutException ex) {
                results.put(i, null);
            }
        }

        return results;
    }

    @Step("Раскрытие аккордеона вопросов")
    public Map<Integer, Boolean> unfoldQuestionsAndVerify() {
        Map<Integer, Boolean> results = new HashMap<>();
        clickOnElement(questionsTabLocator);

        List<WebElement> accordionButtons =
                webDriverWait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(questionsAccordionButtonsLocator));
        for (int i = 0; i < accordionButtons.size(); i++) {
            WebElement accordionButton = accordionButtons.get(i);
            try {
                webDriverShortWait.until(ExpectedConditions.elementToBeClickable(accordionButton));
                clickOnElement(accordionButton);

                WebElement collapsePanel = accordionButton.findElement(accordionCollapsePanelLocator);
                webDriverShortWait.until(ExpectedConditions.attributeContains(collapsePanel, "opacity", "1"));
            } catch (TimeoutException ex) {
                results.put(i, false);
            }
        }

        return results;
    }
}
