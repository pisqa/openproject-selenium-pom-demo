package pages;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import wrappers.WaitWrappers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomFieldsListPage {
    private final WebDriver driver;
    private final WaitWrappers waitWrappers;

    private final By listTable = By.cssSelector("tbody");
    private final By noResultsTable = By.className("generic-table--no-results-title");
    private final By createCustomFieldButton = By.cssSelector(".button[title='New custom field']");
    private final By titleText = By.cssSelector(".title-container>h2");
    private final By tableContainer = By.cssSelector(".generic-table--results-container");

    public CustomFieldsListPage(final WebDriver driver) {
        this.driver = driver;
        this.waitWrappers = new WaitWrappers(driver, 30);
    }

    public void selectTab(String tabName) {
        String locator = "//a[@role='tab'][@title='" + tabName + "']/parent::li";
        waitWrappers.waitForElement(By.xpath(locator)).click();

        //wait for selected state
        waitWrappers.waitForAttributeValue(By.xpath(locator), "data-qa-tab-selected", "true");
    }

    public void deleteCustomField(String fieldName) {

        // first check if there are no custom fields defined for any project
        if (!driver.findElements(noResultsTable).isEmpty()) {
            return;
        }

        // check if there is a custom field with specified name
        String locator = "//td/a[text()='" + fieldName + "']/ancestor::tr//a[@title='Delete']";
        List<WebElement> fields = driver.findElements(By.xpath(locator));

        if (fields.size() != 0) {
            //there can only be one (name is unique)
            WebElement listTable = driver.findElement(tableContainer);
            fields.get(0).click();

            // accept confirm alert
            Alert alert = waitWrappers.waitForAlert();
            assertThat(alert.getText()).isEqualTo("Are you sure?");
            alert.accept();

            // wait for table reload
            waitWrappers.waitForStaleness(listTable);
        }
    }

    public void startCreateCustomField() {
        waitWrappers.waitForElement(createCustomFieldButton).click();
        waitWrappers.waitForText(titleText, "New custom field");
    }
}
