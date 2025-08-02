package com.trafficflow.utils;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientLoadGenerator {
    private static final AtomicInteger successfulConnections = new AtomicInteger(0);
    private static final AtomicInteger failedConnections = new AtomicInteger(0);
    private ExecutorService executor = Executors.newCachedThreadPool();
    
    public void generateLoad(int clientCount, int serverPort) {
        System.out.println("Starting load test with " + clientCount + " clients...");
        
        long startTime = System.currentTimeMillis();
        successfulConnections.set(0);
        failedConnections.set(0);
        
        CountDownLatch latch = new CountDownLatch(clientCount);
        
        for (int i = 0; i < clientCount; i++) {
            final int clientId = i + 1;
            
            executor.submit(() -> {
                try {
                    simulateClient(clientId, serverPort);
                    successfulConnections.incrementAndGet();
                } catch (Exception e) {
                    failedConnections.incrementAndGet();
                    System.err.println("Client " + clientId + " failed: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
            
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        try {
            boolean completed = latch.await(30, TimeUnit.SECONDS);
            long duration = System.currentTimeMillis() - startTime;
            
            printLoadTestResults(clientCount, duration, completed);
            
        } catch (InterruptedException e) {
            System.err.println("Load test interrupted!");
            Thread.currentThread().interrupt();
        }
    }
    
    private void simulateClient(int clientId, int serverPort) throws IOException {
        try (Socket socket = new Socket("localhost", serverPort)) {
            
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            out.println("Hello from Client " + clientId + " - " + socket.getLocalSocketAddress());
            
            String response = in.readLine();
            
            Thread.sleep(100 + new Random().nextInt(200));
            
            System.out.println("Client " + clientId + " received: " + response);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Client interrupted", e);
        }
    }
    
    private void printLoadTestResults(int totalClients, long durationMs, boolean completed) {
        int successful = successfulConnections.get();
        int failed = failedConnections.get();
        
        System.out.println("Load test completed:");
        System.out.println("  Total clients: " + totalClients);
        System.out.println("  Successful: " + successful);
        System.out.println("  Failed: " + failed);
        System.out.println("  Duration: " + durationMs + "ms");
        System.out.println("  Completed: " + (completed ? "Yes" : "No (timeout)"));
        
        if (successful > 0) {
            double avgResponseTime = (double) durationMs / successful;
            System.out.println("  Average response time: " + String.format("%.2f", avgResponseTime) + "ms");
        }
    }
}
