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

public class HomePageLayoutTest extends BaseLayoutTest {
    @Test
    public void testFullPageLayout() {
        MainPage mainPage = new MainPage(webDriver).open();

        ScreenSizeUtils.setWindowSize(ScreenSize.FULL_HD);

        WebElement container = mainPage.getWrapper();

        List<RawDataSet> dataSetList = new ArrayList<>();
        dataSetList.add(new RawDataSet(
                "full page",
                container,
                "DECOR, TEXT, IMAGE, PSEUDO_BEFORE, PSEUDO_AFTER"
        ));

        Executor executor = new Executor(
                dataSetList,
                "full_page_scenario",
                "homepage",
                "CHROME",
                container
        );

        executor.execute();
    }
}