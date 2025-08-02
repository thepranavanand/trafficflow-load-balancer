package com.trafficflow.logic;

import com.trafficflow.config.ServerConfig;
import com.trafficflow.models.ServerMetrics;
import java.io.*;
import java.net.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.logging.Level;

public class MetricsCollector {
    private Map<String, ServerMetrics> serverMetrics = new ConcurrentHashMap<>();
    private static final Logger logger = Logger.getLogger(MetricsCollector.class.getName());
    
    public void updateServerMetrics() {
        serverMetrics.put("single", getServerMetrics(ServerConfig.SINGLE_THREAD_SERVER, ServerConfig.SINGLE_THREAD_STATUS_PORT));
        serverMetrics.put("threadpool", getServerMetrics(ServerConfig.THREAD_POOL_SERVER, ServerConfig.THREAD_POOL_STATUS_PORT));  
        serverMetrics.put("multithreaded", getServerMetrics(ServerConfig.MULTITHREADED_SERVER, ServerConfig.MULTITHREADED_STATUS_PORT));
    }
    
    private ServerMetrics getServerMetrics(String server, int statusPort) {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress("localhost", statusPort), 1000);
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String response = reader.readLine();
            socket.close();
            
            int connections = Integer.parseInt(response.split(",")[0]);
            long responseTime = Long.parseLong(response.split(",")[1]);
            
            return new ServerMetrics(connections, (int)responseTime, true);
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to get metrics from " + server, e);
            return new ServerMetrics(0, 0, false);
        }
    }
    
    public String getMetricsJson() {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"timestamp\":").append(System.currentTimeMillis()).append(",");
        json.append("\"totalLoad\":").append(getTotalLoad()).append(",");
        json.append("\"servers\":{");
        
        boolean first = true;
        for (Map.Entry<String, ServerMetrics> entry : serverMetrics.entrySet()) {
            if (!first) json.append(",");
            ServerMetrics m = entry.getValue();
            json.append("\"").append(entry.getKey()).append("\":{");
            json.append("\"connections\":").append(m.getActiveConnections()).append(",");
            json.append("\"responseTime\":").append(m.getResponseTime()).append(",");
            json.append("\"healthy\":").append(m.isHealthy());
            json.append("}");
            first = false;
        }
        
        json.append("}}");
        return json.toString();
    }
    
    private int getTotalLoad() {
        return serverMetrics.values().stream()
            .mapToInt(ServerMetrics::getActiveConnections)
            .sum();
    }
    
    public Map<String, ServerMetrics> getServerMetrics() {
        return serverMetrics;
    }
    
    public void simulateServerFailure(String serverType) {
        serverMetrics.put(serverType, new ServerMetrics(0, 0, false));
        logger.info("Simulated failure for server: " + serverType);
    }
} 