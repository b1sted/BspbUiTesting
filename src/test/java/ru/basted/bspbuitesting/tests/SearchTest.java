package ru.basted.bspbuitesting.tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ru.basted.bspbuitesting.base.BaseTest;
import ru.basted.bspbuitesting.pages.MainPage;
import ru.basted.bspbuitesting.pages.SearchPage;
import ru.basted.bspbuitesting.steps.UiValidationSteps;

@Epic("Поисковая страница")
@Feature("Проверка работоспособности поиска")
public class SearchTest extends BaseTest {
    @Test
    @DisplayName("Проверка поиска по запросу на странице 'Поиск'")
    @Story("Выполнение поискового запроса")
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
