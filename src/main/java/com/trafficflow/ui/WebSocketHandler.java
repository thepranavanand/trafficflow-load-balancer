package com.trafficflow.ui;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.trafficflow.logic.MetricsCollector;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * WebSocket handler for real-time metrics updates
 */
public class WebSocketHandler implements HttpHandler {
    private final MetricsCollector metricsCollector;
    private static final Logger logger = Logger.getLogger(WebSocketHandler.class.getName());
    
    public WebSocketHandler(MetricsCollector metricsCollector) {
        this.metricsCollector = metricsCollector;
    }
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Server-Sent Events for real-time updates
        exchange.getResponseHeaders().set("Content-Type", "text/event-stream");
        exchange.getResponseHeaders().set("Cache-Control", "no-cache");
        exchange.getResponseHeaders().set("Connection", "keep-alive");
        exchange.sendResponseHeaders(200, 0);
        
        OutputStream output = exchange.getResponseBody();
        
        // Send periodic updates
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    String metricsJson = metricsCollector.getMetricsJson();
                    String data = "data: " + metricsJson + "\n\n";
                    output.write(data.getBytes());
                    output.flush();
                } catch (IOException e) {
                    logger.log(Level.WARNING, "Failed to send metrics update", e);
                    timer.cancel();
                }
            }
        }, 0, 1000);
    }
}
