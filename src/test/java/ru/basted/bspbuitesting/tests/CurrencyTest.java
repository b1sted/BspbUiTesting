package ru.basted.bspbuitesting.tests;

import java.util.Map;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ru.basted.bspbuitesting.base.BaseTest;
import ru.basted.bspbuitesting.pages.CurrencyPage;
import ru.basted.bspbuitesting.pages.MainPage;
import ru.basted.bspbuitesting.steps.UiValidationSteps;

@Epic("Страница обмена валют")
@Feature("Проверка работоспособности элементов")
public class CurrencyTest extends BaseTest {
    @BeforeEach
    public void setupCurrencyPage() {
        MainPage mainPage = new MainPage(webDriver);

        mainPage.clickBuyCurrency();
        UiValidationSteps.verifyCurrentUrlContains(webDriver, "/exchange");
    }

    @Test
    @DisplayName("Выбор офиса в select блоке раздела 'Наличный обмен'")
    @Story("Выбор офиса-обменника")
    public void should_ReturnSelectedOfficeName_When_OfficeIsSelected() {
        CurrencyPage currencyPage = new CurrencyPage(webDriver);

        String expectedOffice = "ДО \"Петродворцовый\"";
        String actualOffice = currencyPage.selectOffice(expectedOffice);
        Assertions.assertThat(actualOffice).isEqualToIgnoringCase(expectedOffice);
    }

    @Test
    @DisplayName("Проверка ввода количества валюты в разделе 'Наличный обмен'")
    @Story("Ввод суммы для конвертации")
    public void should_DisplayFormattedAmount_When_CurrencyAmountIsEntered() {
        CurrencyPage currencyPage = new CurrencyPage(webDriver);
        SoftAssertions softAssertions = new SoftAssertions();

        String query = "10000";
        String expectedOutput = query.replaceAll("(?<=\\d)(?=(\\d{3})+(?!\\d))", " ");

        Map<Integer, String> inputCurrencyStatuses = currencyPage.inputCurrency(query);
        UiValidationSteps.verifyAllValues(softAssertions, inputCurrencyStatuses, expectedOutput);

        softAssertions.assertAll();
    }

    @Test
    @DisplayName("Проверка смены валюты в select блоке раздела 'Наличный обмен'")
    @Story("Выбор валюты для обмена")
    public void should_DisplayEuro_When_CurrencyIsSelected() {
        CurrencyPage currencyPage = new CurrencyPage(webDriver);
        SoftAssertions softAssertions = new SoftAssertions();

        Map<Integer, String> currencyChoiceStatutes = currencyPage.selectCurrencies();
        UiValidationSteps.verifyAllValues(softAssertions, currencyChoiceStatutes, "евро");

        softAssertions.assertAll();
    }

    @Test
    @DisplayName("Проверка раскрытия аккордеона в разделе 'Вопросы'")
    @Story("Раскрытие аккордеона вопросов")
    public void should_ExpandPanel_When_AccordionButtonIsClicked() {
        CurrencyPage currencyPage = new CurrencyPage(webDriver);
        SoftAssertions softAssertions = new SoftAssertions();

        Map<Integer, Boolean> unfoldAccordionStatuses = currencyPage.unfoldQuestionsAndVerify();
        UiValidationSteps.verifyAllStatuses(softAssertions, unfoldAccordionStatuses,
                "AccordionButton №%d на странице обмена валют в разделе 'Вопросы' не раскрылся");

        softAssertions.assertAll();
    }
}
