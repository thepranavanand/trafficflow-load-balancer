# Load Balancer Dashboard

A basic demonstration of different server architectures with simple load balancing.

## What This Project Does

This application showcases **three different server threading models** and demonstrates how a **basic load balancer** can route traffic between them based on current load conditions:

### Server Types
1. **Single-Threaded Server** (Port 8001) - Handles one client at a time (blocking)
2. **Thread Pool Server** (Port 8002) - Uses fixed pool of 10 threads for efficiency  
3. **Multi-Threaded Server** (Port 8003) - Creates unlimited threads (one per client)

### Basic Load Balancer (Port 9000)
- **Low traffic (≤5 clients)** → Routes to Single-Threaded (most efficient)
- **Medium traffic (≤30 clients)** → Routes to Thread Pool (balanced)  
- **High traffic (>30 clients)** → Routes to Multi-Threaded (unlimited capacity)

### Simple Dashboard (Port 8080)
- **Visual server status** with live connection counts
- **Interactive load testing** with preset options (10, 25, 50, 80, 150, 200 clients)
- **Basic metrics** showing response times and health status
- **Routing indicators** showing which server is being used

## Technical Implementation

### Three-Tier Server Architecture
- **Single-threaded server**: Sequential request processing with blocking I/O
- **Thread pool server**: Fixed-size thread pool (10 threads) with connection reuse
- **Multi-threaded server**: Dynamic thread creation (one per client) for unlimited capacity

### Load Balancer Routing Logic
- **6 preset load scenarios**: 10, 25, 50, 80, 150, 200 clients
- **Connection thresholds**: Light load (≤5 clients), Medium load (≤30 clients), Heavy load (>30 clients)
- **Basic request distribution**: Load-based routing with circuit breaker integration
- **Timeout handling**: 5-second connection timeout with basic error handling

### Circuit Breaker Design
- **Three states**: CLOSED (normal), OPEN (failing), HALF_OPEN (testing recovery)
- **Failure counting**: 5-failure threshold before circuit opens
- **Recovery mechanism**: 60-second timeout for automatic recovery
- **Integration**: Prevents routing to failing servers during OPEN state

### Web Dashboard with SSE
- **Server-Sent Events (SSE)**: Real-time updates without polling
- **Server metrics**: Live connection counts and response times
- **Health status**: Basic failover detection and display
- **Basic HTML/CSS/JavaScript**: Simple, responsive frontend
- **HTTP server integration**: Basic API endpoints for dashboard data

## Quick Start

### GitHub Setup
```bash
# Windows
setup_git.bat

# Linux/Mac
chmod +x setup_git.sh
./setup_git.sh
```

### Windows (Recommended)
```bash
# Run the automated demo
start_demo.bat
```

### Manual Start
```bash
cd src

# Compile all files
javac *.java
javac main/java/com/trafficflow/*.java
javac main/java/com/trafficflow/*/*.java

# Start servers (each in separate terminal)
java EnhancedSingleThreadServer     # Terminal 1
java EnhancedThreadPoolServer       # Terminal 2  
java EnhancedMultithreadedServer    # Terminal 3
java com.trafficflow.TrafficFlowApplication  # Terminal 4

# Open dashboard
http://localhost:8080
```

## Testing the System

### Dashboard Interface
1. Open **http://localhost:8080** in your browser
2. Click load testing buttons:
   - **Light Load (10 clients)** → See Single-Thread server activate
   - **Business Hours (50 clients)** → Watch routing switch to Thread Pool
   - **Stress Test (200 clients)** → Observe Multi-Thread server handling overflow

### Command Line Testing
```bash
# Light load test
java ClientLoadGenerator light 9000

# Medium load test  
java ClientLoadGenerator medium 9000

# Heavy load test
java ClientLoadGenerator heavy 9000

# Continuous load (10 connections/sec for 30 seconds)
java ClientLoadGenerator continuous 9000 10 30

# Find maximum capacity
java ClientLoadGenerator capacity 9000
```

## What You'll See

### Real-time Metrics
- **Connection counts** for each server type
- **Response times** showing performance differences
- **Health status** with basic failover detection
- **Routing decisions** based on current load

### Performance Comparison
- **Single-Thread**: ~50-150ms response time, efficient for low load
- **Thread Pool**: ~30-90ms response time, balanced for medium load
- **Multi-Thread**: ~20-60ms response time, highest throughput for peak load

### Load Balancer Logic
```
Current Load: 47 clients
├── Single-Thread: Available (low load)
├── Thread Pool: 15 clients (optimal load)  
└── Multi-Thread: 32 clients (handling overflow)

Routing Decision: Medium Load → Using Thread Pool + Multi-Thread
```

## Architecture

```
                    LOAD BALANCER (Port 9000)
                           |
        ┌─────────────────┼─────────────────┐
        |                 |                 |
   Single-Thread     Thread Pool      Multi-Thread
   (Port 8001)       (Port 8002)      (Port 8003)
   Status: 19001     Status: 19002    Status: 19003
```

### Data Flow
1. **Client connects** to Load Balancer (Port 9000)
2. **Load Balancer checks** current traffic from all servers
3. **Basic routing** decides best server based on load
4. **Traffic forwarded** to selected server
5. **Real-time updates** sent to dashboard via SSE

## Key Features

### Basic Routing
- **Load-based decisions** using connection counts
- **Health monitoring** with circuit breaker integration
- **Resource optimization** to prevent server overload

### Visual Dashboard  
- **Real-time counters** and connection displays
- **Interactive testing** with click-to-load buttons
- **System logs** showing routing decisions
- **Performance comparisons** across server types

### Load Testing Suite
- **Preset scenarios** (Light Load, Mobile App, Website, Business Hours, Peak Load, Stress Test)
- **Basic patterns** simulating different traffic loads
- **Capacity testing** to find system limits
- **Simple monitoring** during tests

## Educational Value

This project demonstrates:
- **Concurrent programming** concepts (threading models)
- **Load balancing** algorithms and strategies
- **System monitoring** and observability  
- **Performance optimization** under different loads
- **Distributed systems** architecture patterns
- **Real-time web interfaces** with SSE communication

## Resume Impact

**Before:** "Built multithreaded server comparison"

**After:** "Implemented basic load balancer with real-time dashboard, demonstrating three-tier server architecture (single-threaded, thread-pool, multi-threaded) with circuit breaker pattern for fault tolerance and SSE-based real-time monitoring."

## Technical Stack
- **Java**: Server implementations, concurrent programming
- **SSE/HTTP**: Real-time dashboard communication
- **HTML/CSS/JavaScript**: Interactive web interface
- **Networking**: Socket programming, load balancing
- **Basic Monitoring**: Resource tracking, metrics collection

## Code Quality

### Learning Project Standards
- **Clean, commented code** for educational purposes
- **Proper package structure** with organized modules
- **Basic logging** with console output
- **Error handling** with try-catch blocks
- **Resource management** with proper cleanup

### Architecture Highlights
- **Circuit Breaker Pattern** for basic fault tolerance
- **Health Monitoring** with simple failover
- **Real-time Metrics** collection and visualization
- **Load-based Routing** with simple decision making
- **Thread Pool Management** for resource usage

## Repository Structure
```
├── src/                          # Main source code
│   ├── *.java                    # Server implementations
│   └── main/java/com/trafficflow/ # Package structure
│       ├── logic/                # Core load balancing logic
│       ├── servers/              # Server implementations
│       ├── utils/                # Utility classes
│       ├── config/               # Configuration management
│       └── ui/                   # User interface components
├── .github/                      # GitHub configuration
│   ├── workflows/                # CI/CD pipelines
│   └── ISSUE_TEMPLATE/          # Issue templates
├── scripts/                      # Setup and utility scripts
├── .gitignore                   # Git ignore rules
├── LICENSE                      # MIT License
├── CONTRIBUTING.md              # Contribution guidelines
├── CODE_OF_CONDUCT.md           # Community guidelines
├── SECURITY.md                  # Security policy
└── README.md                    # This file
```

## Next Steps
- Add unit tests for better code quality
- Implement proper logging framework
- Add database integration for metrics persistence
- Implement REST API endpoints  
- Add Docker containerization
- Add authentication and security features

---

**Ready to demonstrate your distributed systems knowledge!**