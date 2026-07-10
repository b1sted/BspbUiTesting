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
import ru.basted.bspbuitesting.pages.CurrencyPage;

public class ExchangePageLayoutTest extends BaseLayoutTest {
    @Test
    public void testFullPageLayout() {
        CurrencyPage currencyPage = new CurrencyPage(webDriver).open();

        ScreenSizeUtils.setWindowSize(ScreenSize.FULL_HD);

        WebElement container = currencyPage.getWrapper();

        List<RawDataSet> dataSetList = new ArrayList<>();
        dataSetList.add(new RawDataSet(
                "full page",
                container,
//                "ALL, POSITION"
                "DECOR, TEXT, IMAGE, PSEUDO_BEFORE, PSEUDO_AFTER"
        ));

        Executor executor = new Executor(
                dataSetList,
                "exchange_page_scenario",
                "exchange_page",
                "CHROME",
                container
        );

        executor.execute();
    }
}