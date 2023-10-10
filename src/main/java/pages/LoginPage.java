package pages;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import utils.Config;
import wrappers.WaitWrappers;

public class LoginPage {

    private final WebDriver driver;

    private final By userNameBox = By.id("username");
    private final By passwordBox = By.id("password");
    private final By submitButton = By.cssSelector(".user-login--form > input[type=submit]");
    private final By topMenuUser = By.cssSelector(".op-top-menu-user");
    private final By onboardingModal = By.cssSelector(".onboarding-modal");
    private final By languageSelector = By.cssSelector("select#user_language");
    private final By modalSubmitButton = By.cssSelector("button[type=submit");
    private final By skipTourButton = By.cssSelector(".enjoyhint_skip_btn");

    public LoginPage(final WebDriver driver) {
        this.driver = driver;
    }

    public void login(final String user, final String password) throws Exception {

        WaitWrappers waitWrappers = new WaitWrappers(driver, 30);
        Config config = new Config();

        driver.get("https://" + config.getDomainName() + ".openproject.com/login");
        waitWrappers.waitForElement(userNameBox).sendKeys(user);
        waitWrappers.waitForElement(passwordBox).sendKeys(password);
        waitWrappers.waitForElement(submitButton).click();
        driver.manage().window().maximize();

        // first wait until my/page loads
        waitWrappers.waitForElement(topMenuUser);

        //for new users may get the onboarding modal
        List<WebElement> modalList = driver.findElements(onboardingModal);

        if (!modalList.isEmpty()) {
            //make sure English is selected
            Select select = new Select(waitWrappers.waitForElement(languageSelector));
            select.selectByValue("en");
            driver.findElement(modalSubmitButton).click();
            Thread.sleep(Duration.ofSeconds(1).toMillis());
            waitWrappers.waitToBeClickable(skipTourButton).click();
        }
    }
}