package utils.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import utils.Config;
import utils.Log;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class MembershipsApi {
    private final String url;
    private final String apiKey;
    private final String encodedApiKey;

    private final String testProjectName;
    private final String testProjectId;
    private final String testProjectDescription;

    private final Log log;

    public MembershipsApi(final Config config) {
        this.url = "https://" + config.getDomainName() + "." + config.getApiPath() + "memberships/";
        this.apiKey = config.getAdminApiKey();
        this.encodedApiKey = Base64.getEncoder().encodeToString(("apikey:" + apiKey).getBytes());

        this.testProjectName = config.getTestProjectName();
        this.testProjectId = config.getTestProjectId();
        this.testProjectDescription = config.getTestProjectDescription();
        this.log = new Log();
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

    public String getMembershipByPrincipalAndProject(String principalId, String projectId) throws Exception {

        log.info(">>> getMembershipByPrincipalAndProject, url: " + url);

        // add filter parameter
        StringBuilder params = new StringBuilder();
        params.append("[{");
        params.append("\"principal\":");
        params.append("{");
        params.append("\"operator\":\"=\",");
        params.append("\"values\":[\"");
        params.append(principalId);
        params.append("\"");
        params.append("]}},");
        params.append("{");
        params.append("\"project\":");
        params.append("{");
        params.append("\"operator\":\"=\",");
        params.append("\"values\":[\"");
        params.append(projectId);
        params.append("\"");
        params.append("]}}]");

        String encodedParams = URLEncoder.encode(params.toString(), StandardCharsets.UTF_8.toString());
        String urlWithParams = url + "?filters=" + encodedParams;
        log.info(">>> getMembershipByPrincipalAndProject, urlWithParams: " + urlWithParams);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(urlWithParams);

        // add request headers
        request.addHeader("Authorization", "Basic " + encodedApiKey);
        request.addHeader("Content-Type", "application/json");

//        // add body
//        StringBuilder json = new StringBuilder();
//        json.append("{}");

        CloseableHttpResponse response = httpClient.execute(request);
        log.info(">>> getMembershipByPrincipalAndProject, response code: " + response.getStatusLine().getStatusCode());

        // Check HttpResponse Status
        String resultBody;
        if (response.getStatusLine().getStatusCode() != 200) {
            HttpEntity entity = response.getEntity();
            resultBody = EntityUtils.toString(entity);
            String message =
                    "getMembershipByPrincipalAndProject: Call to GET " + url + "\n" +
//                            "Payload:\n" + json + "\n" +
                            "Returned:\n" +
                            response.getStatusLine().getStatusCode() + "\n" +
                            response.getStatusLine().getReasonPhrase() + "\n" +
                            response.getStatusLine().toString() +
                            resultBody.toString();

            throw new Exception(message);
        }
        HttpEntity entity = response.getEntity();
        resultBody = EntityUtils.toString(entity);
        response.close();
        httpClient.close();
        return resultBody;
    }

    public void deleteMembershipById(String membershipId) throws Exception {

        log.info(">>> deleteMembershipById, url: " + url);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpDelete request = new HttpDelete(url + membershipId);

        // add request headers
        request.addHeader("Authorization", "Basic " + encodedApiKey);
        request.addHeader("Content-Type", "application/json");

//        // add body
//        StringBuilder json = new StringBuilder();
//        json.append("{}");

        CloseableHttpResponse response = httpClient.execute(request);
        log.info(">>> deleteMembershipById, response code: " + response.getStatusLine().getStatusCode());

        // Check HttpResponse Status
        if (response.getStatusLine().getStatusCode() != 204) {
            String message =
                    "deleteProject: Call to DELETE " + url + "\n" +
//                            "Payload:\n" + json + "\n" +
                            "Returned:\n" +
                            response.getStatusLine().getStatusCode() + "\n" +
                            response.getStatusLine().getReasonPhrase() + "\n" +
                            response.getStatusLine().toString();
            throw new Exception(message);
        }
        response.close();
        httpClient.close();
    }

    public void deleteMembershipByPrincipalAndProject(String principalId, String projectId) throws Exception {

        log.info(">>> deleteMembershipByPrincipalAndProject, url: " + url);

        String jsonStr = getMembershipByPrincipalAndProject(principalId, projectId);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(jsonStr);

        deleteMembershipById(json.get("_embedded").get("elements").get(0).get("id").asText());
    }


    public void createMembershipIfNonExistent(String principalId, String projectId, String roleId, String roleTitle) throws Exception {

        log.info(">>> createMembershipIfNonexistent, url: " + url);
        String jsonStr = getMembershipByPrincipalAndProject(principalId, projectId);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(jsonStr);
        if (json.get("count").asInt() == 0) {
            log.info(">>> createMembershipIfNonexistent, no membership, creating");
            createMembership(projectId, principalId, roleId, roleTitle);
        } else {
            log.info(">>> createMembershipIfNonexistent, membership exists, not creating");
        }
    }

    public void deleteMembershipIfExists(String principalId, String projectId) throws Exception {

        log.info(">>> deleteMembershipIfExists, url: " + url);
        String jsonStr = getMembershipByPrincipalAndProject(principalId, projectId);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(jsonStr);
        if (json.get("count").asInt() != 0) {
            log.info(">>> deleteMembershipIfExists,membership exists, deleting");
            deleteMembershipByPrincipalAndProject(principalId, projectId);
        } else {
            log.info(">>> deleteMembershipIfExists, membership does not exist, not deleting");
        }
    }
}
