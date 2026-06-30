package ru.basted.bspbuitesting.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ru.basted.bspbuitesting.base.BaseTest;
import ru.basted.bspbuitesting.pages.CurrencyPage;
import ru.basted.bspbuitesting.pages.MainPage;

public class CurrencyTest extends BaseTest {
    @Test
    public void testCurrency() {
        MainPage mainPage = new MainPage(webDriver);
        CurrencyPage currencyPage = new CurrencyPage(webDriver);

        mainPage.clickBuyCurrency();
        Assertions.assertTrue(currencyPage.isCurrencyPageOpened(),
                "Страница обмена валют не была открыта!");

        Assertions.assertTrue(currencyPage.unfoldQuestionsAndVerify(),
                "Раскрывающийся список вопросов не раскрылся!");
    }
}
