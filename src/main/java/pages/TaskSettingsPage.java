package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import wrappers.WaitWrappers;

public class TaskSettingsPage {

    private final WebDriver driver;
    private final WaitWrappers waitWrappers;

    private final By saveButton = By.cssSelector("button[type=submit]");

    public TaskSettingsPage(final WebDriver driver) {
        this.driver = driver;
        this.waitWrappers = new WaitWrappers(driver, 30);
    }

    public void selectTab(String tabName) {
        String locator = "//a[@role='tab'][@title='" + tabName + "']/parent::li";
        waitWrappers.waitForElement(By.xpath(locator)).click();

        //wait for selected state
        waitWrappers.waitForAttributeValue(By.xpath(locator), "data-qa-tab-selected", "true");
    }

    public void moveFormFieldToGroup(String fieldName, String sourceGroup, String targetGroup) {

        Actions actions = new Actions(driver);
        String sourceLocator =
                "//*[@class='group-edit-handler' or @class='group-name'][text()='" +  sourceGroup  + "']/../../.." +
                        "//span[@class='attribute-name'][contains(text(), '" + fieldName + "')]/preceding-sibling::span";

        WebElement source = driver.findElement(By.xpath(sourceLocator));

        String targetLocator =
                "//*[@class='group-edit-handler' or @class='group-name'][text()='" + targetGroup +
                "']//ancestor::div[@class='type-form-conf-group' or @id='type-form-conf-inactive-group']";

        WebElement target = driver.findElement(By.xpath(targetLocator));

        // perform drag and drop
        actions.dragAndDrop(source, target).build().perform();

        //check that move has succeeded
        String newSourceLocator =
                "//*[@class='group-edit-handler' or @class='group-name'][text()='" +  targetGroup  + "']/../../.." +
                        "//span[@class='attribute-name'][contains(text(), '" + fieldName + "')]";

        waitWrappers.waitForElement(By.xpath(newSourceLocator));
    }

    public void saveChanges() {
        driver.findElement(saveButton).click();
    }

    public boolean fieldExistsInGroup(String group, String field) {
        String locator = "//div[@class='group-edit-handler' and text()='" + group + "']/ancestor::div[@class='type-form-conf-group']";
        WebElement groupEl = waitWrappers.waitForElement(By.xpath(locator));
        return !groupEl.findElements(By.xpath("//span[@class='attribute-name' and contains(text(), '" + field + "')]")).isEmpty();
    }
}
