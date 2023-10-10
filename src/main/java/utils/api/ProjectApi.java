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
import java.util.Base64;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import utils.Config;

public class ProjectApi {

    private final String url;
    private final String apiKey;
    private final String encodedApiKey;

    private final String testProjectName;
    private final String testProjectId;
    private final String testProjectDescription;

    public ProjectApi(final Config config) {
        this.url = "https://" + config.getDomainName() + "." + config.getApiPath() + "projects/";
        this.apiKey = config.getAdminApiKey();
        this.encodedApiKey = Base64.getEncoder().encodeToString(("apikey:" + apiKey).getBytes());

        this.testProjectName = config.getTestProjectName();
        this.testProjectId = config.getTestProjectId();
        this.testProjectDescription = config.getTestProjectDescription();
    }

    public String createProject() throws Exception {

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

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpDelete request = new HttpDelete(url + projectId);

        // add request headers
        request.addHeader("Authorization", "Basic " + encodedApiKey);
        request.addHeader("Content-Type", "application/json");

        // add body
        StringBuilder json = new StringBuilder();
        json.append("{}");

        CloseableHttpResponse response = httpClient.execute(request);

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

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(url);

        // add request headers
        request.addHeader("Authorization", "Basic " + encodedApiKey);
        request.addHeader("Content-Type", "application/json");

        // add body
        StringBuilder json = new StringBuilder();
        json.append("{}");

        CloseableHttpResponse response = httpClient.execute(request);

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
        if (entity != null) {
            // return body as a JSON String
            String result = EntityUtils.toString(entity);
            return result;
        }
        response.close();
        httpClient.close();
        return null;
    }

    public void deleteProjectIfExists() throws Exception {

        String jsonStr = getAllProjects();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(jsonStr);

        int projectCount = json.get("count").asInt();
        for (int i = 0; i < projectCount; i++) {
            if (json.get("_embedded").get("elements").get(i).get("name").asText().equals(testProjectName)) {
                deleteProject(json.get("_embedded").get("elements").get(i).get("id").asText());
            }
        }
    }
}

