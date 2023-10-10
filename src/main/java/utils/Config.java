package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Properties;

public class Config {

    private final String domainName;
    private final String adminUser;
    private final String adminPassword;
    private final String adminApiKey;
    private final String testUser;
    private final String testPassword;
    private final String userFirstName;
    private final String userLastName;
    private final String userEmail;
    private final String apiPath;
    private final String testProjectName;
    private final String testProjectId;
    private final String testProjectDescription;

    public String getTestProjectDescription() {
        return testProjectDescription;
    }

    public Config() throws Exception {

        Properties properties;
        String propertyFilePath = "config.properties";
        BufferedReader reader = new BufferedReader(new FileReader(propertyFilePath));
        properties = new Properties();
        properties.load(reader);
        reader.close();

        this.domainName = properties.getProperty("domainName");
        this.adminUser = properties.getProperty("adminUser");
        this.adminPassword = properties.getProperty("adminPassword");
        this.adminApiKey = properties.getProperty("adminApiKey");
        this.testUser = properties.getProperty("testUser");
        this.testPassword = properties.getProperty("testPassword");
        this.userFirstName = properties.getProperty("userFirstName");
        this.userLastName = properties.getProperty("userLastName");
        this.userEmail = properties.getProperty("userEmail");
        this.apiPath = properties.getProperty("apiPath");
        this.testProjectName = properties.getProperty("testProjectName");
        this.testProjectId = properties.getProperty("testProjectId");
        this.testProjectDescription = properties.getProperty("testProjectDescription");
    }

    public String getDomainName() {
        return domainName;
    }

    public String getAdminUser() {
        return adminUser;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public String getAdminApiKey() {
        return adminApiKey;
    }

    public String getTestUser() {
        return testUser;
    }

    public String getTestPassword() {
        return testPassword;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getApiPath() {
        return apiPath;
    }

    public String getTestProjectName() {
        return testProjectName;
    }

    public String getTestProjectId() {
        return testProjectId;
    }
}


