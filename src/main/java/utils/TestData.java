package utils;

import utils.api.MembershipsApi;
import utils.api.ProjectApi;
import utils.api.UserApi;


public class TestData {

    private Config config = new Config();

    public TestData() throws Exception {}

    public void recreateTestData(boolean makeUserProjectMember) throws Exception {

        Log log = new Log();

        //delete project if exists, then create
        ProjectApi projectApi = new ProjectApi(config);
        projectApi.deleteProjectIfExists();
        String projectId = projectApi.createProject();

        //delete user if exists, then create
        UserApi userApi = new UserApi(config);
        userApi.deleteUserIfExists();
        String userId = userApi.createUser();

        //assign user as project member?
        if (makeUserProjectMember) {
            MembershipsApi membershipsApi = new MembershipsApi(config);
            membershipsApi.createMembership(projectId, userId, "3", "Member");
        }
    }

    public void createTestData(boolean makeUserProjectMember) throws Exception {

        Log log = new Log();

        //delete project if exists, then create
        ProjectApi projectApi = new ProjectApi(config);
        projectApi.deleteProjectIfExists();
        String projectId = projectApi.createProject();

//        // create project if it does not exist
//        ProjectApi projectApi = new ProjectApi(config);
//        String projectId = projectApi.createProjectIfNonExistent();

        // create user if it does not exist
        UserApi userApi = new UserApi(config);
        String userId = userApi.createUserIfNonExistent();

        // assign user as project member?
        MembershipsApi membershipsApi = new MembershipsApi(config);
        if (makeUserProjectMember) {
            membershipsApi.createMembershipIfNonExistent(userId, projectId, "3", "Member");
        } else {
            membershipsApi.deleteMembershipIfExists(userId, projectId);
        }
    }
}
