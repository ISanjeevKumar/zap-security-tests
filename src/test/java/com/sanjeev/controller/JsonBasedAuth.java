package com.sanjeev.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.zaproxy.clientapi.core.ApiResponse;
import org.zaproxy.clientapi.core.ApiResponseElement;
import org.zaproxy.clientapi.core.ClientApiException;

public class JsonBasedAuth extends ZapClient {

    public void setJSONBasedAuthentication() throws ClientApiException, UnsupportedEncodingException {
        String loginUrl = "http://localhost:3000/rest/user/login";
        String loginRequestData = "username={%username%}&password={%password%}";

        // Prepare the configuration in a format similar to how URL parameters are
        // formed. This
        // means that any value we add for the configuration values has to be URL
        // encoded.
        StringBuilder jsonBasedConfig = new StringBuilder();
        jsonBasedConfig.append("loginUrl=").append(URLEncoder.encode(loginUrl, "UTF-8"));
        jsonBasedConfig.append("&loginRequestData=").append(URLEncoder.encode(loginRequestData, "UTF-8"));

        System.out.println("Setting JSON based authentication configuration as: " + jsonBasedConfig.toString());
        clientApi.authentication.setAuthenticationMethod(contextId, "jsonBasedAuthentication",
                jsonBasedConfig.toString());

        // Check if everything is set up ok
        System.out.println(
                "Authentication config: " + clientApi.authentication.getAuthenticationMethod(contextId).toString(0));
    }

    public String setUserAuthConfig() throws ClientApiException, UnsupportedEncodingException {
        // Prepare info
        String user = "Test User";
        String username = "test@example.com";
        String password = "testtest";

        // Make sure we have at least one user
        String userId = extractUserId(clientApi.users.newUser(contextId, user));

        // Prepare the configuration in a format similar to how URL parameters are
        // formed. This
        // means that any value we add for the configuration values has to be URL
        // encoded.
        StringBuilder userAuthConfig = new StringBuilder();
        userAuthConfig.append("username=").append(URLEncoder.encode(username, "UTF-8"));
        userAuthConfig.append("&password=").append(URLEncoder.encode(password, "UTF-8"));

        System.out.println("Setting user authentication configuration as: " + userAuthConfig.toString());
        clientApi.users.setAuthenticationCredentials(contextId, userId, userAuthConfig.toString());
        clientApi.users.setUserEnabled(contextId, userId, "true");
        clientApi.forcedUser.setForcedUser(contextId, userId);
        clientApi.forcedUser.setForcedUserModeEnabled(true);

        // Check if everything is set up ok
        System.out.println("Authentication config: " + clientApi.users.getUserById(contextId, userId).toString(0));
        return userId;
    }

    public void addScript() throws ClientApiException {

        String script_name = "jwtScript.js";
        String script_type = "httpsender";
        String script_engine = "Oracle Nashorn";
        String file_name = "/tmp/authscript.js";

        clientApi.script.load(script_name, script_type, script_engine, file_name, null);
    }

    public void scanAsUser(String userId) throws ClientApiException {
        clientApi.spider.scanAsUser(contextId, userId, targetUrl, null, "true", null);
    }

    private String extractUserId(ApiResponse response) {
        return ((ApiResponseElement) response).getValue();
    }
}
