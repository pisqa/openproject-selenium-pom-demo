package utils;

import utils.api.MembershipsApi;
import utils.api.ProjectApi;
import utils.api.UserApi;

import java.time.Duration;

public class TestData {

    private Config config = new Config();

    public TestData() throws Exception {}

    public void createTestData(boolean makeUserProjectMember) throws Exception {

        //delete project if exists, then create
        ProjectApi projectApi = new ProjectApi(config);
        projectApi.deleteProjectIfExists();

        Thread.sleep(Duration.ofSeconds(10).toMillis());
        String projectId = projectApi.createProject();

        //delete user if exists, then create
        UserApi userApi = new UserApi(config);
        userApi.deleteUserIfExists();
        Thread.sleep(Duration.ofSeconds(10).toMillis());
        String userId = userApi.createUser();

        //assign user as project member?
        if (makeUserProjectMember) {
            MembershipsApi membershipsApi = new MembershipsApi(config);
            membershipsApi.createMembership(projectId, userId, "3", "Member");
        }
    }
}
