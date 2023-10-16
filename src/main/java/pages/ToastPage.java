package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import wrappers.WaitWrappers;

public class ToastPage {
    private final WebDriver driver;
    private final WaitWrappers waitWrappers;

    private final By toastContent = By.cssSelector(".op-toast--content");
    private final By closeButton = By.cssSelector(".op-toast .close-handler");

    public ToastPage(final WebDriver driver) {
        this.driver = driver;
        this.waitWrappers = new WaitWrappers(driver, 30);
    }

    public String getText() {
        return waitWrappers.waitForElement(toastContent).getText();
    }

    public void closeToast() {
        waitWrappers.waitForElement(closeButton).click();
    }
}
