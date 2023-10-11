package wrappers;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class DropDownWrapper {

    private final WebDriver driver;
    private final By locator;
    private final WebElement ddElement;
    private final WaitWrappers waitWrappers;

    public DropDownWrapper(final WebDriver driver, final By locator) {
        this.driver = driver;
        this.locator = locator;
        this.waitWrappers = new WaitWrappers(driver, 30);
        this.ddElement = waitWrappers.waitForElement(locator);
    }

    public void select(String option) {
        String locator = "div[title=" + option + "]";
        this.ddElement.click();
        waitWrappers.waitForElement(By.cssSelector(locator)).click();
    }
}
