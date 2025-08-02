package com.trafficflow.ui;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.trafficflow.logic.MetricsCollector;
import com.trafficflow.models.ServerMetrics;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.Level;

public class FailureSimulationHandler implements HttpHandler {
    private final MetricsCollector metricsCollector;
    private static final Logger logger = Logger.getLogger(FailureSimulationHandler.class.getName());
    
    public FailureSimulationHandler(MetricsCollector metricsCollector) {
        this.metricsCollector = metricsCollector;
    }
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            String serverType = exchange.getRequestURI().getQuery().split("=")[1];
            String status = "healthy";
            
            if (serverType.equals("single")) {
                metricsCollector.simulateServerFailure("single");
                status = "unhealthy";
            } else if (serverType.equals("threadpool")) {
                metricsCollector.simulateServerFailure("threadpool");
                status = "unhealthy";
            } else if (serverType.equals("multithreaded")) {
                metricsCollector.simulateServerFailure("multithreaded");
                status = "unhealthy";
            }
            
            String response = "{\"status\":\"simulated\",\"server\":\"" + serverType + "\",\"newStatus\":\"" + status + "\"}";
            byte[] responseBytes = response.getBytes("UTF-8");
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, responseBytes.length);
            exchange.getResponseBody().write(responseBytes);
        }
        exchange.getResponseBody().close();
    }
} 