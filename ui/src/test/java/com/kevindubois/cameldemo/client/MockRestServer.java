package com.kevindubois.cameldemo.client;

import java.util.Map;

import com.github.tomakehurst.wiremock.WireMockServer;

import java.util.Collections;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import static com.github.tomakehurst.wiremock.client.WireMock.*;


public class MockRestServer implements QuarkusTestResourceLifecycleManager {
    
    private WireMockServer wireMockServer;
    static final int WIREMOCK_PORT = 7777;
    private static final String BASE_PATH = "/getresults";



    @Override
    public Map<String, String> start() {
        wireMockServer = new WireMockServer(WIREMOCK_PORT);
        wireMockServer.start(); 
        

        wireMockServer.stubFor(get(urlMatching(BASE_PATH))   
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                            """
                                [
                                    {
                                        "id":1, 
                                        "shortname": "quarkus",
                                        "fullname": "Quarkus",
                                        "counter": 1
                                    }
                                ]
                            """                           
                        )));
       
        
        return Collections.singletonMap("quarkus.rest-client.processor-api.url", wireMockServer.baseUrl());
    }

    @Override
    public void stop() {
        if (null != wireMockServer) {
            wireMockServer.stop();  
        }
    }
}
