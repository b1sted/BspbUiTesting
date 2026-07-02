package ru.basted.bspbuitesting.tests;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ru.basted.bspbuitesting.base.BaseTest;
import ru.basted.bspbuitesting.pages.MainPage;
import ru.basted.bspbuitesting.pages.SearchPage;
import ru.basted.bspbuitesting.steps.UiValidationSteps;

public class SearchTest extends BaseTest {
    @Test
    @DisplayName("Проверка поиска по запросу на странице 'Поиск'")
    public void should_ReturnResults_When_QueryIsSearched() {
        MainPage mainPage = new MainPage(webDriver);
        SearchPage searchPage = new SearchPage(webDriver);

        mainPage.clickSearchButton();
        UiValidationSteps.verifyCurrentUrlContains(webDriver, "/search");

        boolean isSearchSuccessful = searchPage.isSearchSuccessful("ВЭД");
        Assertions.assertThat(isSearchSuccessful)
                .withFailMessage("Поиск не удался: счетчик результатов поиска так и остался равным нулю!")
                .isTrue();
    }
}
