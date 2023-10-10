package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import wrappers.WaitWrappers;

public class ProjectOverviewPage {

    private final WebDriver driver;
    private final WaitWrappers waitWrappers;

    private final By projectDescriptionTextBox = By.cssSelector("[fieldname=description] p");

    public ProjectOverviewPage(final WebDriver driver) {
        this.driver = driver;
        this.waitWrappers = new WaitWrappers(driver, 30);
    }

    public String getProjectDescription() {
        return waitWrappers.waitForElement(projectDescriptionTextBox).getText();
    }
}
