package ru.basted.bspbuitesting.tests.layouts;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import ru.webrelab.layout_testing.Executor;
import ru.webrelab.layout_testing.enums.ScreenSize;
import ru.webrelab.layout_testing.repository.RawDataSet;
import ru.webrelab.layout_testing.utils.ScreenSizeUtils;

import ru.basted.bspbuitesting.base.BaseLayoutTest;
import ru.basted.bspbuitesting.pages.MainPage;

import static ru.webrelab.layout_testing.enums.MeasuringType.SVG;

public class LogoLayoutTest extends BaseLayoutTest {
    @Test
    public void testLogoLayout() {
        MainPage mainPage = new MainPage(webDriver).open();

        ScreenSizeUtils.setWindowSize(ScreenSize.FULL_HD);

        WebElement iconBlock = mainPage.getLogo();

        List<RawDataSet> dataSetList = new ArrayList<>();
        dataSetList.add(new RawDataSet(
                "svg icon",
                iconBlock,
                SVG
        ));

        Executor executor = new Executor(
                dataSetList,
                "bank_logo_scenario",
                "homepage",
                "CHROME",
                iconBlock
        );
        executor.execute();
    }
}
