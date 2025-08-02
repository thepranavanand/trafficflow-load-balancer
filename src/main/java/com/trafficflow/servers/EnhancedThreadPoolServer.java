package com.trafficflow.servers;

import com.trafficflow.config.ApplicationConfig;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class EnhancedThreadPoolServer {
    private static final int SERVER_PORT = 8002;
    private static final int STATUS_PORT = 19002;
    private static final int THREAD_POOL_SIZE = ApplicationConfig.getThreadPoolSize();
    
    private static final AtomicInteger currentConnections = new AtomicInteger(0);
    private static volatile boolean isRunning = true;
    private static AtomicLong totalResponseTime = new AtomicLong(0);
    private static AtomicInteger requestCount = new AtomicInteger(0);
    
    private static ExecutorService threadPool;
    
    public static void main(String[] args) {
        System.out.println("Starting Enhanced Thread Pool Server...");
        System.out.println("Main server port: " + SERVER_PORT);
        System.out.println("Status server port: " + STATUS_PORT);
        System.out.println("Thread pool size: " + THREAD_POOL_SIZE);
        
        threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        
        startStatusServer();
        
        startMainServer();
    }
    
    private static void startStatusServer() {
        new Thread(() -> {
            try (ServerSocket statusSocket = new ServerSocket(STATUS_PORT)) {
                System.out.println("Status server listening on port " + STATUS_PORT);
                
                while (true) {
                    try (Socket client = statusSocket.accept()) {
                        PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                        
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
        }).start();
    }
    
    private static void startMainServer() {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            serverSocket.setSoTimeout(60000);
            System.out.println("Thread pool server listening on port " + SERVER_PORT);
            
            while (isRunning) {
                try {
                    System.out.println("Waiting for client connection...");
                    Socket clientSocket = serverSocket.accept();
                    
                    System.out.println("Thread Pool: Received connection from " + clientSocket.getRemoteSocketAddress());
                    threadPool.submit(() -> handleClient(clientSocket));
                    
                } catch (SocketTimeoutException e) {
                    System.out.println("No connections received, continuing to listen...");
                } catch (IOException e) {
                    System.err.println("Error accepting client: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to start main server: " + e.getMessage());
        } finally {
            threadPool.shutdown();
        }
    }
    
    private static void handleClient(Socket clientSocket) {
        long startTime = System.currentTimeMillis();
        currentConnections.incrementAndGet();
        
        try {
            System.out.println("Client connected: " + clientSocket.getRemoteSocketAddress());
            
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            
            String clientMessage = in.readLine();
            System.out.println("Received: " + clientMessage);
            
            Thread.sleep(50 + (int)(Math.random() * 100));
            
            String response = "Hello from Thread Pool Server! Your message: " + clientMessage;
            out.println(response);
            
            System.out.println("Sent response to client");
            
        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Client handling interrupted");
        } finally {
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
