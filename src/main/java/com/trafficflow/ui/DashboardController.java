package com.trafficflow.ui;

import com.trafficflow.resources.DashboardTemplate;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;

/**
 * Handles HTTP requests for the dashboard HTML
 */
public class DashboardController implements HttpHandler {
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String dashboardHtml = DashboardTemplate.getDashboardHtml();
        byte[] response = dashboardHtml.getBytes("UTF-8");
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, response.length);
        exchange.getResponseBody().write(response);
        exchange.getResponseBody().close();
    }
}
