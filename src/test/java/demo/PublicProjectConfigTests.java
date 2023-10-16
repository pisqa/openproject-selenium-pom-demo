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
import pages.ToastPage;
import utils.Config;
import utils.TestData;
import wrappers.WaitWrappers;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public class PublicProjectConfigTests {

    WebDriver adminDriver;
    WebDriver userDriver;
    Config config;

    @BeforeAll
    static void globalSetup() throws Exception {

        // Note, for these tests we create test user NOT a project member
        new TestData().createTestData(false);
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
    public void setProjectPublicThenNonMemberAccessAllowed() throws Exception {

        // Admin: Navigate to project settings page
        adminDriver.get("https://" + config.getDomainName() + ".openproject.com/projects/" +
                config.getTestProjectId() + "/settings/general/");

        ProjectSettingsInformationPage projectSettingsInformationPage =
                new ProjectSettingsInformationPage(adminDriver);

        // verify we are on the right page!
        assertThat(projectSettingsInformationPage.getProjectName()).isEqualTo(config.getTestProjectName());

        // set the project to public
        projectSettingsInformationPage.setProjectPublicSetting(true);

        // save changes
        projectSettingsInformationPage.saveChanges();

        // User: access the public project
        Thread.sleep(Duration.ofSeconds(3).toMillis());
        userDriver.get("https://" + config.getDomainName() + ".openproject.com/projects/" + config.getTestProjectId());

        // verify project description
        ProjectOverviewPage projectOverviewPage = new ProjectOverviewPage(userDriver);
        assertThat(projectOverviewPage.getProjectDescription()).isEqualTo(config.getTestProjectDescription());
    }

    @Test
    public void setProjectNotPublicThenNonMemberAccessRefused() throws Exception {

        // Admin: Navigate to project settings page
        adminDriver.get("https://" + config.getDomainName() + ".openproject.com/projects/" +
                config.getTestProjectId() + "/settings/general/");

        ProjectSettingsInformationPage projectSettingsInformationPage =
                new ProjectSettingsInformationPage(adminDriver);

        // verify we are on the right page!
        assertThat(projectSettingsInformationPage.getProjectName()).isEqualTo(config.getTestProjectName());

        // set the project to non-public
        projectSettingsInformationPage.setProjectPublicSetting(false);

        // save changes
        projectSettingsInformationPage.saveChanges();

        // User: try to access the non-public project
        Thread.sleep(Duration.ofSeconds(3).toMillis());
        userDriver.get("https://" + config.getDomainName() +
                ".openproject.com/projects/" + config.getTestProjectId());

        // verify access not allowed
        String expectedErrorMessage = "[Error 403] You are not authorized to access this page.";
        ToastPage toastPage = new ToastPage(userDriver);
        String toastText = toastPage.getText();
        toastPage.closeToast();
        assertThat(toastText).isEqualTo(expectedErrorMessage);
    }
}
