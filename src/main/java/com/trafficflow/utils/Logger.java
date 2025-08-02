package com.trafficflow.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    
    public enum Level {
        DEBUG, INFO, WARN, ERROR
    }
    
    private final String className;
    private final Level minLevel;
    
    public Logger(Class<?> clazz) {
        this(clazz, Level.INFO);
    }
    
    public Logger(Class<?> clazz, Level minLevel) {
        this.className = clazz.getSimpleName();
        this.minLevel = minLevel;
    }
    
    public static Logger getLogger(Class<?> clazz) {
        return new Logger(clazz);
    }
    
    public static Logger getLogger(Class<?> clazz, Level minLevel) {
        return new Logger(clazz, minLevel);
    }
    
    private void log(Level level, String message) {
        if (level.ordinal() >= minLevel.ordinal()) {
            String timestamp = LocalDateTime.now().format(formatter);
            System.out.printf("[%s] %s %s - %s%n", 
                timestamp, level.name(), className, message);
        }
    }
    
    public void debug(String message) {
        log(Level.DEBUG, message);
    }
    
    public void info(String message) {
        log(Level.INFO, message);
    }
    
    public void warn(String message) {
        log(Level.WARN, message);
    }
    
    public void error(String message) {
        log(Level.ERROR, message);
    }
    
    public void error(String message, Throwable throwable) {
        log(Level.ERROR, message + " - " + throwable.getMessage());
        throwable.printStackTrace();
    }
} 