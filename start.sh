#!/bin/bash

# Function to cleanup on exit
cleanup() {
    echo ""
    echo "Stopping services..."
    if [ -f backend.pid ]; then
        kill $(cat backend.pid) 2>/dev/null
        rm backend.pid
        echo "Backend stopped."
    fi
    if [ -f frontend.pid ]; then
        kill $(cat frontend.pid) 2>/dev/null
        rm frontend.pid
        echo "Frontend stopped."
    fi
    echo "All services stopped. Goodbye!"
    exit 0
}

# Trap SIGINT (Ctrl+C) to cleanup
trap cleanup INT

# Function to wait for service readiness
wait_for_service() {
    local url=$1
    local service_name=$2
    local timeout=60
    local count=0

    echo "⏳ Waiting for $service_name to be ready..."
    until curl -f --max-time 5 "$url" >/dev/null 2>&1; do
        sleep 2
        count=$((count + 2))
        if [ $count -ge $timeout ]; then
            echo "❌ Timeout waiting for $service_name to be ready"
            cleanup
            exit 1
        fi
    done
    echo "✅ $service_name is ready!"
}

echo "=== Security Orchestrator Startup Script ==="
echo ""

# Check if Java is installed
echo "Checking for Java..."
if ! command -v java >/dev/null 2>&1; then
    echo "❌ Error: Java is not installed or not in PATH."
    echo "   Please install Java (JDK 17 or later recommended) and try again."
    exit 1
fi
echo "✅ Java found."

# Check if Flutter is installed
echo "Checking for Flutter..."
if ! command -v flutter >/dev/null 2>&1; then
    echo "❌ Error: Flutter is not installed or not in PATH."
    echo "   Please install Flutter and try again."
    exit 1
fi
echo "✅ Flutter found."
echo ""

# Start backend
echo "🚀 Starting backend (Spring Boot)..."
cd Backend
if [ ! -f "gradlew" ]; then
    echo "❌ Error: gradlew not found in Backend directory."
    cd ..
    exit 1
fi

./gradlew bootRun > ../backend.log 2>&1 &
BACKEND_PID=$!
echo $BACKEND_PID > ../backend.pid
cd ..
echo "✅ Backend started with PID $BACKEND_PID (logs in backend.log)"

# Wait for backend to be ready
wait_for_service "http://localhost:8080/actuator/health" "Backend"

# Start frontend
echo "🚀 Starting frontend (Flutter Web)..."
cd Frontend/security_orchestrator_frontend
if [ ! -f "pubspec.yaml" ]; then
    echo "❌ Error: pubspec.yaml not found. Is this a Flutter project?"
    cd ../..
    exit 1
fi

flutter run -d web-server --web-port=3000 > ../../frontend.log 2>&1 &
FRONTEND_PID=$!
echo $FRONTEND_PID > ../../frontend.pid
cd ../..
echo "✅ Frontend started with PID $FRONTEND_PID (logs in frontend.log)"

# Wait for frontend to be ready
wait_for_service "http://localhost:3000" "Frontend"
echo ""

# Display access information
echo "🎉 Both applications started successfully!"
echo ""
echo "📍 Access URLs:"
echo "   Backend API:  http://localhost:8080"
echo "   Frontend App: http://localhost:3000"
echo ""
echo "📝 Log files:"
echo "   Backend logs:  $(pwd)/backend.log"
echo "   Frontend logs: $(pwd)/frontend.log"
echo ""
echo "🛑 To stop both services, press Ctrl+C"
echo ""

# Wait indefinitely
wait