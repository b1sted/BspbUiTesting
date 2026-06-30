package ru.basted.bspbuitesting.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ru.basted.bspbuitesting.base.BaseTest;
import ru.basted.bspbuitesting.pages.MainPage;
import ru.basted.bspbuitesting.pages.SearchPage;

public class SearchTest extends BaseTest {
    @Test
    public void testSearch() {
        MainPage mainPage = new MainPage(webDriver);
        SearchPage searchPage = new SearchPage(webDriver);

        mainPage.clickSearchButton();
        Assertions.assertTrue(searchPage.isSearchPageOpened(),
                "Страница поиска не была открыта!");

        Assertions.assertTrue(searchPage.searchAndVerifyResults("ВЭД"),
                "Поиск не удался: счетчик результатов поиска так и остался равным нулю!");
    }
}
