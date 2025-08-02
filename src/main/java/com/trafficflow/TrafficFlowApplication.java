package com.trafficflow;

import com.trafficflow.config.ServerConfig;
import com.trafficflow.logic.LoadBalancer;
import com.trafficflow.logic.MetricsCollector;
import com.trafficflow.ui.DashboardController;
import com.trafficflow.ui.LoadTestHandler;
import com.trafficflow.ui.MetricsHandler;
import com.trafficflow.ui.ResetHandler;
import com.trafficflow.ui.WebSocketHandler;
import com.trafficflow.ui.FailureSimulationHandler;
import com.trafficflow.logic.HealthChecker;
import com.trafficflow.utils.ClientLoadGenerator;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TrafficFlowApplication {
    
    private LoadBalancer loadBalancer;
    private MetricsCollector metricsCollector;
    private HealthChecker healthChecker;
    private ScheduledExecutorService monitoringScheduler;
    
    public static void main(String[] args) {
        TrafficFlowApplication app = new TrafficFlowApplication();
        app.start();
    }
    
    public void start() {
        System.out.println("Starting TrafficFlow Load Balancer Dashboard...");
        
        loadBalancer = new LoadBalancer();
        metricsCollector = new MetricsCollector();
        healthChecker = new HealthChecker(loadBalancer);
        
        startWebServer();
        
        startRealTimeMonitoring();
        
        healthChecker.start();
        
        loadBalancer.start();
        
        System.out.println("Dashboard available at: http://localhost:" + ServerConfig.WEB_SERVER_PORT);
        System.out.println("Load Balancer listening on port: " + ServerConfig.LOAD_BALANCER_PORT);
    }
    
    private void startWebServer() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(ServerConfig.WEB_SERVER_PORT), 0);
            
            server.createContext("/", new DashboardController());
            
            server.createContext("/ws", new WebSocketHandler(metricsCollector));
            
            server.createContext("/api/load-test", new LoadTestHandler(loadBalancer));
            server.createContext("/api/metrics", new MetricsHandler(metricsCollector));
            server.createContext("/api/reset", new ResetHandler(loadBalancer));
            server.createContext("/api/simulate-failure", new FailureSimulationHandler(metricsCollector));
            
            server.setExecutor(Executors.newFixedThreadPool(10));
            server.start();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void startRealTimeMonitoring() {
        monitoringScheduler = Executors.newScheduledThreadPool(2);
        monitoringScheduler.scheduleAtFixedRate(() -> {
            metricsCollector.updateServerMetrics();
        }, 0, ServerConfig.METRICS_UPDATE_INTERVAL, TimeUnit.MILLISECONDS);
    }
    
    public void stop() {
        if (loadBalancer != null) {
            loadBalancer.stop();
        }
        if (healthChecker != null) {
            healthChecker.stop();
        }
        if (monitoringScheduler != null) {
            monitoringScheduler.shutdown();
        }
    }
}
