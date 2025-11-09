# Ollama Installation Script for Windows
# Optimized for RTX 3070 8GB
# SecurityOrchestrator Integration

Write-Host "🚀 Installing Ollama for SecurityOrchestrator..." -ForegroundColor Green

# Check if Ollama is already installed
if (Get-Command "ollama" -ErrorAction SilentlyContinue) {
    Write-Host "✅ Ollama is already installed!" -ForegroundColor Green
    Write-Host "Version: $(ollama --version)" -ForegroundColor Yellow
    exit 0
}

# Download Ollama installer
Write-Host "📦 Downloading Ollama for Windows..." -ForegroundColor Yellow
$ollamaUrl = "https://ollama.ai/download/ollama-windows-amd64.exe"
$installerPath = "$env:TEMP\ollama-setup.exe"

try {
    Invoke-WebRequest -Uri $ollamaUrl -OutFile $installerPath -UseBasicParsing
    Write-Host "✅ Download completed!" -ForegroundColor Green
    
    # Run installer
    Write-Host "🔧 Running Ollama installer..." -ForegroundColor Yellow
    Start-Process -FilePath $installerPath -Wait -ArgumentList "/S"
    
    # Refresh environment variables
    $env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User")
    
    # Verify installation
    if (Get-Command "ollama" -ErrorAction SilentlyContinue) {
        Write-Host "✅ Ollama installed successfully!" -ForegroundColor Green
        Write-Host "Version: $(ollama --version)" -ForegroundColor Yellow
    } else {
        Write-Host "❌ Installation failed!" -ForegroundColor Red
        exit 1
    }
    
    # Clean up
    Remove-Item $installerPath -Force -ErrorAction SilentlyContinue
    
} catch {
    Write-Host "❌ Download failed: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Clean up
Remove-Item $installerPath -Force -ErrorAction SilentlyContinue

# Configuration for RTX 3070 8GB
Write-Host "🔧 Configuring Ollama for RTX 3070 8GB..." -ForegroundColor Yellow

# Set environment variables for optimal performance
[Environment]::SetEnvironmentVariable("OLLAMA_HOST", "0.0.0.0:11434", "Machine")
[Environment]::SetEnvironmentVariable("OLLAMA_ORIGINS", "http://localhost:3000,http://localhost:8080", "Machine")

# Create ollama config directory
$ollamaDir = "$env:USERPROFILE\.ollama"
if (!(Test-Path $ollamaDir)) {
    New-Item -Path $ollamaDir -ItemType Directory -Force | Out-Null
}

Write-Host "📋 Recommended next steps:" -ForegroundColor Cyan
Write-Host "1. Start Ollama service: ollama serve" -ForegroundColor White
Write-Host "2. Install CodeLlama model: ollama pull codellama:7b-instruct-q4_0" -ForegroundColor White
Write-Host "3. Test installation: ollama run codellama:7b-instruct-q4_0 'Hello!'" -ForegroundColor White
Write-Host "4. SecurityOrchestrator will auto-connect to http://localhost:11434" -ForegroundColor White

Write-Host "🎉 Installation completed!" -ForegroundColor Green