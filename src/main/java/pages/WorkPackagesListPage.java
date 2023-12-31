package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import utils.Log;
import wrappers.WaitWrappers;


public class WorkPackagesListPage {

    private final WebDriver driver;
    private final WaitWrappers waitWrappers;

    private final By wpTable = By.cssSelector("table.work-package-table");
    private final By addWpButton = By.cssSelector("button.add-work-package");
    private final By tableCreateButton = By.cssSelector("td.wp-inline-create-button-td");
    private final By fullScreenButton = By.cssSelector(".wp--details--switch-fullscreen");

    public WorkPackagesListPage(final WebDriver driver) {
        this.driver = driver;
        this.waitWrappers = new WaitWrappers(driver, 30);
    }

    public boolean toolbarCreateButtonEnabled() {
        WebElement createButton = waitWrappers.waitForElement(addWpButton);
        return createButton.isEnabled();
    }

    public boolean tableCreateButtonAvailable(boolean expectedAvailability) {
        Log log = new Log();

        WebElement memTab = waitWrappers.waitForElement(wpTable);
        if (expectedAvailability) {
            try {
                waitWrappers.waitForElement(tableCreateButton);
                return true;
            } catch (Exception e) {
                log.info("tableCreateButtonAvailable, exception waiting for " + tableCreateButton.toString());
                log.info(e.getMessage());
            }
        } else {
            try {
                waitWrappers.waitForInvisibility(tableCreateButton);
                return true;
            } catch (Exception e) {
                log.info("tableCreateButtonAvailable, exception waiting for invisibility of" +
                        tableCreateButton.toString());
                log.info(e.getMessage());
            }
        }
        return false;
    }

    public void selectCreateType(String type) {
        waitWrappers.waitForElement(addWpButton).click();
        waitWrappers.waitForElement(By.cssSelector(".dropdown-menu .menu-item[aria-label='" + type + "']")).click();

        //switch details to full screen
        waitWrappers.waitForElement(fullScreenButton).click();
    }

    public void selectItemByName(String itemName) {
        String locator = "//span[@data-field-name='subject' and @title='" + itemName +
                "']/ancestor::tr//span[@data-field-name='id']";
        waitWrappers.waitForElement(By.xpath(locator)).click();
    }
}

