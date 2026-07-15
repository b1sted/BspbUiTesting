package ru.basted.bspbuitesting.base;

import org.junit.jupiter.api.BeforeEach;

import ru.webrelab.layout_testing.LayoutConfiguration;
import ru.webrelab.layout_testing.framework_based_behavior.SeleniumFrameworkBehavior;

/**
 * Базовый класс для layout-тестов. Наследует общую инфраструктуру от {@link BaseTest}
 * (webDriver, webDriverWait и т.д.) и дополнительно регистрирует библиотеку
 * layout_testing перед каждым тестом.
 * <p>
 * {@code LayoutConfiguration.INSTANCE} — глобальный singleton библиотеки, через
 * который PageScanner и все репозитории (DecorRepository, TextRepository...)
 * получают доступ к драйверу. Не потокобезопасен — при параллельном запуске
 * тестов возможны гонки (см. известные ограничения библиотеки).
 * <p>
 * SeleniumFrameworkBehavior — готовая реализация {@code IFrameworkBasedBehavior}
 * из самой библиотеки, писать свою не нужно.
 * BaseLayoutMethods — наша реализация {@code IMethodsInjection} (мост между
 * библиотекой и Selenium WebDriver).
 */
public class BaseLayoutTest extends BaseTest {
    @BeforeEach
    void setupLayoutTesting() {
        LayoutConfiguration.INSTANCE.setMethodsInjection(new BaseLayoutMethods(webDriver));
        LayoutConfiguration.INSTANCE.setFrameworkBasedBehavior(new SeleniumFrameworkBehavior());
    }
}