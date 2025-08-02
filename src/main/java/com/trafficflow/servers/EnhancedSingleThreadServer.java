package com.trafficflow.servers;

import com.trafficflow.utils.Logger;
import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Single-threaded server implementation
 */
public class EnhancedSingleThreadServer {
    private static final int SERVER_PORT = 8001;
    private static final int STATUS_PORT = 19001;
    private static final AtomicInteger currentConnections = new AtomicInteger(0);
    private static volatile boolean isRunning = true;
    private static long totalResponseTime = 0;
    private static int requestCount = 0;
    private static final Logger logger = Logger.getLogger(EnhancedSingleThreadServer.class);
    
    public static void main(String[] args) {
        logger.info("Starting Enhanced Single-Threaded Server...");
        logger.info("Main server port: " + SERVER_PORT);
        logger.info("Status server port: " + STATUS_PORT);
        
        // Start status reporting server
        startStatusServer();
        
        // Start main server
        startMainServer();
    }
    
    private static void startStatusServer() {
        new Thread(() -> {
            try (ServerSocket statusSocket = new ServerSocket(STATUS_PORT)) {
                logger.info("Status server listening on port " + STATUS_PORT);
                
                while (true) {
                    try (Socket client = statusSocket.accept()) {
                        PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                        
                        // Send current metrics: connections,averageResponseTime
                        long avgResponseTime = requestCount > 0 ? totalResponseTime / requestCount : 0;
                        String response = currentConnections.get() + "," + avgResponseTime;
                        
                        out.println(response);
                        
                        logger.info("Status requested - Connections: " + currentConnections.get() + 
                                         ", Avg Response: " + avgResponseTime + "ms");
                    } catch (IOException e) {
                        logger.error("Status server error", e);
                    }
                }
            } catch (IOException e) {
                logger.error("Failed to start status server", e);
            }
        }).start();
    }
    
    private static void startMainServer() {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            serverSocket.setSoTimeout(60000); // 1 minute timeout
            logger.info("Single-threaded server listening on port " + SERVER_PORT);
            
            while (isRunning) {
                try {
                    logger.debug("Waiting for client connection...");
                    Socket clientSocket = serverSocket.accept();
                    
                    // Handle client (blocking - one at a time)
                    handleClient(clientSocket);
                    
                } catch (SocketTimeoutException e) {
                    logger.debug("No connections received, continuing to listen...");
                } catch (IOException e) {
                    logger.error("Error accepting client", e);
                }
            }
                    } catch (IOException e) {
                logger.error("Failed to start main server", e);
            }
    }
    
    private static void handleClient(Socket clientSocket) {
        long startTime = System.currentTimeMillis();
        currentConnections.incrementAndGet();
        
        try {
            logger.info("Client connected: " + clientSocket.getRemoteSocketAddress());
            
            // Read client message
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            
            String clientMessage = in.readLine();
            logger.debug("Received: " + clientMessage);
            
            // Simulate processing time
            Thread.sleep(50 + (int)(Math.random() * 100)); // 50-150ms processing
            
            // Send response
            String response = "Hello from Single-Threaded Server! Your message: " + clientMessage;
            out.println(response);
            
            logger.debug("Sent response to client");
            
        } catch (IOException e) {
            logger.error("Error handling client", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Client handling interrupted", e);
        } finally {
            // Update metrics
            long responseTime = System.currentTimeMillis() - startTime;
            synchronized (EnhancedSingleThreadServer.class) {
                totalResponseTime += responseTime;
                requestCount++;
            }
            
            currentConnections.decrementAndGet();
            
            try {
                clientSocket.close();
                logger.info("Client disconnected. Response time: " + responseTime + "ms");
            } catch (IOException e) {
                logger.error("Error closing client socket", e);
            }
        }
    }
}
