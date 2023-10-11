package wrappers;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class CheckboxWrapper {

    private final WebDriver driver;
    private final By locator;
    private final WebElement cbElement;
    private final WaitWrappers waitWrappers;

    public CheckboxWrapper(final WebDriver driver, final By locator) {
        this.driver = driver;
        this.locator = locator;
        this.waitWrappers = new WaitWrappers(driver, 30);
        this.cbElement = waitWrappers.waitForElement(locator);
    }

    public boolean isChecked() {
        return cbElement.isSelected();
    }

    public void check() {

        if (!isChecked()) {
            cbElement.click();
            waitWrappers.waitForSelectedState(locator, true);
        }
    }

    public void uncheck() {
        if (isChecked()) {
            cbElement.click();
            waitWrappers.waitForSelectedState(locator, false);
        }
    }
}
