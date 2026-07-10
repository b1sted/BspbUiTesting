package ru.basted.bspbuitesting.tests.layouts;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import ru.webrelab.layout_testing.Executor;
import ru.webrelab.layout_testing.enums.ScreenSize;
import ru.webrelab.layout_testing.repository.RawDataSet;
import ru.webrelab.layout_testing.utils.ScreenSizeUtils;

import ru.basted.bspbuitesting.base.BaseLayoutTest;
import ru.basted.bspbuitesting.pages.FeedbackPage;

public class ContactFormLayoutTest extends BaseLayoutTest {
    @Test
    public void testContactFormLayout() {
        FeedbackPage feedbackPage = new FeedbackPage(webDriver).open();

        ScreenSizeUtils.setWindowSize(ScreenSize.FULL_HD);

        WebElement contactFormContainer = feedbackPage.getContactFormContainer();

        Executor executor = new Executor(
                List.of(new RawDataSet(
                        "contact form",
                        contactFormContainer,
                        "DECOR, TEXT, IMAGE, PSEUDO_BEFORE, PSEUDO_AFTER, POSITION"
                )),
                "contact_form_scenario",
                "feedback_page",
                "CHROME",
                contactFormContainer
        );

        executor.execute();
    }
}