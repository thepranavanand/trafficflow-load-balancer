package com.trafficflow.resources;

/**
 * Contains the HTML template for the TrafficFlow dashboard
 */
public class DashboardTemplate {
    
    public static String getDashboardHtml() {
        return """
        <!DOCTYPE html>
        <html>
        <head>
            <title>TrafficFlow Dashboard</title>
            <style>
                * { margin: 0; padding: 0; box-sizing: border-box; }
                body { 
                    font-family: 'Tiempos Text', 'Georgia', serif; 
                    background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
                    color: #ffffff;
                    min-height: 100vh;
                }
                .container { 
                    display: grid;
                    grid-template-columns: 1fr 300px;
                    gap: 20px;
                    max-width: 1400px;
                    margin: 0 auto;
                    padding: 20px;
                    height: calc(100vh - 100px);
                }
                .main-title {
                    text-align: center;
                    margin-bottom: 10px;
                    padding: 20px 0;
                }
                .title-text {
                    font-size: 32px;
                    font-weight: 700;
                    color: #ffffff;
                    letter-spacing: -0.5px;
                    margin-bottom: 8px;
                }
                .subtitle-text {
                    font-size: 20px;
                    color: rgba(255, 255, 255, 0.6);
                    font-weight: 400;
                }
                .main-content {
                    display: flex;
                    flex-direction: column;
                    gap: 15px;
                }
                .metrics-panel {
                    background: rgba(255, 255, 255, 0.05);
                    backdrop-filter: blur(10px);
                    border-radius: 15px;
                    padding: 20px;
                    border: 1px solid rgba(255, 255, 255, 0.1);
                }
                .total-load {
                    font-size: 24px;
                    text-align: center;
                    margin: 15px 0;
                    font-weight: 600;
                }
                .total-load span {
                    background: linear-gradient(45deg, #4CAF50, #45a049);
                    -webkit-background-clip: text;
                    -webkit-text-fill-color: transparent;
                    background-clip: text;
                }
                .routing-indicator {
                    text-align: center;
                    font-size: 14px;
                    padding: 12px;
                    background: rgba(76, 175, 80, 0.1);
                    border-radius: 10px;
                    margin: 10px 0;
                    border: 1px solid rgba(76, 175, 80, 0.3);
                }
                .server-grid {
                    display: grid;
                    grid-template-columns: repeat(3, 1fr);
                    gap: 15px;
                    margin-bottom: 15px;
                }
                .server-card {
                    background: rgba(255, 255, 255, 0.05);
                    backdrop-filter: blur(10px);
                    padding: 18px;
                    border-radius: 15px;
                    text-align: center;
                    border: 1px solid rgba(255, 255, 255, 0.1);
                    transition: all 0.3s ease;
                }
                .server-card.active {
                    border: 2px solid #4CAF50;
                    box-shadow: 0 0 20px rgba(76, 175, 80, 0.3);
                    transform: translateY(-2px);
                }
                .server-title {
                    font-size: 20px;
                    font-weight: 600;
                    margin-bottom: 10px;
                    color: #4CAF50;
                    font-family: 'Tiempos Text', 'Georgia', serif;
                }
                .server-metrics {
                    font-size: 20px;
                    margin: 10px 0;
                    font-weight: 500;
                }
                .connections {
                    color: #2E7D32;
                }
                .response-time {
                    color: #4CAF50;
                }
                .response-time .unit {
                    font-size: 0.7em;
                    opacity: 0.6;
                    font-weight: 400;
                }
                .status {
                    font-weight: 600;
                    padding: 8px 15px;
                    border-radius: 20px;
                    font-size: 14px;
                }
                .healthy {
                    background: rgba(76, 175, 80, 0.2);
                    color: #4CAF50;
                    border: 1px solid rgba(76, 175, 80, 0.3);
                }
                .warning {
                    background: rgba(255, 152, 0, 0.2);
                    color: #FF9800;
                    border: 1px solid rgba(255, 152, 0, 0.3);
                }
                .unhealthy {
                    background: rgba(244, 67, 54, 0.15);
                    color: #f44336;
                    border: 1px solid rgba(244, 67, 54, 0.3);
                    opacity: 0.7;
                }
                .load-buttons {
                    display: flex;
                    gap: 10px;
                    justify-content: center;
                    flex-wrap: wrap;
                    margin: 15px 0;
                }
                .load-btn {
                    padding: 12px 20px;
                    font-size: 14px;
                    border: none;
                    border-radius: 15px;
                    cursor: pointer;
                    transition: all 0.2s ease;
                    font-weight: 500;
                    font-family: 'Tiempos Text', 'Georgia', serif;
                    position: relative;
                    overflow: hidden;
                    letter-spacing: 0.2px;
                }
                .load-btn.light {
                    background: rgba(76, 175, 80, 0.1);
                    color: #4CAF50;
                    border: 1px solid rgba(76, 175, 80, 0.2);
                }
                .load-btn.medium {
                    background: rgba(255, 152, 0, 0.1);
                    color: #FF9800;
                    border: 1px solid rgba(255, 152, 0, 0.2);
                }
                .load-btn.heavy {
                    background: rgba(244, 67, 54, 0.1);
                    color: #f44336;
                    border: 1px solid rgba(244, 67, 54, 0.2);
                }
                .load-btn.error {
                    background: rgba(156, 39, 176, 0.1);
                    color: #9C27B0;
                    border: 1px solid rgba(156, 39, 176, 0.2);
                }
                .load-btn:hover {
                    transform: translateY(-2px);
                    box-shadow: 0 6px 20px rgba(0, 0, 0, 0.3);
                }
                .sidebar-right {
                    display: flex;
                    flex-direction: column;
                    gap: 20px;
                    height: 100%;
                }
                .history-panel {
                    background: rgba(255, 255, 255, 0.05);
                    backdrop-filter: blur(10px);
                    border-radius: 15px;
                    padding: 15px;
                    border: 1px solid rgba(255, 255, 255, 0.1);
                    flex: 0 0 auto;
                    height: 250px;
                }
                .history-title {
                    font-size: 20px;
                    font-weight: 600;
                    margin-bottom: 12px;
                    color: #4CAF50;
                    font-family: 'Tiempos Text', 'Georgia', serif;
                }
                .history-list {
                    max-height: 200px;
                    overflow-y: auto;
                }
                .history-item {
                    background: rgba(255, 255, 255, 0.05);
                    border-radius: 10px;
                    padding: 10px;
                    margin: 6px 0;
                    border: 1px solid rgba(255, 255, 255, 0.1);
                    transition: all 0.3s ease;
                }
                .history-item.running {
                    border-color: #4CAF50;
                    background: rgba(76, 175, 80, 0.1);
                }
                .history-item.completed {
                    border-color: #2196F3;
                    background: rgba(33, 150, 243, 0.1);
                    opacity: 0.8;
                }
                .history-item.failed {
                    border-color: #f44336;
                    background: rgba(244, 67, 54, 0.1);
                }
                .history-item-header {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    margin-bottom: 5px;
                }
                .history-item-title {
                    font-weight: 600;
                    font-size: 16px;
                }
                .history-item-status {
                    font-size: 12px;
                    padding: 4px 8px;
                    border-radius: 12px;
                    font-weight: 500;
                }
                .status-running {
                    background: rgba(76, 175, 80, 0.2);
                    color: #4CAF50;
                }
                .status-completed {
                    background: rgba(33, 150, 243, 0.2);
                    color: #2196F3;
                }
                .status-failed {
                    background: rgba(244, 67, 54, 0.2);
                    color: #f44336;
                }
                .history-item-details {
                    font-size: 14px;
                    opacity: 0.7;
                }
                .log-panel {
                    background: rgba(255, 255, 255, 0.05);
                    backdrop-filter: blur(10px);
                    border-radius: 15px;
                    padding: 15px;
                    border: 1px solid rgba(255, 255, 255, 0.1);
                    flex: 1;
                    max-height: 300px;
                }
                .log {
                    background: rgba(0, 0, 0, 0.3);
                    color: #4CAF50;
                    padding: 12px;
                    border-radius: 10px;
                    font-family: 'SF Mono', 'Monaco', 'Inconsolata', 'Roboto Mono', monospace;
                    height: 240px;
                    overflow-y: auto;
                    font-size: 12px;
                    border: 1px solid rgba(255, 255, 255, 0.1);
                }
                
                /* Custom scrollbar styling */
                .history-list::-webkit-scrollbar,
                .log::-webkit-scrollbar {
                    width: 8px;
                }
                
                .history-list::-webkit-scrollbar-track,
                .log::-webkit-scrollbar-track {
                    background: rgba(255, 255, 255, 0.05);
                    border-radius: 4px;
                }
                
                .history-list::-webkit-scrollbar-thumb,
                .log::-webkit-scrollbar-thumb {
                    background: rgba(76, 175, 80, 0.3);
                    border-radius: 4px;
                    border: 1px solid rgba(76, 175, 80, 0.2);
                }
                
                .history-list::-webkit-scrollbar-thumb:hover,
                .log::-webkit-scrollbar-thumb:hover {
                    background: rgba(76, 175, 80, 0.5);
                }
                
                /* Firefox scrollbar styling */
                .history-list,
                .log {
                    scrollbar-width: thin;
                    scrollbar-color: rgba(76, 175, 80, 0.3) rgba(255, 255, 255, 0.05);
                }
            </style>
        </head>
        <body>
            <div class="main-title">
                <div class="title-text">TrafficFlow</div>
                <div class="subtitle-text">(Intelligent Load Distribution System)</div>
            </div>
            
            <div class="container">
                <div class="main-content">
                    <div class="metrics-panel">
                        <div class="total-load">
                            Total Active Connections: <span id="total-load">0</span>
                        </div>
                        <div class="routing-indicator" id="routing-status">
                            System Ready - Waiting for load...
                        </div>
                        <div style="text-align: center; margin-top: 15px; font-size: 12px; opacity: 0.7; color: rgba(255, 255, 255, 0.8);">
                            Health Monitoring: Warning at 50+ connections, Unhealthy at 100+ connections
                        </div>
                    </div>
                    
                    <div class="server-grid">
                        <div class="server-card" id="single-server">
                            <div class="server-title">Single-Threaded</div>
                            <div class="server-metrics connections"><span id="single-connections">0</span> clients</div>
                            <div class="server-metrics response-time"><span id="single-response">0</span><span class="unit">ms</span></div>
                            <div class="status" id="single-status">Starting...</div>
                        </div>
                        
                        <div class="server-card" id="threadpool-server">
                            <div class="server-title">Thread Pool</div>
                            <div class="server-metrics connections"><span id="threadpool-connections">0</span> clients</div>
                            <div class="server-metrics response-time"><span id="threadpool-response">0</span><span class="unit">ms</span></div>
                            <div class="status" id="threadpool-status">Starting...</div>
                        </div>
                        
                        <div class="server-card" id="multithreaded-server">
                            <div class="server-title">Multi-Threaded</div>
                            <div class="server-metrics connections"><span id="multithreaded-connections">0</span> clients</div>
                            <div class="server-metrics response-time"><span id="multithreaded-response">0</span><span class="unit">ms</span></div>
                            <div class="status" id="multithreaded-status">Starting...</div>
                        </div>
                    </div>
                    
                    <div class="metrics-panel">
                        <h3 style="margin-bottom: 20px; font-size: 18px; font-weight: 700;">Load Testing Controls</h3>
                        <div class="load-buttons">
                            <button class="load-btn light" onclick="startLoadTest(10)">Light Load (10 clients)</button>
                            <button class="load-btn light" onclick="startLoadTest(25)">Mobile App (25 clients)</button>
                            <button class="load-btn medium" onclick="startLoadTest(50)">Website (50 clients)</button>
                            <button class="load-btn medium" onclick="startLoadTest(80)">Business Hours (80 clients)</button>
                            <button class="load-btn heavy" onclick="startLoadTest(150)">Peak Load (150 clients)</button>
                            <button class="load-btn heavy" onclick="startLoadTest(200)">Stress Test (200 clients)</button>
                            <button class="load-btn error" onclick="simulateFailure()">Simulate Failure</button>
                            <button class="load-btn error" onclick="addJitter()">Add Jitter</button>
                            <button class="load-btn error" onclick="resetLoadBalancer()">Reset Load Balancer</button>
                        </div>
                    </div>
                </div>
                
                <div class="sidebar-right">
                    <div class="history-panel">
                        <div class="history-title">Load Test History</div>
                        <div class="history-list" id="history-list">
                        </div>
                    </div>
                    
                    <div class="log-panel">
                        <div class="history-title">System Log</div>
                        <div class="log" id="system-log"></div>
                    </div>
                </div>
            </div>
            
            <script>
                let loadTestHistory = [];
                let nextTestId = 1;
                
                const eventSource = new EventSource('/ws');
                const log = document.getElementById('system-log');
                
                eventSource.onmessage = function(event) {
                    const data = JSON.parse(event.data);
                    updateDashboard(data);
                };
                
                function updateDashboard(data) {
                    document.getElementById('total-load').textContent = data.totalLoad;
                    updateServerCard('single', data.servers.single);
                    updateServerCard('threadpool', data.servers.threadpool);
                    updateServerCard('multithreaded', data.servers.multithreaded);
                    updateRoutingStatus(data.totalLoad);
                    addToLog(`[${new Date().toLocaleTimeString()}] Total: ${data.totalLoad} clients | Routing: ${getRoutingDecision(data.totalLoad)}`);
                    checkLoadTestCompletion(data.totalLoad);
                }
                
                function updateServerCard(serverType, metrics) {
                    document.getElementById(serverType + '-connections').textContent = metrics.connections;
                    document.getElementById(serverType + '-response').textContent = metrics.responseTime;
                    
                    const statusElement = document.getElementById(serverType + '-status');
                    const cardElement = document.getElementById(serverType + '-server');
                    
                    if (serverType === 'single') {
                        if (metrics.healthy) {
                            statusElement.textContent = 'Active';
                            statusElement.className = 'status healthy';
                            cardElement.style.opacity = '1';
                        } else {
                            statusElement.textContent = 'Down';
                            statusElement.className = 'status unhealthy';
                            cardElement.style.opacity = '0.5';
                        }
                    } else if (serverType === 'threadpool') {
                        if (metrics.connections >= 40) {
                            statusElement.textContent = 'High Load';
                            statusElement.className = 'status unhealthy';
                            cardElement.style.opacity = '0.8';
                        } else if (metrics.connections >= 20) {
                            statusElement.textContent = 'Warning';
                            statusElement.className = 'status warning';
                            cardElement.style.opacity = '0.9';
                        } else if (metrics.healthy) {
                            statusElement.textContent = 'Healthy';
                            statusElement.className = 'status healthy';
                            cardElement.style.opacity = '1';
                        } else {
                            statusElement.textContent = 'Down';
                            statusElement.className = 'status unhealthy';
                            cardElement.style.opacity = '0.5';
                        }
                    } else {
                        if (metrics.connections >= 100) {
                            statusElement.textContent = 'High Load';
                            statusElement.className = 'status unhealthy';
                            cardElement.style.opacity = '0.8';
                        } else if (metrics.connections >= 50) {
                            statusElement.textContent = 'Warning';
                            statusElement.className = 'status warning';
                            cardElement.style.opacity = '0.9';
                        } else if (metrics.healthy) {
                            statusElement.textContent = 'Healthy';
                            statusElement.className = 'status healthy';
                            cardElement.style.opacity = '1';
                        } else {
                            statusElement.textContent = 'Down';
                            statusElement.className = 'status unhealthy';
                            cardElement.style.opacity = '0.5';
                        }
                    }
                    
                    cardElement.classList.remove('active');
                    if (metrics.connections > 0) {
                        cardElement.classList.add('active');
                    }
                }
                
                function updateRoutingStatus(totalLoad) {
                    const routingElement = document.getElementById('routing-status');
                    const decision = getRoutingDecision(totalLoad);
                    routingElement.textContent = `Current Routing: ${decision}`;
                }
                
                function getRoutingDecision(load) {
                    if (load <= 10) return "→ Single-Threaded Server (Efficient)";
                    else if (load <= 80) return "→ Thread Pool Server (Balanced)";
                    else return "→ Multi-Threaded Server (High Capacity)";
                }
                
                function startLoadTest(clientCount) {
                    const testId = nextTestId++;
                    const testName = getTestName(clientCount);
                    const startTime = new Date();
                    
                    const historyItem = {
                        id: testId,
                        name: testName,
                        clientCount: clientCount,
                        status: 'running',
                        startTime: startTime,
                        endTime: null
                    };
                    
                    loadTestHistory.unshift(historyItem);
                    updateHistoryDisplay();
                    
                    fetch('/api/load-test', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                        body: 'clients=' + clientCount
                    });
                    
                    addToLog(`Starting load test #${testId}: ${testName} with ${clientCount} clients...`);
                }
                
                function getTestName(clientCount) {
                    if (clientCount <= 10) return 'Light Load';
                    else if (clientCount <= 25) return 'Mobile App';
                    else if (clientCount <= 50) return 'Website';
                    else if (clientCount <= 80) return 'Business Hours';
                    else if (clientCount <= 150) return 'Peak Load';
                    else return 'Stress Test';
                }
                
                function updateHistoryDisplay() {
                    const historyList = document.getElementById('history-list');
                    historyList.innerHTML = '';
                    
                    loadTestHistory.slice(0, 10).forEach(item => {
                        const historyItem = document.createElement('div');
                        historyItem.className = `history-item ${item.status}`;
                        historyItem.id = `history-item-${item.id}`;
                        
                        const statusClass = `status-${item.status}`;
                        const statusText = item.status === 'running' ? 'Running' : 
                                         item.status === 'completed' ? 'Completed' : 'Failed';
                        
                        historyItem.innerHTML = `
                            <div class="history-item-header">
                                <div class="history-item-title">${item.name}</div>
                                <div class="history-item-status ${statusClass}">${statusText}</div>
                            </div>
                            <div class="history-item-details">
                                ${item.clientCount} clients • Started ${formatTime(item.startTime)}
                                ${item.endTime ? '• Completed ' + formatTime(item.endTime) : ''}
                            </div>
                        `;
                        
                        historyList.appendChild(historyItem);
                    });
                }
                
                function formatTime(date) {
                    return date.toLocaleTimeString();
                }
                
                function checkLoadTestCompletion(currentLoad) {
                    if (currentLoad === 0) {
                        loadTestHistory.forEach(item => {
                            if (item.status === 'running') {
                                item.status = 'completed';
                                item.endTime = new Date();
                            }
                        });
                        updateHistoryDisplay();
                    }
                }
                
                function addToLog(message) {
                    const log = document.getElementById('system-log');
                    log.innerHTML += message + '\\n';
                    log.scrollTop = log.scrollHeight;
                    
                    const lines = log.innerHTML.split('\\n');
                    if (lines.length > 50) {
                        log.innerHTML = lines.slice(-50).join('\\n');
                    }
                }
                
                addToLog('Load Balancer Dashboard initialized');
                addToLog('Real-time monitoring active');
                
                function simulateFailure() {
                    addToLog('Simulating server failure...');
                    const servers = ['single', 'threadpool', 'multithreaded'];
                    const randomServer = servers[Math.floor(Math.random() * servers.length)];
                    
                    const statusElement = document.getElementById(randomServer + '-status');
                    statusElement.textContent = 'Failed';
                    statusElement.className = 'status unhealthy';
                    
                    setTimeout(() => {
                        statusElement.textContent = 'Recovered';
                        statusElement.className = 'status healthy';
                        addToLog(`Server ${randomServer} recovered after failure simulation`);
                    }, 3000);
                }
                
                function addJitter() {
                    addToLog('Adding network jitter...');
                    const servers = ['single', 'threadpool', 'multithreaded'];
                    servers.forEach(server => {
                        const responseElement = document.getElementById(server + '-response');
                        const currentTime = parseInt(responseElement.textContent) || 0;
                        const jitter = Math.floor(Math.random() * 500) + 200;
                        responseElement.textContent = currentTime + jitter;
                    });
                    
                    setTimeout(() => {
                        servers.forEach(server => {
                            const responseElement = document.getElementById(server + '-response');
                            const currentTime = parseInt(responseElement.textContent) || 0;
                            responseElement.textContent = Math.max(0, currentTime - 300);
                        });
                        addToLog('Network jitter resolved');
                    }, 5000);
                }
                
                function resetLoadBalancer() {
                    addToLog('Resetting load balancer connection counter...');
                    fetch('/api/reset', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' }
                    }).then(() => {
                        addToLog('Load balancer reset completed');
                    }).catch(() => {
                        addToLog('Reset failed - please try again');
                    });
                }
            </script>
        </body>
        </html>
        """;
    }
}
