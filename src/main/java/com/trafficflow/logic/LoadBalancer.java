package com.trafficflow.logic;

import com.trafficflow.config.ServerConfig;
import com.trafficflow.utils.Logger;
import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoadBalancer {
    private AtomicInteger loadBalancerActiveConnections = new AtomicInteger(0);
    private volatile boolean isRunning = true;
    private volatile long lastConnectionTime = 0;
    private static final Logger logger = Logger.getLogger(LoadBalancer.class);
    private final ConcurrentHashMap<String, CircuitBreaker> circuitBreakers = new ConcurrentHashMap<>();
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    
    public void start() {
        resetCircuitBreakers();
        
        executorService.submit(() -> {
            try (ServerSocket serverSocket = new ServerSocket(ServerConfig.LOAD_BALANCER_PORT)) {
                logger.info("Load Balancer started on port " + ServerConfig.LOAD_BALANCER_PORT);
                
                while (isRunning) {
                    Socket clientSocket = serverSocket.accept();
                    
                    lastConnectionTime = System.currentTimeMillis();
                    
                    int currentConnections = loadBalancerActiveConnections.incrementAndGet();
                    logger.info("Load Balancer: Client connected. Total active: " + currentConnections);
                    
                    String selectedServer = selectBestServer();
                    logger.info("Routing decision: " + currentConnections + " clients → " + selectedServer);
                    logger.info("Current load threshold: LIGHT=" + ServerConfig.LIGHT_LOAD_THRESHOLD + ", MEDIUM=" + ServerConfig.MEDIUM_LOAD_THRESHOLD);
                    routeToServer(clientSocket, selectedServer);
                }
            } catch (IOException e) {
                logger.error("Load Balancer error", e);
            }
        });
    }
    
    private String selectBestServer() {
        int currentLoad = loadBalancerActiveConnections.get();
        
        CircuitBreaker singleThreadBreaker = circuitBreakers.get(ServerConfig.SINGLE_THREAD_SERVER);
        CircuitBreaker threadPoolBreaker = circuitBreakers.get(ServerConfig.THREAD_POOL_SERVER);
        CircuitBreaker multiThreadBreaker = circuitBreakers.get(ServerConfig.MULTITHREADED_SERVER);
        
        String decision;
        
        if (currentLoad <= ServerConfig.LIGHT_LOAD_THRESHOLD) {
            if (singleThreadBreaker == null || singleThreadBreaker.canExecute()) {
                decision = ServerConfig.SINGLE_THREAD_SERVER;
                logger.info("Routing: " + currentLoad + " clients ≤ " + ServerConfig.LIGHT_LOAD_THRESHOLD + " → Single-Thread Server (Efficient)");
            } else if (threadPoolBreaker == null || threadPoolBreaker.canExecute()) {
                decision = ServerConfig.THREAD_POOL_SERVER;
                logger.info("Routing: " + currentLoad + " clients ≤ " + ServerConfig.LIGHT_LOAD_THRESHOLD + " → Thread Pool Server (Fallback)");
            } else {
                decision = ServerConfig.MULTITHREADED_SERVER;
                logger.info("Routing: " + currentLoad + " clients ≤ " + ServerConfig.LIGHT_LOAD_THRESHOLD + " → Multi-Thread Server (Fallback)");
            }
        } else if (currentLoad <= ServerConfig.MEDIUM_LOAD_THRESHOLD) {
            if (threadPoolBreaker == null || threadPoolBreaker.canExecute()) {
                decision = ServerConfig.THREAD_POOL_SERVER;
                logger.info("Routing: " + currentLoad + " clients ≤ " + ServerConfig.MEDIUM_LOAD_THRESHOLD + " → Thread Pool Server (Balanced)");
            } else if (multiThreadBreaker == null || multiThreadBreaker.canExecute()) {
                decision = ServerConfig.MULTITHREADED_SERVER;
                logger.info("Routing: " + currentLoad + " clients ≤ " + ServerConfig.MEDIUM_LOAD_THRESHOLD + " → Multi-Thread Server (Fallback)");
            } else {
                decision = ServerConfig.SINGLE_THREAD_SERVER;
                logger.info("Routing: " + currentLoad + " clients ≤ " + ServerConfig.MEDIUM_LOAD_THRESHOLD + " → Single-Thread Server (Fallback)");
            }
        } else {
            if (multiThreadBreaker == null || multiThreadBreaker.canExecute()) {
                decision = ServerConfig.MULTITHREADED_SERVER;
                logger.info("Routing: " + currentLoad + " clients > " + ServerConfig.MEDIUM_LOAD_THRESHOLD + " → Multi-Thread Server (High Capacity)");
            } else if (threadPoolBreaker == null || threadPoolBreaker.canExecute()) {
                decision = ServerConfig.THREAD_POOL_SERVER;
                logger.info("Routing: " + currentLoad + " clients > " + ServerConfig.MEDIUM_LOAD_THRESHOLD + " → Thread Pool Server (Fallback)");
            } else {
                decision = ServerConfig.SINGLE_THREAD_SERVER;
                logger.info("Routing: " + currentLoad + " clients > " + ServerConfig.MEDIUM_LOAD_THRESHOLD + " → Single-Thread Server (Fallback)");
            }
        }
        
        return decision;
    }
    
    private void routeToServer(Socket clientSocket, String targetServer) {
        executorService.submit(() -> {
            try {
                CircuitBreaker circuitBreaker = circuitBreakers.computeIfAbsent(targetServer, k -> new CircuitBreaker());
                
                if (!circuitBreaker.canExecute()) {
                    logger.warn("Circuit breaker OPEN for " + targetServer + ", rejecting request");
                    closeSocket(clientSocket);
                    return;
                }
                
                String[] parts = targetServer.split(":");
                String host = parts[0];
                int port = Integer.parseInt(parts[1]);
                
                logger.info("Load Balancer: Attempting to connect to " + targetServer);
                Socket serverSocket = new Socket();
                serverSocket.connect(new InetSocketAddress(host, port), ServerConfig.SERVER_CONNECT_TIMEOUT);
                logger.info("Load Balancer: Successfully connected to " + targetServer);
                
                Thread clientToServer = new Thread(() -> proxyData(clientSocket, serverSocket));
                Thread serverToClient = new Thread(() -> proxyData(serverSocket, clientSocket));
                
                clientToServer.start();
                serverToClient.start();
                
                try {
                    clientToServer.join(ServerConfig.CLIENT_TIMEOUT);
                    serverToClient.join(ServerConfig.CLIENT_TIMEOUT);
                    
                    circuitBreaker.recordSuccess();
                    
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                closeSocket(clientSocket);
                closeSocket(serverSocket);
                
                int remaining = loadBalancerActiveConnections.decrementAndGet();
                logger.info("Load Balancer: Client disconnected. Remaining: " + remaining);
                
                if (remaining == 0) {
                    executorService.submit(() -> {
                        try {
                            Thread.sleep(2000);
                            if (loadBalancerActiveConnections.get() == 0) {
                                loadBalancerActiveConnections.set(0);
                                logger.info("Load Balancer: Auto-reset connection counter to 0");
                            }
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    });
                }
                
            } catch (Exception e) {
                logger.error("Routing failed to " + targetServer, e);
                
                CircuitBreaker circuitBreaker = circuitBreakers.get(targetServer);
                if (circuitBreaker != null) {
                    circuitBreaker.recordFailure();
                }
                
                closeSocket(clientSocket);
            }
        });
    }
    
    private void closeSocket(Socket socket) {
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                logger.error("Error closing socket", e);
            }
        }
    }
    
    private void proxyData(Socket from, Socket to) {
        try {
            InputStream input = from.getInputStream();
            OutputStream output = to.getOutputStream();
            
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
                output.flush();
            }
        } catch (IOException e) {
        } finally {
        }
    }
    
    public int getActiveConnections() {
        return loadBalancerActiveConnections.get();
    }
    
    public void resetConnections() {
        loadBalancerActiveConnections.set(0);
        logger.info("Load Balancer: Manually reset connection counter to 0");
    }
    
    public void resetCircuitBreakers() {
        circuitBreakers.values().forEach(CircuitBreaker::reset);
        logger.info("Load Balancer: Reset all circuit breakers");
    }
    
    public void recordServerSuccess(String serverAddress) {
        CircuitBreaker circuitBreaker = circuitBreakers.get(serverAddress);
        if (circuitBreaker != null) {
            circuitBreaker.recordSuccess();
        }
    }
    
    public void recordServerFailure(String serverAddress) {
        CircuitBreaker circuitBreaker = circuitBreakers.get(serverAddress);
        if (circuitBreaker != null) {
            circuitBreaker.recordFailure();
        }
    }
    
    private int getTotalServerConnections() {
        int total = 0;
        try {
            Socket socket1 = new Socket();
            socket1.connect(new InetSocketAddress("localhost", ServerConfig.SINGLE_THREAD_STATUS_PORT), 1000);
            BufferedReader reader1 = new BufferedReader(new InputStreamReader(socket1.getInputStream()));
            String response1 = reader1.readLine();
            socket1.close();
            total += Integer.parseInt(response1.split(",")[0]);
            
            Socket socket2 = new Socket();
            socket2.connect(new InetSocketAddress("localhost", ServerConfig.THREAD_POOL_STATUS_PORT), 1000);
            BufferedReader reader2 = new BufferedReader(new InputStreamReader(socket2.getInputStream()));
            String response2 = reader2.readLine();
            socket2.close();
            total += Integer.parseInt(response2.split(",")[0]);
            
            Socket socket3 = new Socket();
            socket3.connect(new InetSocketAddress("localhost", ServerConfig.MULTITHREADED_STATUS_PORT), 1000);
            BufferedReader reader3 = new BufferedReader(new InputStreamReader(socket3.getInputStream()));
            String response3 = reader3.readLine();
            socket3.close();
            total += Integer.parseInt(response3.split(",")[0]);
            
        } catch (Exception e) {
            logger.error("Failed to get server connections", e);
            total = loadBalancerActiveConnections.get();
        }
        return total;
    }
    
    public void stop() {
        isRunning = false;
        executorService.shutdown();
    }
} 