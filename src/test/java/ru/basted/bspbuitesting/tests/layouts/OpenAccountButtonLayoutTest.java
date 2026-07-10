package ru.basted.bspbuitesting.tests.layouts;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import ru.webrelab.layout_testing.Executor;
import ru.webrelab.layout_testing.enums.ScreenSize;
import ru.webrelab.layout_testing.repository.RawDataSet;
import ru.webrelab.layout_testing.utils.ScreenSizeUtils;

import ru.basted.bspbuitesting.base.BaseLayoutTest;
import ru.basted.bspbuitesting.pages.MainPage;

public class OpenAccountButtonLayoutTest extends BaseLayoutTest {
    @Test
    public void testSubmitButtonLayout() {
        MainPage mainPage = new MainPage(webDriver).open();

        ScreenSizeUtils.setWindowSize(ScreenSize.FULL_HD);

        WebElement processButton = mainPage.getProcessButton();

        Executor executor = new Executor(
                List.of(new RawDataSet(
                        "open account button",
                        processButton,
                        "DECOR, TEXT, PSEUDO_BEFORE, PSEUDO_AFTER, SVG, POSITION"
                )),
                "open_account_button_scenario",
                "homepage",
                "CHROME",
                processButton
        );

        executor.execute();
    }
}