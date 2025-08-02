package com.trafficflow.ui;

import com.trafficflow.config.ServerConfig;
import com.trafficflow.logic.LoadBalancer;
import com.trafficflow.utils.ClientLoadGenerator;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Handles load test API requests
 */
public class LoadTestHandler implements HttpHandler {
    
    private final LoadBalancer loadBalancer;
    
    public LoadTestHandler(LoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
    }
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            // Parse load test request
            BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
            String body = reader.readLine();
            
            int clientCount = Integer.parseInt(body.split("=")[1]);
            
            // Start load test
            startLoadTest(clientCount);
            
            String response = "{\"status\":\"started\",\"clients\":" + clientCount + "}";
            byte[] responseBytes = response.getBytes("UTF-8");
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, responseBytes.length);
            exchange.getResponseBody().write(responseBytes);
        }
        exchange.getResponseBody().close();
    }
    
    private void startLoadTest(int clientCount) {
        // Reset the load balancer connection counter before starting new test
        loadBalancer.resetConnections();
        System.out.println("LoadTestHandler: Reset connection counter before starting " + clientCount + " clients");
        
        new Thread(() -> {
            ClientLoadGenerator generator = new ClientLoadGenerator();
            generator.generateLoad(clientCount, ServerConfig.LOAD_BALANCER_PORT);
        }).start();
    }
}
