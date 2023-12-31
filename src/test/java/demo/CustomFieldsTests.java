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
import java.time.Duration;
import static org.assertj.core.api.Assertions.assertThat;

public class CustomFieldsTests {

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
    public void addAndRemoveCustomFieldInTask() throws Exception {

        String customGroupName = "Details";
        String customFieldName = "Auto Test Custom Field";
        String taskName = "Auto Test Task";

        // Admin: navigate to custom fields page
        adminDriver.get("https://" + config.getDomainName() + ".openproject.com/custom_fields");
        CustomFieldsListPage customFieldsListPage = new CustomFieldsListPage(adminDriver);
        customFieldsListPage.selectTab("Work packages");

        // Admin: delete the test custom field if it exists
        customFieldsListPage.deleteCustomField(customFieldName);

        // Admin: create custom field
        customFieldsListPage.startCreateCustomField();
        CustomFieldPage customFieldPage = new CustomFieldPage(adminDriver);
        customFieldPage.enterFieldName(customFieldName);
        customFieldPage.selectFormat("bool");
        customFieldPage.saveNewCustomField();

        // Admin: add custom field to task
        // navigate to types page
        adminDriver.get("https://" + config.getDomainName() + ".openproject.com/types");
        WorkPackageTypesListPage workPackageTypesListPage = new WorkPackageTypesListPage(adminDriver);
        workPackageTypesListPage.selectTypeByName("Task");

        TaskSettingsPage taskSettingsPage = new TaskSettingsPage(adminDriver);
        taskSettingsPage.selectTab("Form Configuration");
        taskSettingsPage.moveFormFieldToGroup(customFieldName, "Inactive", customGroupName);
        taskSettingsPage.saveChanges();

        // Test User: create a new task
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

        // see if sleep helps with occasional StaleElementReferenceException in getCustomFieldValue
        Thread.sleep(Duration.ofSeconds(5).toMillis());
        assertThat(workPackagePage.getWorkItemTitle()).isEqualTo(taskName);
        assertThat( workPackagePage.customFieldExistsInGroup(customGroupName, customFieldName)).isTrue();
        assertThat(workPackagePage.getCustomFieldValue(customGroupName, customFieldName)).isEqualTo("yes");

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
