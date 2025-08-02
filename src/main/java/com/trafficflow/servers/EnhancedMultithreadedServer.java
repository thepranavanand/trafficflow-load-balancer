package com.trafficflow.servers;

import com.trafficflow.config.ApplicationConfig;
import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Multi-threaded server implementation
 */
public class EnhancedMultithreadedServer {
    private static final int SERVER_PORT = 8003;
    private static final int STATUS_PORT = 19003;
    
    private static final AtomicInteger currentConnections = new AtomicInteger(0);
    private static volatile boolean isRunning = true;
    private static AtomicLong totalResponseTime = new AtomicLong(0);
    private static AtomicInteger requestCount = new AtomicInteger(0);
    private static AtomicInteger totalThreadsCreated = new AtomicInteger(0);
    
    // Thread management to prevent memory leaks
    private static final int MAX_THREADS = ApplicationConfig.getMaxThreads();
    private static final AtomicInteger activeThreads = new AtomicInteger(0);
    
    public static void main(String[] args) {
        System.out.println("Starting Enhanced Multi-Threaded Server...");
        System.out.println("Main server port: " + SERVER_PORT);
        System.out.println("Status server port: " + STATUS_PORT);
        System.out.println("Threading model: New thread per client (unlimited)");
        
        // Start status reporting server
        startStatusServer();
        
        // Start main server
        startMainServer();
    }
    
    private static void startStatusServer() {
        new Thread(() -> {
            try (ServerSocket statusSocket = new ServerSocket(STATUS_PORT)) {
                System.out.println("Status server listening on port " + STATUS_PORT);
                
                while (true) {
                    try (Socket client = statusSocket.accept()) {
                        PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                        
                        // Send current metrics: connections,averageResponseTime
                        long avgResponseTime = requestCount.get() > 0 ? totalResponseTime.get() / requestCount.get() : 0;
                        String response = currentConnections.get() + "," + avgResponseTime;
                        
                        out.println(response);
                        
                        System.out.println("Status requested - Connections: " + currentConnections.get() + 
                                         ", Avg Response: " + avgResponseTime + "ms");
                    } catch (IOException e) {
                        System.err.println("Status server error: " + e.getMessage());
                    }
                }
            } catch (IOException e) {
                System.err.println("Failed to start status server: " + e.getMessage());
            }
        }, "StatusServer").start();
    }
    
    private static void startMainServer() {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            serverSocket.setSoTimeout(0); // No timeout for multithreaded server
            System.out.println("Multi-threaded server listening on port " + SERVER_PORT);
            
            while (isRunning) {
                try {
                    System.out.println("Waiting for client connection...");
                    Socket clientSocket = serverSocket.accept();
                    
                    // Check thread limit before creating new thread
                    if (activeThreads.get() >= MAX_THREADS) {
                        System.err.println("WARNING: Maximum threads reached (" + MAX_THREADS + "). Rejecting connection.");
                        try {
                            clientSocket.close();
                        } catch (IOException e) {
                            System.err.println("Error closing rejected client socket: " + e.getMessage());
                        }
                        continue;
                    }
                    
                    // Create a new thread for each client
                    int threadNumber = totalThreadsCreated.incrementAndGet();
                    activeThreads.incrementAndGet();
                    Thread clientThread = new Thread(() -> {
                        try {
                            handleClient(clientSocket);
                        } finally {
                            activeThreads.decrementAndGet();
                        }
                    }, "ClientHandler-" + threadNumber);
                    clientThread.start();
                    
                    System.out.println("New thread created: " + clientThread.getName() + 
                                     " (Total threads created: " + threadNumber + ")");
                    
                } catch (IOException e) {
                    System.err.println("Error accepting client: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to start main server: " + e.getMessage());
        }
    }
    
    private static void handleClient(Socket clientSocket) {
        long startTime = System.currentTimeMillis();
        currentConnections.incrementAndGet();
        
        try {
            System.out.println("Client connected: " + clientSocket.getRemoteSocketAddress());
            
            // Read client message
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            
            String clientMessage = in.readLine();
            System.out.println("Received: " + clientMessage);
            
            // Simulate processing time
            Thread.sleep(50 + (int)(Math.random() * 100)); // 50-150ms processing
            
            // Send response
            String response = "Hello from Multi-Threaded Server! Your message: " + clientMessage;
            out.println(response);
            
            System.out.println("Sent response to client");
            
        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Client handling interrupted");
        } finally {
            // Update metrics
            long responseTime = System.currentTimeMillis() - startTime;
            totalResponseTime.addAndGet(responseTime);
            requestCount.incrementAndGet();
            
            currentConnections.decrementAndGet();
            
            try {
                clientSocket.close();
                System.out.println("Client disconnected. Response time: " + responseTime + "ms");
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e.getMessage());
            }
        }
    }
}
