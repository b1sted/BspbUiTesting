package ru.basted.bspbuitesting.base;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;

import ru.webrelab.layout_testing.LayoutElement;
import ru.webrelab.layout_testing.LayoutTestingException;
import ru.webrelab.layout_testing.ifaces.IMethodsInjection;
import ru.webrelab.layout_testing.repository.PositionRepository;
import ru.webrelab.layout_testing.repository.SizeRepository;
import ru.webrelab.layout_testing.screen_difference.DifferenceReport;
import ru.webrelab.layout_testing.utils.ScreenDraw;
import ru.webrelab.layout_testing.utils.ScreenSizeUtils;

/**
 * Реализация {@link IMethodsInjection} для Selenium WebDriver.
 * <p>
 * Это единственная точка, через которую библиотека layout_testing обращается
 * к браузеру — все find/execute-операции внутри PageScanner, ElementsTreeGenerator
 * и репозиториев (DecorRepository, TextRepository и т.д.) в итоге вызывают методы отсюда.
 * <p>
 * Регистрируется один раз при старте теста через
 * {@code LayoutConfiguration.INSTANCE.setMethodsInjection(new BaseLayoutMethods(driver))}
 * (см. {@link BaseLayoutTest#setupLayoutTesting()}).
 */
public class BaseLayoutMethods implements IMethodsInjection {

    private final WebDriver webDriver;

    public BaseLayoutMethods(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    /**
     * Возвращает позицию элемента относительно {@code container}.
     * container передаётся библиотекой как "родительская" точка отсчёта —
     * это тот самый containerElement, который указывается последним аргументом в Executor.
     */
    @Override
    public PositionRepository getPosition(PositionRepository container, Object object) {
        final Point point = ((WebElement) object).getLocation();
        return new PositionRepository(container, point.y, point.x);
    }

    /**
     * Выполняет произвольный JS в браузере. Через этот метод библиотека прогоняет
     * все свои js_snippets (get_element_size.js, svg_scan.js, MEASURE_DECOR и т.д.).
     * objects — аргументы, которые попадут в JS как arguments[0], arguments[1]...
     */
    @Override
    public Object executeJs(String js, Object... objects) {
        return ((RemoteWebDriver) webDriver).executeScript(js, objects);
    }

    /**
     * Поиск элементов внутри конкретного родителя по xpath.
     * Используется PageScanner для каждого MeasuringType (DECOR/TEXT/IMAGE/SVG/...),
     * xpath берётся из MeasuringType.getXpath().
     */
    @Override
    public List<?> findElementsByXpath(Object object, String xpath) {
        return ((WebElement) object).findElements(By.xpath(xpath));
    }

    /**
     * Поиск элементов от корня документа (без родительского контейнера).
     * Используется реже — например, если containerElement в Executor не передан.
     */
    @Override
    public List<?> findElementsByXpath(String xpath) {
        return webDriver.findElements(By.xpath(xpath));
    }

    /**
     * Возвращает текстовое содержимое элемента (аналог WebElement.getText()).
     * Используется TextRepository при сборе типа TEXT — итоговая строка попадает
     * в эталонный JSON и сравнивается между прогонами наравне с шрифтом/цветом.
     * <p>
     * Важно: getText() в Selenium возвращает только видимый текст (с учётом
     * display:none/visibility:hidden) — то есть скрытые элементы дадут пустую строку,
     * а не исключение.
     */
    @Override
    public String getText(Object object) {
        return ((WebElement) object).getText();
    }

    /**
     * Возвращает имя тега элемента (div, svg, path, button и т.д.).
     * Внутри библиотеки напрямую почти не используется — но критично важен
     * для диагностики: если добавить в PageScanner точечный try/catch на
     * элемент (см. пункт A4 из списка доработок), именно через getTagName()
     * можно будет понять, какой конкретно элемент вызвал ошибку, а не просто
     * получить голый NullPointerException без контекста.
     */
    @Override
    public String getTagName(Object object) {
        return ((WebElement) object).getTagName();
    }

    /**
     * Возвращает значение произвольного HTML-атрибута элемента.
     * Используется в двух местах:
     * — ImageRepository берёт отсюда src у <img> (упадёт с NPE, если атрибута
     *   нет — актуально для картинок с ленивой загрузкой через data-src/srcset,
     *   см. пункт B3);
     * — та же диагностика, что и у getTagName(): позволяет вытащить class/id
     *   проблемного элемента для сообщения об ошибке.
     */
    @Override
    public String getAttributeValue(Object webElement, String attribute) {
        return ((WebElement) webElement).getAttribute(attribute);
    }

    /**
     * Размер именно viewport'а (полезной области страницы), а не окна браузера целиком.
     * По этому значению ScreenSizeUtils.determineScreenSize() определяет, какой enum
     * ScreenSize (FULL_HD/DESKTOP/TABLET_*) сейчас активен — а значит, из какой подпапки
     * читать/куда писать эталонный JSON. Допуск сравнения — ±3px.
     */
    @Override
    public SizeRepository getWindowBodySize() {
        return ScreenSizeUtils.getViewportSize();
    }

    /**
     * Размер окна браузера целиком (включая рамки/скроллбары) — используется только
     * внутри ScreenSizeUtils.setWindowSize() для итеративной подгонки, чтобы
     * компенсировать разницу между "размер окна" и "размер viewport".
     */
    @Override
    public SizeRepository getWindowSize() {
        final Dimension dimension = webDriver.manage().window().getSize();
        return new SizeRepository(dimension.height, dimension.width);
    }

    /**
     * Выставляет размер окна браузера (не viewport'а — см. разницу в getWindowSize()).
     * Сам по себе вызывается не один раз за тест, а в цикле — внутри
     * ScreenSizeUtils.setWindowSize(ScreenSize), который итеративно подгоняет
     * размер окна так, чтобы РЕАЛЬНЫЙ viewport (getWindowBodySize()) совпал
     * с целевым разрешением. Это нужно, потому что разница между "размер окна"
     * и "размер полезной area страницы" (рамки, скроллбары, панели) отличается
     * между ОС/окружениями, и один вызов setSize() не гарантирует точного viewport.
     */
    @Override
    public void setWindowSize(SizeRepository size) {
        webDriver.manage().window().setSize(new Dimension(size.getWidth(), size.getHeight()));
    }

    /**
     * Вызывается один раз в Executor.prepare(), ДО того как библиотека начнёт
     * авторазметку DOM (MEASURE_DECOR/MEASURE_TEXT/MEASURE_PSEUDO_ELEMENTS) и сам скан.
     * Место для действий, общих для ВСЕХ тестов проекта — например, заморозка
     * CSS-анимаций/transition, чтобы избежать StaleElementReferenceException
     * из-за перерисовки DOM во время скана (актуально для React/Vue-страниц).
     * В эталонном примере библиотеки метод пустой ("do nothing") — оставлен как есть,
     * при необходимости сюда можно добавить свою логику.
     */
    @Override
    public void actionsBeforeTesting() {
        // do nothing
    }

    /**
     * Вызывается Executor'ом, если LayoutComparator нашёл расхождения между
     * актуальным сканом и эталонным JSON. Библиотека сама по себе assert не делает —
     * решение "уронить тест" и что именно сделать с найденными расхождениями
     * полностью на совести реализации IMethodsInjection.
     * <p>
     * Здесь дополнительно сохраняются скриншоты каждого расхождения — как ожидаемого
     * (если элемент вообще не нашёлся на странице — reports содержит r.isElementNotFound()),
     * так и актуального (если элемент есть, но его атрибуты не совпали с эталоном).
     */
    @Override
    public void actionAfterTestFailed(List<DifferenceReport> reports) {
        reports.forEach(System.out::println);
        reports.forEach(r -> {
            if (r.isElementNotFound()) {
                saveScreenshot(ScreenDraw.DataState.EXPECTED, r.getExpected());
            } else {
                saveScreenshot(ScreenDraw.DataState.ACTUAL, r.getActual());
            }
        });
        throw new LayoutTestingException("Layout errors detected");
    }

    /**
     * Делает скриншот конкретного расхождения и сохраняет в build/.
     * <p>
     * id элемента собирается из ACTUAL/EXPECTED + типа проверки (DECOR/TEXT/SVG/...) + id
     * самого LayoutElement — именно под таким id ScreenDraw ранее нарисовал разметку
     * (цветную рамку) поверх элемента прямо в DOM страницы, поэтому его можно найти
     * обратно через By.id(id). Перед скриншотом страница проматывается к элементу,
     * иначе он может быть вне текущей видимой области.
     */
    private void saveScreenshot(final ScreenDraw.DataState dataState, final LayoutElement element) {
        final String id = dataState.name() + "-" + element.getType().toString() + "-" + element.getId();
        final WebElement webElement = webDriver.findElement(By.id(id));
        final Actions actions = new Actions(webDriver);
        actions.scrollToElement(webElement).build().perform();
        final byte[] bytes = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.BYTES);
        final Path path = Paths.get("build", id + ".png");
        try {
            Files.write(path, bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Вызывается после того, как эталонный JSON создан в первый раз
     * (то есть до этого момента файла ещё не было). Тест при этом НЕ падает —
     * первый прогон всегда считается успешным созданием baseline.
     * В эталонном примере библиотеки метод пустой — оставлен как есть,
     * можно использовать, например, чтобы залогировать в CI "создан новый эталон,
     * не забудьте закоммитить".
     */
    @Override
    public void actionAfterSnapshotCreated() {

    }
}