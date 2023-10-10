package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;
import wrappers.WaitWrappers;

public class CustomFieldsPage {

    private final WebDriver driver;
    private final WaitWrappers waitWrappers;

    private final By customFieldName = By.cssSelector("#custom_field_name");
    private final By formatSelector = By.cssSelector("select#custom_field_field_format");
    private final By saveButton = By.cssSelector("button[type=submit]");

    public CustomFieldsPage(final WebDriver driver) {
        this.driver = driver;
        this.waitWrappers = new WaitWrappers(driver, 30);
    }

    public void enterFieldName(String name) {
        waitWrappers.waitForElement(customFieldName).sendKeys(name);
    }

    public void selectFormat(String format) {
        Select select = new Select(waitWrappers.waitForElement(formatSelector));
        select.selectByValue(format);
    }

    public void saveNewCustomField() {
        waitWrappers.waitForElement(saveButton).click();
    }
}
