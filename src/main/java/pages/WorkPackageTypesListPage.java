package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import wrappers.WaitWrappers;

public class WorkPackageTypesListPage {

    private final WebDriver driver;
    private final WaitWrappers waitWrappers;

    public WorkPackageTypesListPage(final WebDriver driver) {
        this.driver = driver;
        this.waitWrappers = new WaitWrappers(driver, 30);
    }

    public void selectTypeByName(String typeName) {

        // check if there is a custom field with specified name
        String locator = "//td/a[text()='" + typeName + "']";
        waitWrappers.waitForElement(By.xpath(locator)).click();
    }
}
