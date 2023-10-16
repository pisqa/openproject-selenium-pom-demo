package wrappers;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

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

        // if it's a 'real' select, use the Select class
        if (ddElement.getTagName().equals("select")) {
            Select select = new Select(ddElement);
            select.selectByValue(option);
        } else {
            String locator = "div[title=" + option + "]";
            this.ddElement.click();
            waitWrappers.waitForElement(By.cssSelector(locator)).click();
        }
    }
}
