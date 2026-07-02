package ru.basted.bspbuitesting.tests;

import java.util.Map;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ru.basted.bspbuitesting.base.BaseTest;
import ru.basted.bspbuitesting.pages.MainPage;

public class AdsTest extends BaseTest {
    @Test
    @DisplayName("Проверка перехода по рекламным ссылкам на главной странице")
    public void should_ChangeUrl_When_AdIsClicked() {
        MainPage mainPage = new MainPage(webDriver);

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
