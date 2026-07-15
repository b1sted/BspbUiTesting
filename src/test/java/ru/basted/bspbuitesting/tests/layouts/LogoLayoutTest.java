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

import static ru.webrelab.layout_testing.enums.MeasuringType.SVG;

/**
 * Тест-пример для проверки отдельного компонента (логотипа).
 * В отличие от тестов всей страницы, здесь фокус смещен на визуальную
 * целостность конкретного графического элемента.
 */
public class LogoLayoutTest extends BaseLayoutTest {

    @Test
    public void testLogoLayout() {
        // --- 1. ПОДГОТОВКА СТРАНИЦЫ ---
        MainPage mainPage = new MainPage(webDriver).open();

        // Разрешение экрана задаётся ДО создания Executor — так же критично,
        // как и в тесте всей страницы: если текущий размер окна не совпадёт
        // ни с одним значением ScreenSize, Executor не создастся и тест упадёт
        // ещё до сканирования, а не из-за расхождения координат.
        ScreenSizeUtils.setWindowSize(ScreenSize.FULL_HD);

        // Получаем конкретный элемент (логотип) из Page Object.
        // Мы будем тестировать только этот блок, игнорируя остальную страницу.
        WebElement logoElement = mainPage.getLogo();

        // --- 2. ОПРЕДЕЛЕНИЕ ПАРАМЕТРОВ СКАНИРОВАНИЯ ---
        // Для логотипов/иконок используем тип SVG — библиотека сравнивает
        // векторные данные (пути, заливку) элемента.
        // TEXT и DECOR здесь просто не указаны в списке типов — они не выполняются,
        // а не "отключены" каким-то отдельным переключателем.
        List<RawDataSet> layoutDataSets = List.of(new RawDataSet(
                "bank logo", // Описание набора (отобразится в отчете при ошибке)
                logoElement,             // Элемент для сканирования
                SVG                      // Тип проверки: только векторная графика
        ));

        // --- 3. НАСТРОЙКА ДВИЖКА (EXECUTOR) ---
        Executor executor = new Executor(
                layoutDataSets,
                "bank_logo_scenario", // Уникальное имя файла-эталона в рамках проекта
                "homepage",                             // Папка в хранилище (storage/homepage/...)
                "CHROME",                               // Субдиректория для конкретного браузера

                // ВАЖНО: передаём logoElement и как элемент, и как точку отсчёта (container).
                // Координаты внутри него считаются относительно самого logoElement,
                // поэтому смещение логотипа целиком по странице (например, на 5px вправо)
                // тест не уронит. НО если сам логотип изменится — размер, форма,
                // цвет заливки, положение внутренних путей — тест всё равно упадёт,
                // поскольку это уже отличие внутри самого элемента, а не сдвиг по странице.
                logoElement
        );

        // --- 4. ЗАПУСК ПРОВЕРКИ ---
        // Первый запуск: создаст JSON-файл с характеристиками логотипа.
        // Повторные: сравнит текущий вид с сохраненным файлом.
        executor.execute();
    }
}