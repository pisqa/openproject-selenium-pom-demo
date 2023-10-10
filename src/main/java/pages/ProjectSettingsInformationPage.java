package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import wrappers.CheckboxWrapper;
import wrappers.WaitWrappers;

public class ProjectSettingsInformationPage {

    private final WebDriver driver;
    private final WaitWrappers waitWrappers;

    private final By projectNameTextBox = By.cssSelector("[data-qa-field-name=name] input[type=text]");
    private final By publicCheckbox = By.cssSelector("[data-qa-field-name=public] input[type=checkbox]");
    private final By saveButton = By.cssSelector(".op-form--submit button[type=submit]");

    public ProjectSettingsInformationPage(final WebDriver driver) {
        this.driver = driver;
        this.waitWrappers = new WaitWrappers(driver, 30);
    }

    public String getProjectName() {
        return waitWrappers.waitForElement(projectNameTextBox).getAttribute("value");
    }

    public void setProjectPublicSetting(boolean projectIsPublic) {
        CheckboxWrapper checkboxWrapper = new CheckboxWrapper(driver, publicCheckbox);
        if (projectIsPublic) {
            checkboxWrapper.check();
        } else {
            checkboxWrapper.uncheck();
        }
    }

    public void saveChanges() {
        waitWrappers.waitForElement(saveButton).click();
    }
}
