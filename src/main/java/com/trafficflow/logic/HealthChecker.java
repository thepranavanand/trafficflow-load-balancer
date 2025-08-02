package com.trafficflow.logic;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.net.Socket;
import java.net.InetSocketAddress;

public class HealthChecker {
    private static final Logger logger = Logger.getLogger(HealthChecker.class.getName());
    private final LoadBalancer loadBalancer;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    public HealthChecker(LoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
    }
    
    public void start() {
        scheduler.scheduleAtFixedRate(this::checkServerHealth, 0, 10, TimeUnit.SECONDS);
        logger.info("Health checker started");
    }
    
    private void checkServerHealth() {
        checkServer("localhost:8001", "Single-threaded");
        checkServer("localhost:8002", "Thread Pool");
        checkServer("localhost:8003", "Multi-threaded");
    }
    
    private void checkServer(String serverAddress, String serverName) {
        try {
            String[] parts = serverAddress.split(":");
            String host = parts[0];
            int port = Integer.parseInt(parts[1]);
            
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(host, port), 2000);
            socket.close();
            
            loadBalancer.recordServerSuccess(serverAddress);
            logger.fine(serverName + " server health check: OK");
            
        } catch (Exception e) {
            loadBalancer.recordServerFailure(serverAddress);
            logger.log(Level.WARNING, serverName + " server health check: FAILED", e);
        }
    }
    
    public void stop() {
        scheduler.shutdown();
        logger.info("Health checker stopped");
    }
} 