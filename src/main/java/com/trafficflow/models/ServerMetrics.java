package com.trafficflow.models;

public class ServerMetrics {
    private int activeConnections;
    private int responseTime;
    private boolean healthy;
    
    public ServerMetrics(int activeConnections, int responseTime, boolean healthy) {
        this.activeConnections = activeConnections;
        this.responseTime = responseTime;
        this.healthy = healthy;
    }
    
    public int getActiveConnections() {
        return activeConnections;
    }
    
    public int getResponseTime() {
        return responseTime;
    }
    
    public boolean isHealthy() {
        return healthy;
    }
    
    public void setActiveConnections(int activeConnections) {
        this.activeConnections = activeConnections;
    }
    
    public void setResponseTime(int responseTime) {
        this.responseTime = responseTime;
    }
    
    public void setHealthy(boolean healthy) {
        this.healthy = healthy;
    }
} 