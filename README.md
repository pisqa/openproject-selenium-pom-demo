# openproject-selenium-pom-demo
This project demonstrates end-to-end testing of a cloud Software as a Service (SaaS) application using Selenium (Java) Page Object Model.

## System Under Test (SUT)
The SUT chosen for this demo is [OpenProject](https://openproject.org/), an open source project management product. 
OpenProject is available in Community edition (downloadable) and Enterprise edition (cloud hosted or on-premises). 
This project has been developed against the Enterprise Cloud Edition (14-day trial).

## Test Cases
The test cases focus on various project or system configuration scenarios, and verify that configuration changes made 
by an Administrator in one browser session take effect in a separate User browser session.

A few random selected functionalities are used in order to demo the Page Object Model.
Absolutely no claims are made regarding test coverage 
:smile:

### [PublicProjectConfigTests](src/test/java/demo/PublicProjectConfigTests.java)
These tests exercise the 
[Public Project](https://www.openproject.org/docs/user-guide/projects/project-settings/project-information/) 
functionality.

#### Test Case: setProjectPublicThenNonMemberAccessAllowed
<ul>
Verify that when a project Public flag is enabled, then a User that is not a member of the project can access it.
</ul>

#### Test Case: setProjectNotPublicThenNonMemberAccessRefused
<ul>
Verify that when a project Public flag is disabled, then a User that is not a member of the project cannot access it.
</ul>

### [UserProjectRoleTests](src/test/java/demo/UserProjectRoleTests.java)
These tests exercise the 
[Project Role](https://www.openproject.org/docs/system-admin-guide/users-permissions/roles-permissions/#project-role)
functionality

#### Test Case: setUserAsMemberThenWorkPackageCreationAllowed
<ul>
Verify that when a user is assigned the role of member of a project, then they have permission to create a new work package.
</ul>

#### Test Case: setUserAsProjectAdminThenWorkPackageCreationAllowed
<ul>
Verify that when a user is assigned the role of administrator of a project, then they have permission to create a new work package.
</ul>

#### Test Case: setUserAsReaderThenWorkPackageCreationNotAllowed
<ul>
Verify that when a user is assigned the role of reader of a project, then they do not have permission to create a new work package.
</ul>

### [ProjectModulesTests](src/test/java/demo/ProjectModulesTests.java)
These tests exercise the
[Project Modules](https://www.openproject.org/docs/user-guide/projects/project-settings/modules/)
functionality

#### Test Case: addProjectModules
<ul>
Verify that when modules are activated for a project, they are available in the project sidebar menu.
</ul>

#### Test Case: setUserAsReaderThenWorkPackageCreationNotAllowed
<ul>
Verify that when modules are deactivated for a project, they are not available in the project sidebar menu, and neither can be accessed by direct url.
</ul>

### [CustomFieldsTests](src/test/java/demo/CustomFieldsTests.java)
This test exercises the
[Custom Fields](https://www.openproject.org/docs/system-admin-guide/custom-fields/)
functionality

#### Test Case: addAndRemoveCustomFieldInTask
<ul>
Verify that when an Administrator creates a new custom field and adds it to the Task Form Configuration, 
then the custom field is available to an ordinary user.

Verify that the custom field appears in the Task Work Package type, but not in Milestone or Phase.

When the Administrator deletes custom field, it is not available to the User.
</ul>

#### Test Steps
1. Admin: In the Administration > Custom Fields page, create a new custom field
2. Admin: In the Administration > Work Packages >  Types page, add the new custom field to the Task Form Configuration
3. User: In the Work Packages page, create a new Task. Verify the custom field is present and set a value
4. User: Open the new Task and verify custom field and value are displayed
5. User: In the Work Packages page, start creation of a Milestone. Verify the custom field is not present, then cancel
6. User: In the Work Packages page, start creation of a Phase. Verify the custom field is not present, then cancel
7. Admin: In the Administration > Custom Fields page, delete the custom field
8. Admin: In the Administration > Work Packages >  Types page, verify the custom field is not present 
in the Task Form Configuration
9. User: In the Work Packages page, open Task created in step 3. Verify the custom field is not present 

## Page Object Model
Page Object Model (POM) is a popular test framework design pattern that aims to improve maintainability, 
robustness and readability of test automation frameworks.

As the name suggests it entails modelling UI application pages as objects, thus creating an abstraction layer
between the test case logic, and the details of the interaction of the UI automation tool (in this case Selenium) 
with the SUT.

The POM design pattern offers three main advantages:

### Maintainability
Selenium driver code that interacts with the SUT are isolated in the page object methods. 
When changes occur in the SUT that require updates in the Selenium code (e.g. page element locator changes), 
then that change only needs to be applied in one page object method.
Without the POM, where each test case calls the Selenium code directly, the same change may need to be replicated
in dozens of test cases.

### Robustness

### Readability
The test case code tends to be more concise, with references to well-named classes and methods making the 
intent and flow of the test case much clearer, as can be seen from this snippet:

```
        // Admin: create custom field
        customFieldsListPage.startCreateCustomField();
        CustomFieldsPage customFieldPage = new CustomFieldsPage(adminDriver);
        customFieldPage.enterFieldName(customFieldName);
        customFieldPage.selectFormat("bool");
        customFieldPage.saveNewCustomField();
```

### Example Usage
Let's walk through an example Page Object, to explain the idea in more depth.
We continue with the previous snippet, taken from 
[src/test/java/demo/CustomFieldsTests.java](src/test/java/demo/CustomFieldsTests.java) (lines 60-65).

The Administrator is about to create a new custom field at https://{domain}.openproject.com/custom_fields:


![Custom fields list](src/main/resources/custom-fields-list.JPG)

The page object for this page is defined in 
[src/test/java/pages/CustomFieldsListPage.java](src/test/java/pages/CustomFieldsListPage.java).


The class CustomFieldsListPage exposes a number of methods including startCreateCustomField, 
which simply clicks on the Create custom field button, and waits for the new custom field page to load: 
```
public void startCreateCustomField() {
    waitWrappers.waitForElement(createCustomFieldButton).click();
    waitWrappers.waitForText(titleText, "New custom field");
}
```

Now we are at https://{<domain}.openproject.com/custom_fields/new?type=WorkPackageCustomField:

![Custom field](src/main/resources/custom-field.JPG)

The page object for this page is defined in
[src/test/java/pages/CustomFieldPage.java](src/test/java/pages/CustomFieldPage.java).

The class CustomFieldPage defines 3 methods to: enter the field name, select the field format and save the new field:

```
    public void enterFieldName(String name) {
        waitWrappers.waitForElement(customFieldName).sendKeys(name);
    }

    public void selectFormat(String format) {
        DropDownWrapper dropDownWrapper = new DropDownWrapper(driver, formatSelector);
        dropDownWrapper.select(format);
    }

    public void saveNewCustomField() {
        waitWrappers.waitForElement(saveButton).click();
    }
```

A couple of things to note about this POM implementation:

· The POM methods are mostly 'low-level' - they interact with a single UI control such as text box or button. An exception is the
[Login Page](src/test/java/pages/LoginPage.java), which is 'high-level'. 
It exposes a single login() method which takes parameters user and password, 
and completes the login process (including logic to handle the possible onboarding modal for new users).

Both approaches are valid, but it is usually recommended to be consistent and stick with one.
For this demo, because login is not the focus of the test cases, I chose to implement that high-level.

· This POM is obviously incomplete, with only the necessary objects/methods required for the demo. 


## Project Structure
Overview of different code components

### [src/test/java/demo](src/test/java/demo)
The high-level test case source files. We use
[JUnit 5](https://junit.org/junit5/docs/current/user-guide/)
 for test runner scaffolding (@BeforeEach, @AfterEach, @test etc).

### [src/main/java/java/pages](src/main/java/java/pages)
The Page Object Model source files. Most of the 




## Execution

### Prerequisites
Maven is required to execute the tests in this project. 

### Configuration
Before running the tests, you will need to configure the following properties in [config.properties](config.properties):
```
# These properties need to be set
domainName=
adminUser=
adminPassword=
adminApiKey=
```
Refer to [API v3 usage example | Basic Auth](https://www.openproject.org/docs/api/example/#basic-auth)
for API key generation.

### Test Execution
To run the tests, execute the following command in the project root folder
```
mvn test
```

The tests should take no more then a couple of minutes to run, and display the following results:


![Execution results](src/main/resources/execution-results.JPG)

### Test Report
To create a test report, execute the following command in the project root folder:
```
mvn surefire-report:report-only
```
The report will be available in target/site/surefire-report.html:


![Test report](src/main/resources/test-report.JPG)

