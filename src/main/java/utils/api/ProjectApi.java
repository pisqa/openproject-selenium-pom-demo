package utils.api;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.http.entity.StringEntity;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import utils.Config;
import utils.Log;

public class ProjectApi {

    private final String url;
    private final String apiKey;
    private final String encodedApiKey;

    private final String testProjectName;
    private final String testProjectId;
    private final String testProjectDescription;

    private final Log log;

    public ProjectApi(final Config config) {
        this.url = "https://" + config.getDomainName() + "." + config.getApiPath() + "projects/";
        this.apiKey = config.getAdminApiKey();
        this.encodedApiKey = Base64.getEncoder().encodeToString(("apikey:" + apiKey).getBytes());

        this.testProjectName = config.getTestProjectName();
        this.testProjectId = config.getTestProjectId();
        this.testProjectDescription = config.getTestProjectDescription();
        this.log = new Log();
    }

    public String createProject() throws Exception {

        log.info(">>> createProject, url: " + url);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost request = new HttpPost(url);

        // add request headers
        request.addHeader("Authorization", "Basic " + encodedApiKey);
        request.addHeader("Content-Type", "application/json");

        // add body
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"identifier\":\"");
        json.append(testProjectId);
        json.append("\",");
        json.append("\"name\":\"");
        json.append(testProjectName);
        json.append("\",");
        json.append("\"description\": {");
        json.append("\"format\": \"markdown\",");
        json.append("\"raw\":\"");
        json.append(testProjectDescription);
        json.append("\"");
        json.append("}}");

        log.info(">>> createProject, body: " + json);

        request.setEntity(new StringEntity(json.toString()));
        CloseableHttpResponse response = httpClient.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        log.info(">>> createProject, response code: " + statusCode);

        // work-around for intermittent 422 error: retry after delay
        int retries = 4;
        int delay = 15;
        while (statusCode == 422 && retries > 0) {
            log.info(">>> createProject, delay & retry after 422");
            Thread.sleep(Duration.ofSeconds(delay).toMillis());
            response = httpClient.execute(request);
            statusCode = response.getStatusLine().getStatusCode();
            log.info(">>> createProject, status code after retry: " + statusCode);
            retries--;
        }

        if (statusCode != 201) {
            String message =
                    "createProject: Call to POST " + url + "\n" +
                            "Payload:\n" + json + "\n" +
                            "Returned:\n" +
                            response.getStatusLine().getStatusCode() + "\n" +
                            response.getStatusLine().getReasonPhrase() + "\n" +
                            response.getStatusLine().toString();
            throw new Exception(message);
        }

        // extract project id
        HttpEntity entity = response.getEntity();
        String result = EntityUtils.toString(entity);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonRs = mapper.readTree(result);

        String id = jsonRs.get("id").asText();
        response.close();
        httpClient.close();
        return id;
    }

    public void deleteProject(String projectId) throws Exception {

        log.info(">>> deleteProject, url: " + url + projectId);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpDelete request = new HttpDelete(url + projectId);

        // add request headers
        request.addHeader("Authorization", "Basic " + encodedApiKey);
        request.addHeader("Content-Type", "application/json");

        // add body
        StringBuilder json = new StringBuilder();
        json.append("{}");

        CloseableHttpResponse response = httpClient.execute(request);
        log.info(">>> deleteProject, response code: " + response.getStatusLine().getStatusCode());

        // Check HttpResponse Status
        if (response.getStatusLine().getStatusCode() != 204) {
            String message =
                    "deleteProject: Call to DELETE " + url + "\n" +
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

    public String getAllProjects() throws Exception {

        log.info(">>> getAllProjects, url: " + url);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(url);

        // add request headers
        request.addHeader("Authorization", "Basic " + encodedApiKey);
        request.addHeader("Content-Type", "application/json");

        // add body
        StringBuilder json = new StringBuilder();
        json.append("{}");

        CloseableHttpResponse response = httpClient.execute(request);
        log.info(">>> getAllProjects, response code: " + response.getStatusLine().getStatusCode());

        // Check HttpResponse Status
        if (response.getStatusLine().getStatusCode() != 200) {
            String message =
                    "getAllProjects: Call to GET " + url + "\n" +
                            "Payload:\n" + json + "\n" +
                            "Returned:\n" +
                            response.getStatusLine().getStatusCode() + "\n" +
                            response.getStatusLine().getReasonPhrase() + "\n" +
                            response.getStatusLine().toString();
            throw new Exception(message);
        }

        HttpEntity entity = response.getEntity();
        String result = null;

        if (entity != null) {
            // return body as a JSON String
            result = EntityUtils.toString(entity);
        }
        response.close();
        httpClient.close();
        return result;
    }

    public String getProjectByNameAndIdentifier() throws Exception {

        log.info(">>> getProjectByNameAndIdentifier, url: " + url);

        // add filter parameter
        StringBuilder params = new StringBuilder();
        params.append("[{");
        params.append("\"name_and_identifier\":");
        params.append("{");
        params.append("\"operator\":\"=\",");
        params.append("\"values\":[\"");
        params.append(testProjectName);
        params.append("\",\"");
        params.append(testProjectId);
        params.append("\"");
        params.append("]}}]");

        String encodedParams = URLEncoder.encode(params.toString(), StandardCharsets.UTF_8.toString());
        String urlWithParams = url + "?filters=" + encodedParams;
        log.info(">>> getProjectByNameAndIdentifier, urlWithParams: " + urlWithParams);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(urlWithParams);

        // add request headers
        request.addHeader("Authorization", "Basic " + encodedApiKey);
        request.addHeader("Content-Type", "application/json");

//        // add body
//        StringBuilder json = new StringBuilder();
//        json.append("{}");

        CloseableHttpResponse response = httpClient.execute(request);
        log.info(">>> getProjectByNameAndIdentifier, response code: " + response.getStatusLine().getStatusCode());

        // Check HttpResponse Status
        String resultBody;
        if (response.getStatusLine().getStatusCode() != 200) {
            String message =
                    "getProjectByNameAndIdentifier: Call to GET " + url + "\n" +
//                            "Payload:\n" + json + "\n" +
                            "Returned:\n" +
                            response.getStatusLine().getStatusCode() + "\n" +
                            response.getStatusLine().getReasonPhrase() + "\n" +
                            response.getStatusLine().toString();
            throw new Exception(message);
        }
        HttpEntity entity = response.getEntity();
        resultBody = EntityUtils.toString(entity);
        response.close();
        httpClient.close();
        return resultBody;
    }


    public void deleteProjectIfExists() throws Exception {

        log.info(">>> deleteProjectIfExists, url: " + url);

        String jsonStr = getProjectByNameAndIdentifier();
        if (jsonStr == null) {
            log.info(">>> deleteProjectIfExists, no projects");
            return;
        }
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(jsonStr);

        int projectCount = json.get("count").asInt();
        log.info(">>> deleteProjectIfExists, checking " + projectCount + " projects");

        for (int i = 0; i < projectCount; i++) {
            if (json.get("_embedded").get("elements").get(i).get("name").asText().equals(testProjectName)) {
                String pid = json.get("_embedded").get("elements").get(i).get("id").asText();
                log.info(">>> deleteProjectIfExists, deleting project id " + pid);
                deleteProject(pid);
            }
        }
    }

    public String createProjectIfNonExistent() throws Exception {

        log.info(">>> createProjectIfNotExists, url: " + url);

        String jsonStr = getProjectByNameAndIdentifier();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(jsonStr);
        if (json.get("count").asInt() == 0) {
            log.info(">>> createProjectIfNotExists, no project, creating");
            return createProject();
        } else {
            log.info(">>> createProjectIfNotExists, project exists, not creating");
            return json.get("_embedded").get("elements").get(0).get("id").asText();
        }
    }
}

