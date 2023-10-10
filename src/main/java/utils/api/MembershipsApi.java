package utils.api;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import utils.Config;

import java.util.Base64;

public class MembershipsApi {
    private final String url;
    private final String apiKey;
    private final String encodedApiKey;

    private final String testProjectName;
    private final String testProjectId;
    private final String testProjectDescription;

    public MembershipsApi(final Config config) {
        this.url = "https://" + config.getDomainName() + "." + config.getApiPath() + "memberships/";
        this.apiKey = config.getAdminApiKey();
        this.encodedApiKey = Base64.getEncoder().encodeToString(("apikey:" + apiKey).getBytes());

        this.testProjectName = config.getTestProjectName();
        this.testProjectId = config.getTestProjectId();
        this.testProjectDescription = config.getTestProjectDescription();
    }

    public void createMembership(String projectId, String userId, String roleId, String roleTitle) throws Exception {

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost request = new HttpPost(url);

        // add request headers
        request.addHeader("Authorization", "Basic " + encodedApiKey);
        request.addHeader("Content-Type", "application/json");

        // add body
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"project\": {");
        json.append("\"href\":\"/api/v3/projects/");
        json.append(projectId);
        json.append("\"},");
        json.append("\"principal\": {");
        json.append("\"href\":\"/api/v3/users/");
        json.append(userId);
        json.append("\"},");
        json.append("\"roles\": [ {");
        json.append("\"href\":\"/api/v3/roles/");
        json.append(roleId);
        json.append("\",");
        json.append("\"title\":\"");
        json.append(roleTitle);
        json.append("\"}]}");

        request.setEntity(new StringEntity(json.toString()));
        CloseableHttpResponse response = httpClient.execute(request);

        // Check HttpResponse Status
        if (response.getStatusLine().getStatusCode() != 201) {
            String message =
                    "createProject: Call to POST " + url + "\n" +
                            "Payload:\n" + json + "\n" +
                            "Returned:\n" +
                            response.getStatusLine().getStatusCode() + "\n" +
                            response.getStatusLine().getReasonPhrase() + "\n" +
                            response.getStatusLine().toString();
            throw new Exception(message);
        }

        response.close();
        httpClient.close();
    }
}
