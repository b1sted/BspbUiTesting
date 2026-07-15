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

/**
 * Тест-пример для демонстрации работы библиотеки layout-testing.
 * Используется для автоматической проверки визуальной вёрстки страницы.
 * <p>
 * Логика работы:
 * <p>
 * 1. Первый запуск: сравнения ещё нет — библиотека только создаёт "эталон"
 *    (JSON-файл) в папке storage.
 * <p>
 * 2. Повторные запуски: текущая страница сравнивается с этим эталоном,
 *    и только начиная с этого момента тест может найти расхождения.
 */
public class HomePageLayoutTest extends BaseLayoutTest {

    @Test
    public void testFullPageLayout() {
        // --- 1. ПОДГОТОВКА СТРАНИЦЫ ---
        MainPage mainPage = new MainPage(webDriver).open();

        // КРИТИЧНО: Устанавливаем разрешение ДО создания Executor.
        // В конструкторе Executor один раз определяет текущий размер окна
        // и сопоставляет его с одним из значений enum ScreenSize.
        // Если на момент создания Executor размер окна не совпадёт ни с одним
        // значением ScreenSize (с допуском в несколько пикселей),
        // конструктор выбросит исключение и тест упадёт ещё до старта сканирования.
        ScreenSizeUtils.setWindowSize(ScreenSize.FULL_HD);

        // Элемент-обёртка страницы, внутри которого будем сравнивать вёрстку с эталоном
        WebElement pageWrapper = mainPage.getWrapper();

        // --- 2. КОНФИГУРАЦИЯ СКАНЕРА ---
        // RawDataSet определяет: какой элемент сканируем (pageWrapper) и какие
        // типы проверки (аспекты вёрстки) к нему применить.
        // Доступные типы: DECOR (фон/тени), TEXT (шрифты), IMAGE, PSEUDO_BEFORE/PSEUDO_AFTER (псевдоэлементы) —
        // это не полный список, есть ещё, например, SVG и POSITION.
        List<RawDataSet> layoutDataSets = List.of(new RawDataSet(
                "full page",
                pageWrapper,
                "DECOR, TEXT, IMAGE, PSEUDO_BEFORE, PSEUDO_AFTER"
        ));

        // --- 3. НАСТРОЙКА СРАВНЕНИЯ (EXECUTOR) ---
        Executor executor = new Executor(
                layoutDataSets,
                "full_page_scenario", // Уникальное имя JSON-файла с эталоном
                "homepage",                             // Путь внутри storage (группировка тестов)
                "CHROME",                               // Маркер браузера (влияет на путь к эталону)
                pageWrapper                             // Точка отсчёта: координаты всех найденных
                                                        // элементов будут считаться относительно неё,
                                                        // а не от левого верхнего угла всей страницы
        );

        // --- 4. ВЫПОЛНЕНИЕ ---
        // На первом запуске просто сохраняет эталон. На повторных — сравнивает
        // На первом запуске просто сохраняет эталон. На повторных — сравнивает
        // вёрстку с ним; если найдены отличия, тест упадёт и сгенерирует
        // скриншот с красными зонами (настраивается в BaseLayoutMethods).
        executor.execute();
    }
}