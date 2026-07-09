package ru.basted.bspbuitesting.tests;

import java.util.Map;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ru.basted.bspbuitesting.base.BaseTest;
import ru.basted.bspbuitesting.pages.MainPage;

@Epic("Главная страница")
@Feature("Рекламные ссылки в начале страницы")
public class AdsTest extends BaseTest {
    @Test
    @DisplayName("Проверка перехода по рекламным ссылкам на главной странице")
    @Story("Переход по рекламным ссылкам")
    public void should_ChangeUrl_When_AdIsClicked() {
        MainPage mainPage = new MainPage(webDriver).open();

        Map<Integer, String> adUrlResults = mainPage.clickOnAllAds();
        SoftAssertions.assertSoftly(softly ->
                adUrlResults.forEach((elementIndex, url) ->
                        softly.assertThat(url)
                                .as("Реклама №%d на главной странице не открыла новую страницу (клик не сработал)", elementIndex)
                                .isNotEmpty()
                )
        );
    }
}
