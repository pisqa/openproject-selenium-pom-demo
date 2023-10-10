package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import wrappers.WaitWrappers;
import java.util.List;

public class MenuSidebarPage {

    private final WebDriver driver;
    private final WaitWrappers waitWrappers;

    private final By menuRoot = By.cssSelector("#menu-sidebar > ul.menu_root");
    private final By menuItems = By.cssSelector("li .op-menu--item-title>span");

    public MenuSidebarPage(final WebDriver driver) {
        this.driver = driver;
        this.waitWrappers = new WaitWrappers(driver, 30);
    }

    public String[] getMenuItems() {
        WebElement menu = waitWrappers.waitForElement(menuRoot);
        List<WebElement> itemElements = menu.findElements(menuItems);
        String[] items = new String[itemElements.size()];
        for (int i = 0; i < itemElements.size(); i++) {
            items[i] = itemElements.get(i).getText();
        }
        return items;
    }

    public void selectMenuItem(String item) {
        String locator = "//span[@class='op-menu--item-title']/span[text()='" + item + "']";
        waitWrappers.waitForElement(By.xpath(locator)).click();
    }
}

