const express = require('express');
const path = require('path');
const app = express();
const port = 3000;

// Serve static files from the web build directory
app.use(express.static(path.join(__dirname, 'security_orchestrator_frontend/build/web')));

// Serve index.html for all other routes (SPA routing)
app.get('*', (req, res) => {
  res.sendFile(path.join(__dirname, 'security_orchestrator_frontend/build/web/index.html'));
});

app.listen(port, () => {
  console.log(`🚀 SecurityOrchestrator Frontend running at http://localhost:${port}`);
  console.log(`📊 Backend API available at http://localhost:8090`);
  console.log(`🔧 Real-time CodeLlama 7B monitoring active`);
});