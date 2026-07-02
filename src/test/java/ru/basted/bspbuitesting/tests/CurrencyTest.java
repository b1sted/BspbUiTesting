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

        Map<Integer, Boolean> unfoldAccordionStatuses = currencyPage.unfoldQuestionsAndVerify();
        UiValidationSteps.verifyAllStatuses(softAssertions, unfoldAccordionStatuses,
                "AccordionButton №%d на странице обмена валют в разделе 'Вопросы' не раскрылся");

        softAssertions.assertAll();
    }
}
