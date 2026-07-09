package ru.basted.bspbuitesting.base;

import org.junit.jupiter.api.BeforeEach;

import ru.webrelab.layout_testing.LayoutConfiguration;
import ru.webrelab.layout_testing.framework_based_behavior.SeleniumFrameworkBehavior;

public class BaseLayoutTest extends BaseTest {
    @BeforeEach
    void setupLayoutTesting() {
        LayoutConfiguration.INSTANCE.setMethodsInjection(new BaseLayoutMethods(webDriver));
        LayoutConfiguration.INSTANCE.setFrameworkBasedBehavior(new SeleniumFrameworkBehavior());
    }
}
