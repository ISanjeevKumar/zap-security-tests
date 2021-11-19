package com.sanjeev.tests;

import com.sanjeev.controller.JsonBasedAuth;
import com.sanjeev.controller.ZapClientController;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.zaproxy.clientapi.core.ClientApiException;

public class ZapTests {

    private static final int ZAP_PORT = 8081;
    private static final String ZAP_ADDRESS = "localhost";
    private static final String TARGET_URL = "http://localhost:3000";
    private static final String CONTEXT_NAME = "JuiceShop";
    private ZapClientController zap;
    private JsonBasedAuth jsonAuth;

    @BeforeTest
    public void instantiateZapClient() throws ClientApiException {
        zap = new ZapClientController(ZAP_ADDRESS, ZAP_PORT, TARGET_URL, CONTEXT_NAME);
        jsonAuth = new JsonBasedAuth();
    }

    @AfterTest
    public void disposeClientApi() throws ClientApiException {
        zap.generateHtmlReport();
    }

    @Test(description = "Scan the application before login")
    public void spiderScanTest() {

        try {
            {
                // Start the spider scan
                String scanId = zap.startSpiderScan();
                System.out.println("Spider ScanId:" + scanId);
                zap.waitTillSpiderScanIsCompleted(scanId);
                // Start the active scan
                String activeScanId = zap.startActiveScan();
                zap.waitTillActiveScanIsCompleted(activeScanId);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test(description = "Scan the application after login")
    public void loginBasedScan() {
        try {
            {
                String loginUrl = "http://localhost:3000/rest/user/login";
                String userId = "";
                String userName = "test@test.com";
                String password = "admin@123";
                // zap.setAuthenticationJsonBased(loginUrl);
                // String userId =
                // zap.setUserAuthConfig("customer","test@test.com","admin@123");
                // String scanId = zap.scanAsUser("1", userId);
                // zap.waitTillSpiderScanIsCompleted(scanId);
                zap.loadTargetUrlToSitesTree();
                String contextName = CONTEXT_NAME;
                String contextId = zap.createContext(contextName);
                String urlToIncludeInContext = "http://localhost:3000/.*";
                zap.includeUrlToContext(contextName, urlToIncludeInContext);

                userId = zap.createNewUser(contextId, userName);
                zap.setAuthenticationJsonBased(loginUrl, contextId, userId, userName, password);
                zap.enableUser(contextId, userId);
                zap.setASpecificForcedUser(contextId, userId);
                zap.enableForcedUserMode();

                String scanId = zap.scanAsUser(contextId, userId);
                zap.waitTillSpiderScanIsCompleted(scanId);

                zap.startAjaxSpidering(contextName);
                zap.waitTillAjaxScanCompleted();

                String activeScanId = zap.startActiveScan();
                zap.waitTillActiveScanIsCompleted(activeScanId);

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
