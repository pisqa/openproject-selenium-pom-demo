package utils.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClients;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.openqa.selenium.WebDriver;
import utils.Config;
import wrappers.WaitWrappers;

import java.time.Duration;
import java.util.Base64;

public class UserApi {

    private final String url;
    private final String apiKey;
    private final String encodedApiKey;
    private final Config config;

    public UserApi(final Config config) {
        this.url = "https://" + config.getDomainName() + "." + config.getApiPath() + "users/";
        this.apiKey = config.getAdminApiKey();
        this.encodedApiKey = Base64.getEncoder().encodeToString(("apikey:" + apiKey).getBytes());
        this.config = config;
    }

    // default version - takes data from config file
    public String createUser() throws Exception {
        Config cfg = this.config;
        return createUser(cfg.getTestUser(), cfg.getTestPassword(), cfg.getUserFirstName(),
                cfg.getUserLastName(), cfg.getUserEmail(), false, "active", "en");
    }

    public String createUser(String login, String password, String firstName, String lastName,
                           String email, Boolean admin, String status, String language) throws Exception {

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost request = new HttpPost(this.url);

        // add request headers
        request.addHeader("Authorization", "Basic " + encodedApiKey);
        request.addHeader("Content-Type", "application/json");

        // add body
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"login\":\"" + login + "\",");
        json.append("\"password\":\"" + password + "\",");
        json.append("\"firstName\":\"" + firstName + "\",");
        json.append("\"lastName\":\"" + lastName + "\",");
        json.append("\"email\":\"" + email + "\",");
        json.append("\"admin\":" + (admin ? "true" : "false") + ",");
        json.append("\"status\":\"" + status + "\",");
        json.append("\"language\":\"" + language + "\"");
        json.append("}");

        request.setEntity(new StringEntity(json.toString()));
        CloseableHttpResponse response = httpClient.execute(request);

        // Check HttpResponse Status
        if (response.getStatusLine().getStatusCode() != 201) {
            String message =
                    "createUser: Call to POST " + url + "\n" +
                            "Payload:\n" + json + "\n" +
                            "Returned:\n" +
                            response.getStatusLine().getStatusCode() + "\n" +
                            response.getStatusLine().getReasonPhrase() + "\n" +
                            response.getStatusLine().toString();
            throw new Exception(message);
        }

        // extract user id
        HttpEntity entity = response.getEntity();
        String result = EntityUtils.toString(entity);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonRs = mapper.readTree(result);

        String id = jsonRs.get("id").asText();

        response.close();
        httpClient.close();

        return id;
    }

    public void deleteUser(String userId) throws Exception {

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpDelete request = new HttpDelete(this.url + userId);

        // add request headers
        request.addHeader("Authorization", "Basic " + encodedApiKey);
        request.addHeader("Content-Type", "application/json");

        // add body
        StringBuilder json = new StringBuilder();
        json.append("{}");

        CloseableHttpResponse response = httpClient.execute(request);

        // Check HttpResponse Status
        if (response.getStatusLine().getStatusCode() != 202) {
            String message =
                    "deleteUser: Call to DELETE " + url + "\n" +
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

    public String getAllUsers() throws Exception {

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(this.url);

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
                    "getAllUsers: Call to GET " + url + "\n" +
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

    public void deleteUserIfExists() throws Exception {

        String jsonStr = getAllUsers();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(jsonStr);

        int userCount = json.get("count").asInt();
        for (int i = 0; i < userCount; i++) {
            if (json.get("_embedded").get("elements").get(i).get("login").asText().equals(this.config.getTestUser())) {
                deleteUser(json.get("_embedded").get("elements").get(i).get("id").asText());
                // See if delay after delete helps with occasional 422 error on create
                Thread.sleep(Duration.ofSeconds(20).toMillis());
            }
        }
    }
}

