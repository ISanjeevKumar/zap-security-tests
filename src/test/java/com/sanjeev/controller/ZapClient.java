package com.sanjeev.controller;

import org.zaproxy.clientapi.core.ApiResponseElement;
import org.zaproxy.clientapi.core.ClientApi;
import org.zaproxy.clientapi.core.ClientApiException;

public class ZapClient {

    protected String targetUrl;
    protected ClientApi clientApi;
    protected String contextId;
 
    public ZapClient(String zapAddress, int port, String targetUrl, String contextName) throws ClientApiException{
        this.targetUrl = targetUrl;
        System.out.println("Creating new Session");
        clientApi = new ClientApi(zapAddress, port);
        contextId =createContext(contextName);
    }

    public ZapClient(){
        
    }
    

    private String createContext(String contextName) throws ClientApiException {
        String contextId = ((ApiResponseElement) clientApi.context.newContext(contextName)).getValue();
        System.out.println(contextName + " context created with id " + contextId);
        return contextId;
    }
}
