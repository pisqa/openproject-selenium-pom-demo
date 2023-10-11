package demo;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import pages.*;
import utils.Config;
import utils.TestData;
import wrappers.WaitWrappers;

import static org.assertj.core.api.Assertions.assertThat;

public class ProjectModulesTests {

    WebDriver adminDriver;
    Config config;

    @BeforeAll
    static void globalSetup() throws Exception {
        new TestData().createTestData(true);
    }

    @BeforeEach
    void setup() throws Exception {
        adminDriver = WebDriverManager.chromedriver().create();
        config = new Config();
        LoginPage loginPage = new LoginPage(adminDriver);
        loginPage.login(config.getAdminUser(), config.getAdminPassword());
    }

    @AfterEach
    void teardown() {
        adminDriver.quit();
    }


    @Test
    public void addAndDeleteProjectModules() throws Exception {

        // as admin, go to project settings modules page
        adminDriver.get("https://" + config.getDomainName() + ".openproject.com/projects/" +
                config.getTestProjectId() + "/settings/modules");

        // add budgets module
        ProjectSettingsModulesPage projectSettingsModulesPage = new ProjectSettingsModulesPage(adminDriver);
        projectSettingsModulesPage.setModuleSelection("Budgets", true);
        projectSettingsModulesPage.saveChanges();

        // on a new browser session, login as test user
        WebDriver userDriver = WebDriverManager.chromedriver().create();
        LoginPage loginPage = new LoginPage(userDriver);
        loginPage.login(config.getTestUser(), config.getTestPassword());

        // go to project page
        userDriver.get("https://" + config.getDomainName() + ".openproject.com/projects/" + config.getTestProjectId());

        // verify budgets is available in the sidebar menu
        MenuSidebarPage menuSidebarPage = new MenuSidebarPage(userDriver);
        String[] items = menuSidebarPage.getMenuItems();
        assertThat(items).contains("Budgets");

        //verify link actually works!
        menuSidebarPage.selectMenuItem("Budgets");

        BudgetsPage budgetsPage = new BudgetsPage(userDriver);
        assertThat(budgetsPage.getPageHeader()).isEqualTo("Budgets");

        // now remove budgets module from project

        // as admin, go to project settings modules page
        adminDriver.get("https://" + config.getDomainName() + ".openproject.com/projects/" +
                config.getTestProjectId() + "/settings/modules");

        // remove budgets module
        projectSettingsModulesPage = new ProjectSettingsModulesPage(adminDriver);
        projectSettingsModulesPage.setModuleSelection("Budgets", false);
        projectSettingsModulesPage.saveChanges();

        // verify budgets module not available in user session
        // go to project page
        userDriver.get("https://" + config.getDomainName() + ".openproject.com/projects/" + config.getTestProjectId());

        // verify budgets is not available in the sidebar menu
        menuSidebarPage = new MenuSidebarPage(userDriver);
        items = menuSidebarPage.getMenuItems();
        assertThat(items).doesNotContain("Budgets");

        // try going directly to budgets page url
        userDriver.get("https://" + config.getDomainName() + ".openproject.com/projects/" +
                config.getTestProjectId() + "budgets");

        // verify access not allowed
        WaitWrappers waitWrappers = new WaitWrappers(userDriver, 30);
        WebElement we = waitWrappers.waitForElement(By.cssSelector(".op-toast--content"));
        assertThat(we.getText().contains("[Error 403] You are not authorized to access this page."));
    }
}
