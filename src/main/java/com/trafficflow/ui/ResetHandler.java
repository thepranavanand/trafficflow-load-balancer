package com.trafficflow.ui;

import com.trafficflow.logic.LoadBalancer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;

/**
 * Handles reset API requests
 */
public class ResetHandler implements HttpHandler {
    
    private final LoadBalancer loadBalancer;
    
    public ResetHandler(LoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
    }
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            // Reset the load balancer connection counter
            loadBalancer.resetConnections();
            
            String response = "{\"status\":\"reset\",\"message\":\"Load balancer connection counter reset\"}";
            byte[] responseBytes = response.getBytes("UTF-8");
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, responseBytes.length);
            exchange.getResponseBody().write(responseBytes);
        }
        exchange.getResponseBody().close();
    }
} 