package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import wrappers.DropDownWrapper;
import wrappers.WaitWrappers;

public class UserListPage {

    private final WebDriver driver;
    private final WaitWrappers waitWrappers;

    private final By searchSelector = By.cssSelector("select#status");
    private final By searchBox = By.cssSelector("input#name");
    private final By tableContainer = By.cssSelector(".generic-table--results-container");
    private final By applyButton = By.cssSelector(".button[value=Apply]");
    private final By emptyRow = By.cssSelector(".generic-table--empty-row");
    private final By tableRows = By.cssSelector("tbody > tr");

    public UserListPage(final WebDriver driver) {
        this.driver = driver;
        this.waitWrappers = new WaitWrappers(driver, 30);
    }

    public void selectSearchStatus(String status) {
        DropDownWrapper dropDownWrapper = new DropDownWrapper(driver, searchSelector);
        dropDownWrapper.select(status);
    }

    public void enterSearchName(String name) {
        waitWrappers.waitForElement(searchBox).sendKeys(name);
    }

    public void applySearch() {
        WebElement resultsTable = driver.findElement(tableContainer);
        waitWrappers.waitForElement(applyButton).click();
        waitWrappers.waitForStaleness(resultsTable);
    }

    public int getSearchResultsCount() {

        //first check for no results
        if (!driver.findElements(emptyRow).isEmpty()) {
            return 0;
        }

        //return row count
        return driver.findElements(tableRows).size();
    }

    public void selectUserAtRow(int row) {
        driver.findElement(By.xpath(
                "//td[@class='username'][" + row + "]")).click();
    }
}
