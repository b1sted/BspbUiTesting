package ru.basted.bspbuitesting.tests;

import java.util.Map;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import ru.basted.bspbuitesting.base.BaseTest;
import ru.basted.bspbuitesting.pages.CurrencyPage;
import ru.basted.bspbuitesting.pages.MainPage;
import ru.basted.bspbuitesting.steps.UiValidationSteps;

public class CurrencyTest extends BaseTest {
    @Test
    public void testCurrency() {
        MainPage mainPage = new MainPage(webDriver);
        CurrencyPage currencyPage = new CurrencyPage(webDriver);

        SoftAssertions softAssertions = new SoftAssertions();

        mainPage.clickBuyCurrency();
        UiValidationSteps.verifyCurrentUrlContains(webDriver, "/exchange");

        String expectedOffice = "ДО \"Петродворцовый\"";
        String actualOffice = currencyPage.selectOffice(expectedOffice);
        Assertions.assertThat(actualOffice).isEqualToIgnoringCase(expectedOffice);

        String query = "10000";
        String expectedOutput = query.replaceAll("(?<=\\d)(?=(\\d{3})+(?!\\d))", " ");
        Map<Integer, String> inputCurrencyStatuses = currencyPage.inputCurrency(query);
        UiValidationSteps.verifyAllValues(softAssertions, inputCurrencyStatuses, expectedOutput);

        Map<Integer, String> currencyChoiceStatutes = currencyPage.selectCurrencies();
        UiValidationSteps.verifyAllValues(softAssertions, currencyChoiceStatutes, "евро");

        Map<Integer, Boolean> unfoldAccordionStatuses = currencyPage.unfoldQuestionsAndVerify();
        UiValidationSteps.verifyAllStatuses(softAssertions, unfoldAccordionStatuses,
                "AccordionButton №%d на странице обмена валют в разделе 'Вопросы' не раскрылся");

        softAssertions.assertAll();
    }
}
