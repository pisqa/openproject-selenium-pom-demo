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
import utils.Log;
import wrappers.WaitWrappers;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

public class UserApi {

    private final String url;
    private final String apiKey;
    private final String encodedApiKey;
    private final Config config;

    private final Log log;

    public UserApi(final Config config) {
        this.url = "https://" + config.getDomainName() + "." + config.getApiPath() + "users/";
        this.apiKey = config.getAdminApiKey();
        this.encodedApiKey = Base64.getEncoder().encodeToString(("apikey:" + apiKey).getBytes());
        this.config = config;
        this.log = new Log();
    }

    // default version - takes data from config file
    public String createUser() throws Exception {
        Config cfg = this.config;
        return createUser(cfg.getTestUser(), cfg.getTestPassword(), cfg.getUserFirstName(),
                cfg.getUserLastName(), cfg.getUserEmail(), false, "active", "en");
    }

    public String createUser(String login, String password, String firstName, String lastName,
                             String email, Boolean admin, String status, String language) throws Exception {

        log.info(">>> createUser, url: " + url);

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

        log.info(">>> createUser, body: " + json);

        request.setEntity(new StringEntity(json.toString()));
        CloseableHttpResponse response = httpClient.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        log.info(">>> createUser, response code: " + statusCode);

        // work-around for intermittent 422 error: retry after delay
        int retries = 4;
        int delay = 15;
        while (statusCode == 422 && retries > 0) {
            log.info(">>> createUser, delay & retry after 422");
            Thread.sleep(Duration.ofSeconds(delay).toMillis());
            response = httpClient.execute(request);
            statusCode = response.getStatusLine().getStatusCode();
            log.info(">>> createUser, status code after retry: " + statusCode);
            retries--;
        }

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

        log.info(">>> createUser, returning id: " + id);
        return id;
    }

    public void deleteUser(String userId) throws Exception {

        log.info(">>> deleteUser, url: " + url + userId);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpDelete request = new HttpDelete(this.url + userId);

        // add request headers
        request.addHeader("Authorization", "Basic " + encodedApiKey);
        request.addHeader("Content-Type", "application/json");

        // add body
        StringBuilder json = new StringBuilder();
        json.append("{}");

        CloseableHttpResponse response = httpClient.execute(request);
        log.info(">>> deleteUser, response code: " + response.getStatusLine().getStatusCode());

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

        log.info(">>> getAllUsers, url: " + url);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(this.url);

        // add request headers
        request.addHeader("Authorization", "Basic " + encodedApiKey);
        request.addHeader("Content-Type", "application/json");

        // add body
        StringBuilder json = new StringBuilder();
        json.append("{}");

        CloseableHttpResponse response = httpClient.execute(request);
        log.info(">>> getAllUsers, response code: " + response.getStatusLine().getStatusCode());

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

    public String getUserByLogin() throws Exception {

        log.info(">>> getUserByLogin, url: " + url);

        // add filter parameter
        StringBuilder params = new StringBuilder();
        params.append("[{");
        params.append("\"login\":");
        params.append("{");
        params.append("\"operator\":\"=\",");
        params.append("\"values\":[\"");
        params.append(config.getTestUser());
        params.append("\"");
        params.append("]}}]");

        String encodedParams = URLEncoder.encode(params.toString(), StandardCharsets.UTF_8.toString());
        String urlWithParams = url + "?filters=" + encodedParams;
        log.info(">>> getUserByLogin, urlWithParams: " + urlWithParams);

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



    public void deleteUserIfExists() throws Exception {

        log.info(">>> deleteUserIfExists, url: " + url);

        String jsonStr = getAllUsers();
        if (jsonStr == null) {
            log.info(">>> deleteUserIfExists, no users");
            return;
        }
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(jsonStr);

        int userCount = json.get("count").asInt();
        log.info(">>> deleteUserIfExists, checking " + userCount + " users");

        for (int i = 0; i < userCount; i++) {
            if (json.get("_embedded").get("elements").get(i).get("login").asText().equals(this.config.getTestUser())) {
                String uid = json.get("_embedded").get("elements").get(i).get("id").asText();
                log.info(">>> deleteProjectIfExists, deleting project id " + uid);
                deleteUser(uid);
            }
        }
    }



    public String createUserIfNonExistent() throws Exception {

        log.info(">>> createUserIfNonExistent, url: " + url);

        String jsonStr = getUserByLogin();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(jsonStr);
        if (json.get("count").asInt() == 0) {
            log.info(">>> createUserIfNonExistent, no user, creating");
            return createUser();
        } else {
            log.info(">>> createUserIfNonExistent, user exists, not creating");
            return json.get("_embedded").get("elements").get(0).get("id").asText();
        }
    }


}

