package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import wrappers.WaitWrappers;

public class UserGeneralPage {

    private final WebDriver driver;
    private final WaitWrappers waitWrappers;

    public UserGeneralPage(final WebDriver driver) {
        this.driver = driver;
        this.waitWrappers = new WaitWrappers(driver, 30);
    }

    public void selectTab(String tab) {
        waitWrappers.waitForElement(By.xpath("//a[@class='op-tab-row--link'][text()='" + tab + "']")).click();
    }
}
