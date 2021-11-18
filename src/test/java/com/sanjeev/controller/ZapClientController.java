package com.sanjeev.controller;

import org.zaproxy.clientapi.core.ApiResponse;
import org.zaproxy.clientapi.core.ApiResponseElement;
import org.zaproxy.clientapi.core.ClientApi;
import org.zaproxy.clientapi.core.ClientApiException;

public class ZapClientController {

    private ClientApi clientApi;
    private String targetUrl;

    public ZapClientController(String localhost, int port, String targetUrl) {
        this.targetUrl = targetUrl;
        clientApi = new ClientApi(localhost, port);
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
        String reportdir = "./zap-security-tests/reports";
        String display = "";

        clientApi.reports.generate(title, template, theme, description, contexts, sites, sections, includedconfidences,
                includedrisks, reportfilename, reportfilenamepattern, reportdir, display);
        System.out.println("Report generated");
        // File.(reportFileName + ".html", clientApi.reports.generate());
    }
}
