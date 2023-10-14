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
    public void activateProjectModules() throws Exception {

        // as admin, go to project settings modules page
        adminDriver.get("https://" + config.getDomainName() + ".openproject.com/projects/" +
                config.getTestProjectId() + "/settings/modules");

        // add a number of modules that are not present in project by default: budgets, calendars, documents
        ProjectSettingsModulesPage projectSettingsModulesPage = new ProjectSettingsModulesPage(adminDriver);
        projectSettingsModulesPage.setModuleSelection("Budgets", true);
        projectSettingsModulesPage.setModuleSelection("Calendars", true);
        projectSettingsModulesPage.setModuleSelection("Documents", true);
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
        BudgetsListPage budgetsListPage = new BudgetsListPage(userDriver);
        assertThat(budgetsListPage.getPageHeader()).isEqualTo("Budgets");

        // verify calendars is available in the sidebar menu
        assertThat(items).contains("Calendars");

        //verify link actually works!
        menuSidebarPage.selectMenuItem("Calendars");
        CalendarsListPage calendarsListPage = new CalendarsListPage(userDriver);
        assertThat(calendarsListPage.getPageHeader()).isEqualTo("Calendars");
        userDriver.get("https://" + config.getDomainName() + ".openproject.com/projects/" + config.getTestProjectId());

        // verify documents is available in the sidebar menu
        assertThat(items).contains("Documents");

        //verify link actually works!
        menuSidebarPage.selectMenuItem("Documents");
        DocumentsListPage documentsListPage = new DocumentsListPage(userDriver);
        assertThat(documentsListPage.getPageHeader()).isEqualTo("Documents");



    }

    @Test
    public void deactivateProjectModules() throws Exception {

        // as admin, go to project settings modules page
        adminDriver.get("https://" + config.getDomainName() + ".openproject.com/projects/" +
                config.getTestProjectId() + "/settings/modules");

        // delete a number of modules that are present in project by default: news, wiki, backlogs
        ProjectSettingsModulesPage projectSettingsModulesPage = new ProjectSettingsModulesPage(adminDriver);
        projectSettingsModulesPage.setModuleSelection("News", false);
        projectSettingsModulesPage.setModuleSelection("Wiki", false);
        projectSettingsModulesPage.setModuleSelection("Backlogs", false);
        projectSettingsModulesPage.saveChanges();

        // on a new browser session, login as test user
        WebDriver userDriver = WebDriverManager.chromedriver().create();
        LoginPage loginPage = new LoginPage(userDriver);
        loginPage.login(config.getTestUser(), config.getTestPassword());


        // verify deleted modules not available in user session
        // go to project page
        userDriver.get("https://" + config.getDomainName() + ".openproject.com/projects/" + config.getTestProjectId());

        // verify News is not available in the sidebar menu
        MenuSidebarPage menuSidebarPage = new MenuSidebarPage(userDriver);
        String[] items = menuSidebarPage.getMenuItems();
        assertThat(items).doesNotContain("News");

        // try going directly to News page url
        userDriver.get("https://" + config.getDomainName() + ".openproject.com/projects/" +
                config.getTestProjectId() + "news");

        // verify access not allowed
        WaitWrappers waitWrappers = new WaitWrappers(userDriver, 30);
        WebElement we = waitWrappers.waitForElement(By.cssSelector(".op-toast--content"));
        assertThat(we.getText().contains("[Error 403] You are not authorized to access this page."));

        // verify wiki is not available in the sidebar menu
        assertThat(items).doesNotContain("Wiki");

        // try going directly to Wiki page url
        userDriver.get("https://" + config.getDomainName() + ".openproject.com/projects/" +
                config.getTestProjectId() + "wiki/wiki");

        // verify access not allowed
        waitWrappers = new WaitWrappers(userDriver, 30);
        we = waitWrappers.waitForElement(By.cssSelector(".op-toast--content"));
        assertThat(we.getText().contains("[Error 403] You are not authorized to access this page."));

        // verify Backlogs is not available in the sidebar menu
        assertThat(items).doesNotContain("Backlogs");

        // try going directly to Wiki page url
        userDriver.get("https://" + config.getDomainName() + ".openproject.com/projects/" +
                config.getTestProjectId() + "backlogs");

        // verify access not allowed
        waitWrappers = new WaitWrappers(userDriver, 30);
        we = waitWrappers.waitForElement(By.cssSelector(".op-toast--content"));
        assertThat(we.getText().contains("[Error 403] You are not authorized to access this page."));


    }
}
