package com.trafficflow.models;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Represents a WebSocket connection for real-time dashboard updates
 */
public class WebSocketConnection {
    private OutputStream output;
    
    public WebSocketConnection(OutputStream output) {
        this.output = output;
    }
    
    public void send(String message) throws IOException {
        output.write(("data: " + message + "\n\n").getBytes());
        output.flush();
    }
} 