package com.sanjeev.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

import org.zaproxy.clientapi.core.ApiResponse;
import org.zaproxy.clientapi.core.ApiResponseElement;
import org.zaproxy.clientapi.core.ClientApiException;

public class ZapClientController extends ZapClient{

    public ZapClientController(String zapAddress, int port, String targetUrl, String contextName) throws ClientApiException {
        super(zapAddress, port, targetUrl,contextName);
    }

    public String startSpiderScan() {
        try {
            ApiResponse response = clientApi.spider.scan(targetUrl, null, null, null, null);
            String scanid = ((ApiResponseElement) response).getValue();
            return scanid;
        } catch (Exception e) {
            return null;
        }

    }

    public String scanAsUser(String contextId, String userId) throws ClientApiException {
        try {
            ApiResponse response = clientApi.spider.scanAsUser(contextId, userId, targetUrl, null, "JuiceShop", null);
            String scanid = ((ApiResponseElement) response).getValue();
            return scanid;
        } catch (Exception e) {
            return null;
        }
    }

    public void waitTillSpiderScanIsCompleted(String scanId)
            throws InterruptedException, NumberFormatException, ClientApiException {
        int progress;
        while (true) {
            Thread.sleep(1000);
            progress = Integer.parseInt(((ApiResponseElement) clientApi.spider.status(scanId)).getValue());
            System.out.println("Spider progress : " + progress + "%");
            if (progress >= 100) {
                break;
            }
        }
        System.out.println("Spider completed");
    }

    public  void startAjaxSpidering(String contextName) throws ClientApiException
    {
        System.out.println("Ajax Spider: " +targetUrl);

        if ("OK" == ((ApiResponseElement)clientApi.ajaxSpider.scan(targetUrl, "", contextName, "")).getValue())
            System.out.println("Ajax Spider started for " + targetUrl);
    }

    public void waitTillAjaxScanCompleted() throws ClientApiException, InterruptedException{
        String status;

        long startTime = System.currentTimeMillis();
        long timeout = TimeUnit.MINUTES.toMillis(2); // Two minutes in milli seconds
        // Loop until the ajax spider has finished or the timeout has exceeded
        while (true) {
            Thread.sleep(2000);
            status = (((ApiResponseElement) clientApi.ajaxSpider.status()).getValue());
            System.out.println("Spider status : " + status);
            if (!("stopped".equals(status)) || (System.currentTimeMillis() - startTime) < timeout) {
                break;
            }
        }
        System.out.println("Ajax Spider completed");
    }

    public String startActiveScan() {
        try {
            ApiResponse resp = clientApi.ascan.scan(targetUrl, "True", null, null, null, null);
            String scanid = ((ApiResponseElement) resp).getValue();
            return scanid;
        } catch (Exception e) {
            return null;
        }
    }

    public void waitTillActiveScanIsCompleted(String scanId)
            throws InterruptedException, NumberFormatException, ClientApiException {
        int progress;
        while (true) {
            Thread.sleep(5000);
            progress = Integer.parseInt(((ApiResponseElement) clientApi.ascan.status(scanId)).getValue());
            System.out.println("Active Scan progress : " + progress + "%");
            if (progress >= 100) {
                break;
            }
        }
        System.out.println("Active Scan complete");
    }

    public void setAuthenticationJsonBased(String loginUrl,String contextId, String userId, String userName, String password) {
        try {

            String loginRequestData = "username={%username%}&password={%password%}";
            // Prepare the configuration in a format similar to how URL parameters are
            // formed. This
            // means that any value we add for the configuration values has to be URL
            // encoded.
            StringBuilder jsonBasedConfig = new StringBuilder();
            jsonBasedConfig.append("loginUrl=").append(URLEncoder.encode(loginUrl, "UTF-8"));
            jsonBasedConfig.append("&loginRequestData=").append(URLEncoder.encode(loginRequestData, "UTF-8"));

            System.out.println("Setting JSON based authentication configuration as: " + jsonBasedConfig.toString());
            clientApi.authentication.setAuthenticationMethod(contextId, userId,
                    jsonBasedConfig.toString());
        } catch (Exception message) {
            new Throwable(message);
        }
    }

    public String setUserAuthConfig(String userType, String username, String password)
            throws ClientApiException, UnsupportedEncodingException {
        // Make sure we have at least one user
        String userId = ((ApiResponseElement) clientApi.users.newUser("1", userType)).getValue();

        // Prepare the configuration in a format similar to how URL parameters are
        // formed. This
        // means that any value we add for the configuration values has to be URL
        // encoded.
        StringBuilder userAuthConfig = new StringBuilder();
        userAuthConfig.append("username=").append(URLEncoder.encode(username, "UTF-8"));
        userAuthConfig.append("&password=").append(URLEncoder.encode(password, "UTF-8"));

        System.out.println("Setting user authentication configuration as: " + userAuthConfig.toString());
        clientApi.users.setAuthenticationCredentials("1", userId, userAuthConfig.toString());
        clientApi.users.setUserEnabled("1", userId, "true");
        clientApi.forcedUser.setForcedUser("1", userId);
        clientApi.forcedUser.setForcedUserModeEnabled(true);

        // Check if everything is set up ok
        System.out.println("Authentication config: " + clientApi.users.getUserById("1", userId).toString(0));
        return userId;
    }


    public String createNewUser(String contextId, String userName) throws ClientApiException
    {
        String userId = ((ApiResponseElement)clientApi.users.newUser(contextId, userName)).getValue();
        return userId;
    }

    public void setASpecificForcedUser(String contextId, String userId) throws ClientApiException
    {
        clientApi.forcedUser.setForcedUser(contextId, userId);
    }
    
    public void enableForcedUserMode() throws ClientApiException
    {
        clientApi.forcedUser.setForcedUserModeEnabled(true);
    }

    public void enableUser(String contextId, String userId) throws ClientApiException
    {

        if ("OK" == ((ApiResponseElement)clientApi.users.setUserEnabled(contextId, userId, "true")).getValue())
            System.out.println("user enabled");
    }

    public void loadTargetUrlToSitesTree() throws ClientApiException {
        clientApi.core.accessUrl(targetUrl, "true");
    }

    public String createContext(String contextName) throws ClientApiException {
        String contextId = ((ApiResponseElement) clientApi.context.newContext(contextName)).getValue();
        System.out.println(contextName + " context created with id " + contextId);
        return contextId;
    }

    public void includeUrlToContext(String contextName, String urlToIncludeInContext) throws ClientApiException {
        if ("OK" == ((ApiResponseElement)clientApi.context.includeInContext(contextName, urlToIncludeInContext))
                .getValue())
            System.out.println(urlToIncludeInContext + " included to context " + contextName);

    }

    public void generateHtmlReport() throws ClientApiException {
        String title = "JuiceShop App";
        String template = "traditional-html-plus";
        String theme = "light";
        String description = " JuiceShop Tests";
        String contexts = "";
        String sites = "";
        String sections = "";
        String includedconfidences = "";
        String includedrisks = "";
        String reportfilename = "";
        String reportfilenamepattern = "";
        String reportdir = System.getProperty("user.dir") + "/reports";
        String display = "";
        clientApi.reports.generate(title, template, theme, description, contexts, sites, sections, includedconfidences,
                includedrisks, reportfilename, reportfilenamepattern, reportdir, display);
        System.out.println("Report generated:" + reportdir);
        // File.(reportFileName + ".html", clientApi.reports.generate());
    }
}
