package com.trafficflow.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration management for TrafficFlow load balancer
 */
public class ServerConfig {
    
    // Server endpoints
    public static final String SINGLE_THREAD_SERVER = getConfigProperty("server.single-thread", "localhost:8001");
    public static final String THREAD_POOL_SERVER = getConfigProperty("server.thread-pool", "localhost:8002");
    public static final String MULTITHREADED_SERVER = getConfigProperty("server.multithreaded", "localhost:8003");
    
    // Ports
    public static final int LOAD_BALANCER_PORT = getIntConfigProperty("loadbalancer.port", 9000);
    public static final int WEB_SERVER_PORT = getIntConfigProperty("webserver.port", 8080);
    
    // Status ports
    public static final int SINGLE_THREAD_STATUS_PORT = getIntConfigProperty("server.single-thread.status-port", 19001);
    public static final int THREAD_POOL_STATUS_PORT = getIntConfigProperty("server.thread-pool.status-port", 19002);
    public static final int MULTITHREADED_STATUS_PORT = getIntConfigProperty("server.multithreaded.status-port", 19003);
    
    // Load balancing thresholds
    public static final int LIGHT_LOAD_THRESHOLD = getIntConfigProperty("loadbalancer.light-threshold", 5);
    public static final int MEDIUM_LOAD_THRESHOLD = getIntConfigProperty("loadbalancer.medium-threshold", 30);
    
    // Timeouts
    public static final int SERVER_CONNECT_TIMEOUT = getIntConfigProperty("server.connect-timeout", 5000);
    public static final int CLIENT_TIMEOUT = getIntConfigProperty("client.timeout", 30000);
    
    // Monitoring
    public static final int METRICS_UPDATE_INTERVAL = getIntConfigProperty("monitoring.metrics-interval", 1000);
    
    // Thread pool settings
    public static final int THREAD_POOL_SIZE = getIntConfigProperty("server.thread-pool-size", 10);
    public static final int MAX_THREADS = getIntConfigProperty("server.max-threads", 1000);
    
    private static final Properties config = loadConfig();
    
    private static Properties loadConfig() {
        Properties props = new Properties();
        try (InputStream input = ServerConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input != null) {
                props.load(input);
            }
        } catch (IOException e) {
            // Use defaults if config file not found
        }
        return props;
    }
    
    private static String getConfigProperty(String key, String defaultValue) {
        if (config != null) {
            return config.getProperty(key, defaultValue);
        }
        return defaultValue;
    }
    
    private static int getIntConfigProperty(String key, int defaultValue) {
        if (config != null) {
            String value = config.getProperty(key);
            if (value != null) {
                try {
                    return Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    // Return default if parsing fails
                }
            }
        }
        return defaultValue;
    }
} 