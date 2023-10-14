package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import wrappers.CheckboxWrapper;
import wrappers.WaitWrappers;

import java.util.List;

public class WorkPackagePage {
    private final WebDriver driver;
    private final WaitWrappers waitWrappers;

    private final By wpNameBox = By.cssSelector("#wp-new-inline-edit--field-subject");
    private final By wpNewState = By.cssSelector("#wp-new-inline-edit--field-subject");
    private final By saveChangesButton = By.cssSelector("button#work-packages--edit-actions-save");
    private final By cancelChangesButton = By.cssSelector("button#work-packages--edit-actions-cancel");
    private final By workItemTitle = By.cssSelector("span[data-field-name='subject']");

    public WorkPackagePage(final WebDriver driver) {
        this.driver = driver;
        this.waitWrappers = new WaitWrappers(driver, 30);
    }

    public boolean customFieldExistsInGroup(String group, String field) {
        String locator = ".attributes-group[data-group-name='" + group + "']";
        WebElement groupEl = waitWrappers.waitForElement(By.cssSelector(locator));
        String locator2 = "//span[contains(@data-qa-selector, 'customField') and contains(text(), '" + field + "')]";
        List<WebElement> fieldEl = groupEl.findElements(By.xpath(locator2));
        return !fieldEl.isEmpty();
    }

    public void enterWorkPackageName(String name) {
        waitWrappers.waitForElement(wpNameBox).sendKeys(name);
    }

    public void setCustomCheckboxField(String group, String fieldName, boolean select) {

        String locator = ".attributes-group[data-group-name='" + group + "']";
        waitWrappers.waitForElement(By.cssSelector(locator));

        String locator2 =
                "//label[contains(@for, 'wp-new-inline-edit--field-customField') and contains(text(), '" +
                        fieldName + "')]/ancestor::form//input[@type='checkbox']";

        CheckboxWrapper checkboxWrapper = new CheckboxWrapper(driver, By.xpath(locator2));
        checkboxWrapper.check();
    }

    public void saveChanges() {
        waitWrappers.waitForElement(saveChangesButton).click();

        //wait for work-package--new-state to not exist
        waitWrappers.waitForInvisibility(wpNewState);
    }

    public void cancelChanges() {
        waitWrappers.waitForElement(cancelChangesButton).click();

        //wait for work-package--new-state to not exist
        waitWrappers.waitForInvisibility(wpNewState);
    }

    public String getWorkItemTitle() {
        return waitWrappers.waitForElement(workItemTitle).getText();
    }

    public String getCustomFieldValue(String groupName, String fieldName) {

        String groupLocator = "div.attributes-group[data-group-name='" + groupName + "']";
        String fieldLlocator = "//span[contains(@data-field-name, 'customField') and contains(@aria-label, '" + fieldName + "')]";

        WebElement groupEl = waitWrappers.waitForElement(By.cssSelector(groupLocator));

        return groupEl.findElement(By.xpath(fieldLlocator)).getText();
    }
}





