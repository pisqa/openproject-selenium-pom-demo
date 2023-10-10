package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import wrappers.CheckboxWrapper;
import wrappers.WaitWrappers;

public class ProjectSettingsModulesPage {

    private final WebDriver driver;
    private final WaitWrappers waitWrappers;

    private final By saveButton = By.cssSelector("form[action*=modules] button[type=submit]");

    public ProjectSettingsModulesPage(final WebDriver driver) {
        this.driver = driver;
        this.waitWrappers = new WaitWrappers(driver, 30);
    }

    public void setModuleSelection(String module, boolean select) {

        String locator = "//label[@class='form--label'][contains(text(), '" + module +
                "')]/parent::div//input[@type='checkbox']";

        CheckboxWrapper checkboxWrapper = new CheckboxWrapper(driver, By.xpath(locator));
        if (select) {
            checkboxWrapper.check();
        } else {
            checkboxWrapper.uncheck();
        }
    }

    public void saveChanges() {
        waitWrappers.waitForElement(saveButton).click();
    }
}
