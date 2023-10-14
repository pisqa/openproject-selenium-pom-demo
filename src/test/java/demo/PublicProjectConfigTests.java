package demo;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import pages.LoginPage;
import pages.ProjectSettingsInformationPage;
import pages.ProjectOverviewPage;
import utils.Config;
import utils.TestData;
import utils.api.MembershipsApi;
import utils.api.ProjectApi;
import utils.api.UserApi;
import wrappers.WaitWrappers;

import static org.assertj.core.api.Assertions.assertThat;

public class PublicProjectConfigTests {

    WebDriver adminDriver;
    Config config;

    @BeforeAll
    static void globalSetup() throws Exception {

        // Note, for these tests we create test user NOT a project member
        new TestData().createTestData(false);
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

//    @Test
//    public void dummyTest() throws Exception {
//
////        UserApi uapi = new UserApi(config);
////        uapi.createUserIfNonExistent();
//
////        ProjectApi papi = new ProjectApi(config);
////        String pid = papi.createProjectIfNotExists();
//
////        MembershipsApi mapi = new MembershipsApi(config);
////        mapi.createMembershipIfNonExistent("5", "5", "3", "Member");
//
//        new TestData().createTestData(true);
//        return;
//
//    }

    @Test
    public void setProjectPublicThenNonMemberAccessAllowed() throws Exception {

        // Navigate to project settings page
        adminDriver.get("https://" + config.getDomainName() + ".openproject.com/projects/" +
                config.getTestProjectId() + "/settings/general/");

        ProjectSettingsInformationPage projectSettingsInformationPage = new ProjectSettingsInformationPage(adminDriver);

        // verify we are on the right page!
        assertThat(projectSettingsInformationPage.getProjectName()).isEqualTo(config.getTestProjectName());

        // set the project to public
        projectSettingsInformationPage.setProjectPublicSetting(true);

        // save changes
        projectSettingsInformationPage.saveChanges();

        // on a new browser session, login as test user
        WebDriver userDriver = WebDriverManager.chromedriver().create();
        LoginPage loginPage = new LoginPage(userDriver);
        loginPage.login(config.getTestUser(), config.getTestPassword());

        // access the public project
        userDriver.get("https://" + config.getDomainName() + ".openproject.com/projects/" + config.getTestProjectId());

        // verify project description
        ProjectOverviewPage projectOverviewPage = new ProjectOverviewPage(userDriver);
        assertThat(projectOverviewPage.getProjectDescription()).isEqualTo(config.getTestProjectDescription());
    }

    @Test
    public void setProjectNotPublicThenNonMemberAccessRefused() throws Exception {

        // Navigate to project settings page
        adminDriver.get("https://" + config.getDomainName() + ".openproject.com/projects/" +
                config.getTestProjectId() + "/settings/general/");

        ProjectSettingsInformationPage projectSettingsInformationPage = new ProjectSettingsInformationPage(adminDriver);

        // verify we are on the right page!
        assertThat(projectSettingsInformationPage.getProjectName()).isEqualTo(config.getTestProjectName());

        // set the project to non-public
        projectSettingsInformationPage.setProjectPublicSetting(false);

        // save changes
        projectSettingsInformationPage.saveChanges();

        // on a new browser session, login as test user
        WebDriver driver2 = WebDriverManager.chromedriver().create();
        LoginPage loginPage = new LoginPage(driver2);
        loginPage.login(config.getTestUser(), config.getTestPassword());

        // try to access the non-public project
        driver2.get("https://" + config.getDomainName() + ".openproject.com/projects/" + config.getTestProjectId());

        // verify access not allowed
        WaitWrappers waitWrappers = new WaitWrappers(driver2, 30);
        WebElement we = waitWrappers.waitForElement(By.cssSelector(".op-toast--content"));
        assertThat(we.getText().contains("[Error 403] You are not authorized to access this page."));
    }
}
