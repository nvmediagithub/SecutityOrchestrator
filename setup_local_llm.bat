# Local LLM Setup Script for SecurityOrchestrator
# Optimized for RTX 3070 8GB

echo "🚀 Setting up Local LLM for SecurityOrchestrator..."

# Option 1: Install Ollama (Recommended for RTX 3070 8GB)
echo "📦 Installing Ollama..."
wget https://ollama.ai/download/ollama-windows-amd64.exe -O ollama-setup.exe
start ollama-setup.exe

# Option 2: Alternative LLM Server (Text Generation WebUI)
echo "🛠️  Alternative: Text Generation WebUI setup..."
# pip install torch torchvision torchaudio --index-url https://download.pytorch.org/whl/cu118
# pip install text-generation-webui

echo "📋 Recommended Models for RTX 3070 8GB:"
echo "   - mistral:7b (6GB VRAM)"
echo "   - codellama:7b (6GB VRAM)"  
echo "   - llama2:7b (6GB VRAM)"
echo "   - phi3:mini (2.3GB VRAM)"

echo "🔧 To start Ollama after installation:"
echo "   1. Open terminal/command prompt"
echo "   2. Run: ollama serve"
echo "   3. In another terminal: ollama pull mistral:7b"
echo "   4. Test: ollama run mistral:7b 'Hello, how are you?'"

echo "⚡ Performance Tips for RTX 3070:"
echo "   - Use quantized models (4-bit or 8-bit)"
echo "   - Set max context length to 2048-4096"
echo "   - Use temperature 0.7-0.8 for creativity"
echo "   - Monitor VRAM usage with nvidia-smi"

echo "✅ SecurityOrchestrator will auto-connect to:"
echo "   - Ollama: http://localhost:11434"
echo "   - Custom ONNX: http://localhost:8000"

echo "🎯 To test the setup:"
echo "   1. Start SecurityOrchestrator"
echo "   2. Check LLM Dashboard in Flutter"
echo "   3. Test local model generation"