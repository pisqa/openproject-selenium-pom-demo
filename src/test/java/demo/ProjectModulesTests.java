package demo;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import pages.*;
import utils.Config;
import utils.TestData;
import static org.assertj.core.api.Assertions.assertThat;

public class ProjectModulesTests {

    WebDriver adminDriver;
    WebDriver userDriver;
    Config config;

    @BeforeAll
    static void globalSetup() throws Exception {
        new TestData().createTestData(true);
    }

    @BeforeEach
    void setup() throws Exception {
        config = new Config();
        adminDriver = WebDriverManager.chromedriver().create();
        userDriver = WebDriverManager.chromedriver().create();

        LoginPage adminLoginPage = new LoginPage(adminDriver);
        adminLoginPage.login(config.getAdminUser(), config.getAdminPassword());

        LoginPage userLoginPage = new LoginPage(userDriver);
        userLoginPage.login(config.getTestUser(), config.getTestPassword());
    }

    @AfterEach
    void teardown() {
        adminDriver.quit();
        userDriver.quit();
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

        // User: navigate to project page
        userDriver.get("https://" + config.getDomainName() + ".openproject.com/projects/" + config.getTestProjectId());

        // User: verify budgets is available in the sidebar menu
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

        // verify link actually works!
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

        // User: verify deleted modules not available in user session
        // User: navigate to project page
        userDriver.get("https://" + config.getDomainName() + ".openproject.com/projects/" + config.getTestProjectId());

        // verify News is not available in the sidebar menu
        MenuSidebarPage menuSidebarPage = new MenuSidebarPage(userDriver);
        String[] items = menuSidebarPage.getMenuItems();
        assertThat(items).doesNotContain("News");

        // try going directly to News page url
        userDriver.get("https://" + config.getDomainName() + ".openproject.com/projects/" +
                config.getTestProjectId() + "news");

        // verify access not allowed
        String expectedErrorMessage = "[Error 404] The page you were trying to access doesn't exist or has been removed.";
        ToastPage toastPage = new ToastPage(userDriver);
        String toastText = toastPage.getText();
        toastPage.closeToast();
        assertThat(toastText).isEqualTo(expectedErrorMessage);

        // verify wiki is not available in the sidebar menu
        assertThat(items).doesNotContain("Wiki");

        // try going directly to Wiki page url
        userDriver.get("https://" + config.getDomainName() + ".openproject.com/projects/" +
                config.getTestProjectId() + "wiki/wiki");

        // verify access not allowed
        toastText = toastPage.getText();
        toastPage.closeToast();
        assertThat(toastText).isEqualTo(expectedErrorMessage);

        // verify Backlogs is not available in the sidebar menu
        assertThat(items).doesNotContain("Backlogs");

        // try going directly to Backlogs page url
        userDriver.get("https://" + config.getDomainName() + ".openproject.com/projects/" +
                config.getTestProjectId() + "backlogs");

        // verify access not allowed
        toastText = toastPage.getText();
        toastPage.closeToast();
        assertThat(toastText).isEqualTo(expectedErrorMessage);
    }
}
