#!/bin/bash

echo "========================================"
echo "  GITHUB REPOSITORY SETUP"
echo "========================================"
echo

echo "Initializing Git repository..."
git init
if [ $? -ne 0 ]; then
    echo "Git initialization failed!"
    exit 1
fi

echo "Git repository initialized!"
echo

echo "Adding all files to Git..."
git add .
if [ $? -ne 0 ]; then
    echo "Git add failed!"
    exit 1
fi

echo "Files added to Git!"
echo

echo "Creating initial commit..."
git commit -m "Initial commit: Load Balancer Dashboard with real-time monitoring

- Three server implementations (Single-thread, Thread Pool, Multi-thread)
- Intelligent load balancer with traffic-based routing
- Real-time dashboard with WebSocket communication
- Interactive load testing capabilities
- Comprehensive documentation and setup scripts"
if [ $? -ne 0 ]; then
    echo "Git commit failed!"
    exit 1
fi

echo "Initial commit created!"
echo

echo "Repository status:"
git status
echo

echo "Next steps:"
echo "1. Create a new repository on GitHub"
echo "2. Copy the repository URL"
echo "3. Run: git remote add origin YOUR_REPOSITORY_URL"
echo "4. Run: git branch -M main"
echo "5. Run: git push -u origin main"
echo

echo "Tips for GitHub upload:"
echo "- Make sure you have a GitHub account"
echo "- Create a new repository on GitHub (don't initialize with README)"
echo "- Use the repository URL in the commands above"
echo "- Consider adding topics/tags to your repository"
echo

echo "Suggested repository topics:"
echo "- java"
echo "- load-balancer"
echo "- multithreading"
echo "- networking"
echo "- real-time-dashboard"
echo "- websocket"
echo "- performance-monitoring"
echo "- concurrent-programming"
echo

read -p "Press Enter to continue..." 