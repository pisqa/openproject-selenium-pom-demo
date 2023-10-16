package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import wrappers.CheckboxWrapper;
import wrappers.DropDownWrapper;
import wrappers.WaitWrappers;
import java.util.List;

public class UserProjectRolesPage {

    private final WebDriver driver;
    private final WaitWrappers waitWrappers;

    private final By membershipsTable = By.cssSelector("table.memberships");
    private final By availableProjectsDropdown = By.cssSelector("form#new_project_membership div[role=combobox]");
    private final By noResultsTable = By.className("generic-table--no-results-title");
    private final By rowDeleteButton = By.cssSelector("a[data-method=delete]");
    private final By createButton = By.cssSelector("button[type=submit]");

    public UserProjectRolesPage(final WebDriver driver) {
        this.driver = driver;
        this.waitWrappers = new WaitWrappers(driver, 30);
    }

    // if a project role exists delete it
    // otherwise do nothing
    public void deleteProjectRole(String project) {

        // first check if there are no roles for any project
        if (!driver.findElements(noResultsTable).isEmpty()) {
            return;
        }

        // check if there is a role for the specified project
        WebElement memTab = waitWrappers.waitForElement(membershipsTable);
        String locator = "//td[@class='project']//a[text() = '" + project + "']";
        List<WebElement> roles = memTab.findElements(By.xpath(locator));
        if (roles.size() != 0) {
            //there can only be one - delete it
            WebElement row = roles.get(0).findElement(By.xpath("ancestor::tr"));
            row.findElement(rowDeleteButton).click();
        }
    }

    public void createProjectRole(String project, String role) {

        DropDownWrapper availableProjects = new DropDownWrapper(driver, availableProjectsDropdown);
        availableProjects.select(project);

        By roleCheckbox = By.xpath("//label[text()='" + role + "']//input[@type='checkbox']");
        CheckboxWrapper checkboxWrapper = new CheckboxWrapper(driver, roleCheckbox);
        checkboxWrapper.check();

        //save
        driver.findElement(createButton).click();

        // verify role is displayed in table
        String locator = "//td[@class='project']//a[text()='" + project +
                "']/ancestor::tr/td[@class='roles']/span";
        String regex = "\\s*" + role + "\\s*";
        waitWrappers.waitForTextMatch(By.xpath(locator), regex);
    }
}
