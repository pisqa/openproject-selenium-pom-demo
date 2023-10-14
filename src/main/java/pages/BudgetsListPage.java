package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import wrappers.WaitWrappers;

public class BudgetsListPage {

    private final WebDriver driver;
    private final WaitWrappers waitWrappers;

    private final By pageHeaderText = By.cssSelector(".title-container>h2");

    public BudgetsListPage(final WebDriver driver) {
        this.driver = driver;
        this.waitWrappers = new WaitWrappers(driver, 30);
    }

    public String getPageHeader() {
        return waitWrappers.waitForElement(pageHeaderText).getText();
    }
}
