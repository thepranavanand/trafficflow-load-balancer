package com.trafficflow.logic;

import com.trafficflow.utils.Logger;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CircuitBreaker {
    private static final Logger logger = Logger.getLogger(CircuitBreaker.class);
    
    public enum State {
        CLOSED,
        OPEN,
        HALF_OPEN
    }
    
    private volatile State state = State.CLOSED;
    private final AtomicInteger failureCount = new AtomicInteger(0);
    private final AtomicLong lastFailureTime = new AtomicLong(0);
    
    private final int failureThreshold;
    private final long timeoutMs;
    private final int halfOpenMaxRequests;
    
    public CircuitBreaker(int failureThreshold, long timeoutMs, int halfOpenMaxRequests) {
        this.failureThreshold = failureThreshold;
        this.timeoutMs = timeoutMs;
        this.halfOpenMaxRequests = halfOpenMaxRequests;
    }
    
    public CircuitBreaker() {
        this(5, 60000, 3);
    }
    
    public boolean canExecute() {
        switch (state) {
            case CLOSED:
                return true;
            case OPEN:
                if (System.currentTimeMillis() - lastFailureTime.get() > timeoutMs) {
                    state = State.HALF_OPEN;
                    logger.info("Circuit breaker transitioning to HALF_OPEN");
                    return true;
                }
                return false;
            case HALF_OPEN:
                return failureCount.get() < halfOpenMaxRequests;
            default:
                return false;
        }
    }
    
    public void recordSuccess() {
        if (state == State.HALF_OPEN) {
            state = State.CLOSED;
            failureCount.set(0);
            logger.info("Circuit breaker reset to CLOSED");
        }
    }
    
    public void recordFailure() {
        failureCount.incrementAndGet();
        lastFailureTime.set(System.currentTimeMillis());
        
        if (state == State.CLOSED && failureCount.get() >= failureThreshold) {
            state = State.OPEN;
            logger.warn("Circuit breaker opened after " + failureCount.get() + " failures");
        } else if (state == State.HALF_OPEN) {
            state = State.OPEN;
            logger.warn("Circuit breaker reopened after test failure");
        }
    }
    
    public State getState() {
        return state;
    }
    
    public int getFailureCount() {
        return failureCount.get();
    }
    
    public long getLastFailureTime() {
        return lastFailureTime.get();
    }
    
    public void reset() {
        state = State.CLOSED;
        failureCount.set(0);
        lastFailureTime.set(0);
        logger.info("Circuit breaker manually reset");
    }
} 