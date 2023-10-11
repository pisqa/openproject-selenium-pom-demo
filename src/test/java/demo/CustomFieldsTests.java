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

public class CustomFieldsTests {

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
    public void addAndRemoveCustomFieldToTask() throws Exception {

        // Admin: create custom field

        String customGroupName = "Details";
        String customFieldName = "Auto Test Custom Field";
        String taskName = "Auto Test Task";

        adminDriver.get("https://" + config.getDomainName() + ".openproject.com/custom_fields");

        CustomFieldsListPage customFieldsListPage = new CustomFieldsListPage(adminDriver);
        customFieldsListPage.selectTab("Work packages");

        //delete the test custom field if it exists
        customFieldsListPage.deleteCustomField(customFieldName);

        //create custom field
        customFieldsListPage.startCreateCustomField();

        CustomFieldsPage customFieldsPage = new CustomFieldsPage(adminDriver);
        customFieldsPage.enterFieldName(customFieldName);
        customFieldsPage.selectFormat("bool");
        customFieldsPage.saveNewCustomField();

        //Admin: add custom field to task
        //go to types page
        adminDriver.get("https://" + config.getDomainName() + ".openproject.com/types");
        WorkPackageTypesListPage workPackageTypesListPage = new WorkPackageTypesListPage(adminDriver);
        workPackageTypesListPage.selectTypeByName("Task");

        TaskSettingsPage taskSettingsPage = new TaskSettingsPage(adminDriver);
        taskSettingsPage.selectTab("Form Configuration");
        taskSettingsPage.moveFormFieldToGroup(customFieldName, "Inactive", customGroupName);
        taskSettingsPage.saveChanges();

        // Test User: create a new task
        // on a new browser session, login as test user
        WebDriver userDriver = WebDriverManager.chromedriver().create();
        LoginPage loginPage = new LoginPage(userDriver);
        loginPage.login(config.getTestUser(), config.getTestPassword());

        String url = "https://" + config.getDomainName() + ".openproject.com/projects/" +
                config.getTestProjectId() + "/work_packages";
        userDriver.get(url);

        WorkPackagesListPage workPackagesListPage = new WorkPackagesListPage(userDriver);
        workPackagesListPage.selectCreateType("Task");

        // verify custom field shows in task
        WorkPackagePage workPackagePage = new WorkPackagePage(userDriver);
        assertThat( workPackagePage.customFieldExistsInGroup(customGroupName, customFieldName)).isTrue();

        // complete task creation
        workPackagePage.enterWorkPackageName(taskName);
        workPackagePage.setCustomCheckboxField(customGroupName, customFieldName, true);
        workPackagePage.saveChanges();

        // verify task saved correctly with custom fields
        // navigate to Work Packages page
        userDriver.get(url);

        // open the new task
        workPackagesListPage.selectItemByName(taskName);

        // verify title and custom field
        assertThat(workPackagePage.getWorkItemTitle()).isEqualTo(taskName);
        assertThat( workPackagePage.customFieldExistsInGroup(customGroupName, customFieldName)).isTrue();
        assertThat(workPackagePage.getCustomFieldValue(customFieldName)).isEqualTo("yes");

        // verify custom field does not show in new Milestone
        userDriver.get(url);
        workPackagesListPage.selectCreateType("Milestone");
        assertThat( workPackagePage.customFieldExistsInGroup(customGroupName, customFieldName)).isFalse();
        workPackagePage.cancelChanges();

        // verify custom field does not show in new Phase
        workPackagesListPage.selectCreateType("Phase");
        assertThat( workPackagePage.customFieldExistsInGroup(customGroupName, customFieldName)).isFalse();
        workPackagePage.cancelChanges();

        // Admin: delete custom field
        adminDriver.get("https://" + config.getDomainName() + ".openproject.com/custom_fields");
        customFieldsListPage.deleteCustomField(customFieldName);

        // verify field does not appear in task definition
        adminDriver.get("https://" + config.getDomainName() + ".openproject.com/types");
        workPackageTypesListPage.selectTypeByName("Task");
        taskSettingsPage.selectTab("Form Configuration");
        assertThat( taskSettingsPage.fieldExistsInGroup(customGroupName, customFieldName)).isFalse();

        // Test User: verify task does not display custom field
        userDriver.get(url);
        workPackagesListPage.selectItemByName(taskName);
        assertThat(workPackagePage.getWorkItemTitle()).isEqualTo(taskName);
        assertThat( workPackagePage.customFieldExistsInGroup(customGroupName, customFieldName)).isFalse();
    }
}
