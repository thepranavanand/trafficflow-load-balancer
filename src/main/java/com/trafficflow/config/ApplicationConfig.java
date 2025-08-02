package com.trafficflow.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Application configuration management
 */
public class ApplicationConfig {
    private static final Properties properties = new Properties();
    private static final String CONFIG_FILE = "application.properties";
    
    static {
        loadConfiguration();
    }
    
    private static void loadConfiguration() {
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            properties.load(input);
        } catch (IOException e) {
            // Use default values if config file not found
            System.err.println("Configuration file not found, using defaults: " + e.getMessage());
            setDefaultProperties();
        }
    }
    
    private static void setDefaultProperties() {
        // Server configurations
        properties.setProperty("server.thread-pool-size", "10");
        properties.setProperty("server.max-threads", "1000");
        properties.setProperty("server.connection-timeout", "30000");
        properties.setProperty("server.read-timeout", "60000");
        
        // Load balancer configurations
        properties.setProperty("loadbalancer.light-threshold", "10");
        properties.setProperty("loadbalancer.medium-threshold", "80");
        properties.setProperty("loadbalancer.auto-reset-delay", "2000");
        
        // Monitoring configurations
        properties.setProperty("monitoring.metrics-interval", "1000");
        properties.setProperty("monitoring.health-check-interval", "5000");
        
        // Logging configurations
        properties.setProperty("logging.level", "INFO");
        properties.setProperty("logging.enable-timestamps", "true");
    }
    
    public static String getString(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    public static int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(properties.getProperty(key));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    public static long getLong(String key, long defaultValue) {
        try {
            return Long.parseLong(properties.getProperty(key));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    public static boolean getBoolean(String key, boolean defaultValue) {
        try {
            return Boolean.parseBoolean(properties.getProperty(key));
        } catch (Exception e) {
            return defaultValue;
        }
    }
    
    // Convenience methods for common configurations
    public static int getThreadPoolSize() {
        return getInt("server.thread-pool-size", 10);
    }
    
    public static int getMaxThreads() {
        return getInt("server.max-threads", 1000);
    }
    
    public static int getConnectionTimeout() {
        return getInt("server.connection-timeout", 30000);
    }
    
    public static int getLightLoadThreshold() {
        return getInt("loadbalancer.light-threshold", 10);
    }
    
    public static int getMediumLoadThreshold() {
        return getInt("loadbalancer.medium-threshold", 80);
    }
    
    public static int getMetricsInterval() {
        return getInt("monitoring.metrics-interval", 1000);
    }
    
    public static String getLogLevel() {
        return getString("logging.level", "INFO");
    }
} 