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

import ru.basted.bspbuitesting.dynamic.DynamicContentMasker;

public class BaseLayoutMethods implements IMethodsInjection {
    private final WebDriver webDriver;

    public BaseLayoutMethods(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    @Override
    public PositionRepository getPosition(PositionRepository container, Object object) {
        final Point point = ((WebElement) object).getLocation();
        return new PositionRepository(container, point.y, point.x);
    }

    @Override
    public Object executeJs(String js, Object... objects) {
        return ((RemoteWebDriver) webDriver).executeScript(js, objects);
    }

    @Override
    public List<?> findElementsByXpath(Object object, String xpath) {
        return ((WebElement) object).findElements(By.xpath(xpath));
    }

    @Override
    public List<?> findElementsByXpath(String xpath) {
        return webDriver.findElements(By.xpath(xpath));
    }

    @Override
    public String getText(Object object) {
        return DynamicContentMasker.mask(((WebElement) object).getText());
    }

    @Override
    public String getTagName(Object object) {
        return ((WebElement) object).getTagName();
    }

    @Override
    public String getAttributeValue(Object webElement, String attribute) {
        return ((WebElement) webElement).getAttribute(attribute);
    }

    @Override
    public SizeRepository getWindowBodySize() {
        return ScreenSizeUtils.getViewportSize();
    }

    @Override
    public SizeRepository getWindowSize() {
        final Dimension dimension = webDriver.manage().window().getSize();
        return new SizeRepository(dimension.height, dimension.width);
    }

    @Override
    public void setWindowSize(SizeRepository size) {
        webDriver.manage().window().setSize(new Dimension(size.getWidth(), size.getHeight()));
    }

    @Override
    public void actionsBeforeTesting() {
    }

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

    private void saveScreenshot(final ScreenDraw.DataState dataState, final LayoutElement element) {
        final String id = dataState.name() + "-" + element.getType().toString() + "-" + element.getId();
        final WebElement webElement = webDriver.findElement(By.id(id));
        new Actions(webDriver).scrollToElement(webElement).build().perform();
        final byte[] bytes = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.BYTES);
        final Path path = Paths.get("build", id + ".png");
        try {
            Files.createDirectories(path.getParent());
            Files.write(path, bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void actionAfterSnapshotCreated() {
    }
}