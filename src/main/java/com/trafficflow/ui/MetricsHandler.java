package com.trafficflow.ui;

import com.trafficflow.logic.MetricsCollector;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;

/**
 * Handles metrics API requests
 */
public class MetricsHandler implements HttpHandler {
    
    private final MetricsCollector metricsCollector;
    
    public MetricsHandler(MetricsCollector metricsCollector) {
        this.metricsCollector = metricsCollector;
    }
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String metricsJson = metricsCollector.getMetricsJson();
        byte[] responseBytes = metricsJson.getBytes("UTF-8");
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, responseBytes.length);
        exchange.getResponseBody().write(responseBytes);
        exchange.getResponseBody().close();
    }
}
