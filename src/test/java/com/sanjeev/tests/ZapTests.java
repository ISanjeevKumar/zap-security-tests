package com.sanjeev.tests;

import com.sanjeev.controller.ZapClientController;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.zaproxy.clientapi.core.ClientApiException;

public class ZapTests {

    private ZapClientController zap;

    @BeforeTest
    public void instantiateZapClient() {
        zap = new ZapClientController("localhost", 8081, "http://localhost:3000");
    }

    @AfterTest
    public void disposeClientApi() throws ClientApiException {
        zap.generateHtmlReport();
    }

    @Test(description = "Start Spider Scan against JuiceShop application")
    public void spiderScanTest() {

        try {
            {
                String scanId = zap.startSpiderScan();
                System.out.println("Spider ScanId:" + scanId);
                zap.waitTillSpiderScanIsCompleted(scanId);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
