package wrappers;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.regex.Pattern;

public class WaitWrappers {

    private final WebDriver driver;
    private final WebDriverWait webDriverWait;

    public WaitWrappers(final WebDriver driver, int seconds) {
        this.driver = driver;
        this.webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
    }

    public void waitForText(By by, String text) {
        webDriverWait.until(ExpectedConditions.textToBe(by, text));
    }

    public void waitForTextMatch(By by, String regex) {
        webDriverWait.until(ExpectedConditions.textMatches(by, Pattern.compile(regex)));
    }

    public void waitForAttributeValue(By by, String attribute, String value) {
        webDriverWait.until(ExpectedConditions.attributeToBe(by, attribute, value));
    }

    public void waitForSelectedState(By by, Boolean selected) {
        webDriverWait.until(ExpectedConditions.elementSelectionStateToBe(by, selected));
    }

    public WebElement waitForElement(By by) {
        return webDriverWait.until(ExpectedConditions.presenceOfElementLocated(by));
    }

    public WebElement waitToBeClickable(By by) {
        WebDriverWait webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(30));
        return webDriverWait.until(ExpectedConditions.elementToBeClickable(by));
    }

    public Alert waitForAlert() {
        webDriverWait.until(ExpectedConditions.alertIsPresent());
        return driver.switchTo().alert();
    }

    public void waitForStaleness(WebElement we) {
        webDriverWait.until(ExpectedConditions.stalenessOf(we));
    }

    public void waitForInvisibility(By by) {
        webDriverWait.until(ExpectedConditions.invisibilityOfElementLocated(by));
    }
}
