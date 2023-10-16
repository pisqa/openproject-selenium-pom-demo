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

public class UserProjectRoleTests {

    WebDriver adminDriver;
    WebDriver userDriver;
    Config config;

    @BeforeAll
    static void globalSetup() throws Exception {
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
    public void setUserAsMemberThenWorkPackageCreationAllowed() throws Exception {

        // Admin: Set user as member of project
        // navigate to users page and search for test user
        adminDriver.get("https://" + config.getDomainName() + ".openproject.com/users/");

        UserListPage userListPage = new UserListPage(adminDriver);
        userListPage.selectSearchStatus("active");
        userListPage.enterSearchName(config.getTestUser());
        userListPage.applySearch();

        //verify only one result from search
        assertThat(userListPage.getSearchResultsCount()).isEqualTo(1);
        userListPage.selectUserAtRow(1);

        UserGeneralPage userGeneralPage = new UserGeneralPage(adminDriver);
        userGeneralPage.selectTab("Projects");

        // delete role if it exists
        UserProjectRolesPage userProjectRolesPage = new UserProjectRolesPage(adminDriver);
        userProjectRolesPage.deleteProjectRole(config.getTestProjectName());

        //add role of Member
        userProjectRolesPage.createProjectRole(config.getTestProjectName(), "Member");

        // User: navigate to the project work packages page
        String url = "https://" + config.getDomainName() + ".openproject.com/projects/" +
                config.getTestProjectId() + "/work_packages";
        userDriver.get(url);

        // verify Work Package create buttons enabled
        WorkPackagesListPage workPackagesListPage = new WorkPackagesListPage(userDriver);
        assertThat(workPackagesListPage.toolbarCreateButtonEnabled()).isTrue();
        assertThat(workPackagesListPage.tableCreateButtonAvailable(true)).isTrue();

        // Admin: remove user from project
        userProjectRolesPage.deleteProjectRole(config.getTestProjectName());
    }

    @Test
    public void setUserAsProjectAdminThenWorkPackageCreationAllowed() throws Exception {

        // Admin: Set user as member of project
        // navigate to users page and search for test user
        adminDriver.get("https://" + config.getDomainName() + ".openproject.com/users/");

        UserListPage userListPage = new UserListPage(adminDriver);
        userListPage.selectSearchStatus("active");
        userListPage.enterSearchName(config.getTestUser());
        userListPage.applySearch();

        //verify only one result from search
        assertThat(userListPage.getSearchResultsCount()).isEqualTo(1);
        userListPage.selectUserAtRow(1);

        UserGeneralPage userGeneralPage = new UserGeneralPage(adminDriver);
        userGeneralPage.selectTab("Projects");

        // delete role if it exists
        UserProjectRolesPage userProjectRolesPage = new UserProjectRolesPage(adminDriver);
        userProjectRolesPage.deleteProjectRole(config.getTestProjectName());

        //add role of Project admin
        userProjectRolesPage.createProjectRole(config.getTestProjectName(), "Project admin");

        // User: navigate to the project work packages page
        String url = "https://" + config.getDomainName() + ".openproject.com/projects/" +
                config.getTestProjectId() + "/work_packages";
        userDriver.get(url);

        // User: verify Work Package create buttons enabled
        WorkPackagesListPage workPackagesListPage = new WorkPackagesListPage(userDriver);
        assertThat(workPackagesListPage.toolbarCreateButtonEnabled()).isTrue();
        assertThat(workPackagesListPage.tableCreateButtonAvailable(true)).isTrue();

        // Admin: remove user from project
        userProjectRolesPage.deleteProjectRole(config.getTestProjectName());
    }

    @Test
    public void setUserAsReaderThenWorkPackageCreationNotAllowed() throws Exception {

        // Admin: Set user as member of project
        // navigate to users page and search for test user
        adminDriver.get("https://" + config.getDomainName() + ".openproject.com/users/");

        UserListPage userListPage = new UserListPage(adminDriver);
        userListPage.selectSearchStatus("active");
        userListPage.enterSearchName(config.getTestUser());
        userListPage.applySearch();

        //verify only one result from search
        assertThat(userListPage.getSearchResultsCount()).isEqualTo(1);
        userListPage.selectUserAtRow(1);

        UserGeneralPage userGeneralPage = new UserGeneralPage(adminDriver);
        userGeneralPage.selectTab("Projects");

        // delete role if it exists
        UserProjectRolesPage userProjectRolesPage = new UserProjectRolesPage(adminDriver);
        userProjectRolesPage.deleteProjectRole(config.getTestProjectName());

        // add role of Reader
        userProjectRolesPage.createProjectRole(config.getTestProjectName(), "Reader");

        // User: navigate to the project work packages page
        String url = "https://" + config.getDomainName() + ".openproject.com/projects/" +
                config.getTestProjectId() + "/work_packages";
        userDriver.get(url);

        // verify Work Package create buttons disabled
        WorkPackagesListPage workPackagesListPage = new WorkPackagesListPage(userDriver);
        assertThat(workPackagesListPage.toolbarCreateButtonEnabled()).isFalse();
        assertThat(workPackagesListPage.tableCreateButtonAvailable(false)).isTrue();

        //Admin: remove user from project
        userProjectRolesPage.deleteProjectRole(config.getTestProjectName());
    }

}
