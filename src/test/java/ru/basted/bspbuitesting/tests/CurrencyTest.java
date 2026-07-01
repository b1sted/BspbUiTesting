package ru.basted.bspbuitesting.tests;

import java.util.Map;

import org.assertj.core.api.SoftAssertions;
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
        boolean isOpened = currencyPage.isCurrencyPageOpened();
        Assertions.assertTrue(isOpened, "Страница обмена валют не была открыта!");

        Map<Integer, Boolean> unfoldAccordionStatuses = currencyPage.unfoldQuestionsAndVerify();
        SoftAssertions.assertSoftly(softly -> {
            unfoldAccordionStatuses.forEach((accordionButtonId, isSuccess) -> {
                softly.assertThat(isSuccess)
                        .withFailMessage(
                                "AccordionButton №%d на странице 'Обмен валюты' в разделе 'Вопросы' не найден/не открылся",
                                accordionButtonId
                        )
                        .isTrue();
            });
        });
    }
}
