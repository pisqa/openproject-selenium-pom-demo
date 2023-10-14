# openproject-selenium-pom-demo
This project demonstrates end-to-end testing of a cloud Software as a Service (SaaS) application using Selenium (Java) Page Object Model.

## System Under Test (SUT)
The SUT chosen for this demo is [OpenProject](https://openproject.org/), an open source project management product. 
OpenProject is available in Community edition (downloadable) and Enterprise edition (cloud hosted or on-premises). 
This project has been developed against the Enterprise Cloud Edition (14-day trial).

## Test Cases
The test cases focus on various project or system configuration scenarios, and verify that configuration changes made 
by an Administrator in one browser session take effect in a separate User browser session.

A few random selected functionalities are used in order to demo the Page Object Model. Absolutely no claims are made for test coverage :-)

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
1. Admin: In the Admininistration > Custom Fields page, create a new custom field
1. Admin: In the Admininistration > Work Packages >  Types page, add the new custom field to the Task Form Configuration
1. User: In the Work Packages page, create a new Task. Verify the custom field is present and set a value
2. User: Open the new Task and verify custom field and value are displayed
3. User: In the Work Packages page, start creation of a Milestone. Verify the custom field is not present, then cancel
1. User: In the Work Packages page, start creation of a Phase. Verify the custom field is not present, then cancel
2. Admin: In the Admininistration > Custom Fields page, delete the custom field
3. Admin: In the Admininistration > Work Packages >  Types page, verify the custom field is not present 
in the Task Form Configuration
4. User: In the Work Packages page, open Task created in step 3. Verify the custom field is not present 


