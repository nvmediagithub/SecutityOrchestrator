/**
 * TypeScript/JavaScript Client for BPMN Analysis System
 * Provides comprehensive integration with SecurityOrchestrator's BPMN Analysis System
 */

import { EventEmitter } from 'events';
import WebSocket from 'ws';

export interface BPMNAnalysisClientConfig {
  baseUrl: string;
  apiKey?: string;
  timeout?: number;
  websocketUrl?: string;
  retryAttempts?: number;
  retryDelay?: number;
}

export interface ProcessUploadRequest {
  name: string;
  description?: string;
  file: File | Buffer;
}

export interface AnalysisRequest {
  processId: string;
  analysisType: 'QUICK' | 'STANDARD' | 'COMPREHENSIVE' | 'COMPLIANCE';
  complianceStandards?: string[];
  configuration?: {
    includePerformance?: boolean;
    includeCompliance?: boolean;
    includeThreatModeling?: boolean;
    aiAssisted?: boolean;
    customRules?: string[];
  };
}

export interface AnalysisResult {
  analysisId: string;
  processId: string;
  status: 'PENDING' | 'IN_PROGRESS' | 'COMPLETED' | 'FAILED' | 'CANCELLED';
  analysisType: string;
  startedAt: string;
  completedAt?: string;
  results?: {
    securityScore: number;
    riskLevel: 'CRITICAL' | 'HIGH' | 'MEDIUM' | 'LOW' | 'MINIMAL';
    totalFindings: number;
    findings: SecurityFinding[];
    recommendations: SecurityRecommendation[];
    complianceResults: Record<string, ComplianceResult>;
    metrics: AnalysisMetrics;
  };
  aiInsights?: AIInsights;
  errorMessage?: string;
}

export interface SecurityFinding {
  id: string;
  type: FindingType;
  severity: 'CRITICAL' | 'HIGH' | 'MEDIUM' | 'LOW' | 'INFO';
  title: string;
  description: string;
  location: {
    elementId: string;
    elementType: string;
    xpath?: string;
  };
  impact: {
    likelihood: 'LOW' | 'MEDIUM' | 'HIGH';
    impact: 'LOW' | 'MEDIUM' | 'HIGH';
    riskScore: number;
  };
  recommendation: string;
  evidence: string[];
  compliance?: Record<string, ComplianceResult>;
  aiGenerated: boolean;
  confidence?: number;
}

export interface SecurityRecommendation {
  id: string;
  type: string;
  priority: 'HIGH' | 'MEDIUM' | 'LOW';
  title: string;
  description: string;
  implementation: {
    effort: 'HIGH' | 'MEDIUM' | 'LOW';
    cost: 'HIGH' | 'MEDIUM' | 'LOW';
    timeframe: string;
  };
  compliance?: Record<string, string>;
}

export interface ComplianceResult {
  score: number;
  status: 'COMPLIANT' | 'MOSTLY_COMPLIANT' | 'PARTIALLY_COMPLIANT' | 'NON_COMPLIANT';
  gaps: string[];
  requirements: ComplianceRequirement[];
}

export interface ComplianceRequirement {
  requirement: string;
  status: 'COMPLIANT' | 'NON_COMPLIANT';
  description: string;
}

export interface AIInsights {
  summary: string;
  keyFindings: string[];
  businessContext?: string;
}

export interface AnalysisMetrics {
  elementsAnalyzed: number;
  securityControls: number;
  vulnerabilities: {
    critical: number;
    high: number;
    medium: number;
    low: number;
  };
  aiRecommendations: number;
}

export type FindingType = 
  | 'AUTHENTICATION_GAP'
  | 'AUTHORIZATION_WEAKNESS'
  | 'SENSITIVE_DATA_EXPOSURE'
  | 'INPUT_VALIDATION_MISSING'
  | 'ERROR_HANDLING_INSUFFICIENT'
  | 'COMPLIANCE_VIOLATION'
  | 'SECURITY_CONTROL_MISSING';

export interface AnalysisProgressUpdate {
  type: 'ANALYSIS_PROGRESS' | 'ANALYSIS_COMPLETE' | 'ANALYSIS_ERROR' | 'HEARTBEAT';
  analysisId: string;
  progress?: number;
  currentStep?: string;
  completedSteps?: string[];
  estimatedTimeRemaining?: number;
  status?: string;
  summary?: {
    securityScore?: number;
    totalFindings?: number;
    highSeverityFindings?: number;
  };
  error?: {
    code: string;
    message: string;
    retryable: boolean;
  };
}

export interface WebSocketMessage {
  type: string;
  [key: string]: any;
}

export class BPMNAnalysisClient extends EventEmitter {
  private config: Required<BPMNAnalysisClientConfig>;
  private websocket?: WebSocket;
  private subscribedAnalysisIds: Set<string> = new Set();
  private isConnected = false;
  private reconnectAttempts = 0;
  private maxReconnectAttempts = 5;

  constructor(config: BPMNAnalysisClientConfig) {
    super();
    
    this.config = {
      baseUrl: config.baseUrl.replace(/\/$/, ''), // Remove trailing slash
      apiKey: config.apiKey,
      timeout: config.timeout || 30000,
      websocketUrl: config.websocketUrl || config.baseUrl.replace('http', 'ws') + '/ws/bpmn',
      retryAttempts: config.retryAttempts || 3,
      retryDelay: config.retryDelay || 1000,
      ...config
    };
    
    this.setupWebSocket();
  }

  /**
   * Upload and analyze a BPMN process
   */
  async uploadAndAnalyzeProcess(
    request: ProcessUploadRequest,
    analysisOptions?: Partial<AnalysisRequest>
  ): Promise<AnalysisResult> {
    try {
      this.emit('progress', { step: 'upload', message: 'Uploading BPMN process' });
      
      // Step 1: Upload process
      const processResponse = await this.uploadProcess(request);
      this.emit('progress', { step: 'analysis', message: 'Starting security analysis' });
      
      // Step 2: Start analysis
      const analysisRequest: AnalysisRequest = {
        processId: processResponse.processId,
        analysisType: 'COMPREHENSIVE',
        complianceStandards: ['GDPR', 'PCI-DSS'],
        ...analysisOptions
      };
      
      const analysisResponse = await this.startAnalysis(analysisRequest);
      
      // Step 3: Setup real-time monitoring
      if (this.isWebSocketConnected()) {
        this.subscribeToAnalysis(analysisResponse.analysisId);
      }
      
      // Step 4: Wait for completion
      this.emit('progress', { step: 'waiting', message: 'Waiting for analysis completion' });
      const result = await this.waitForAnalysisCompletion(analysisResponse.analysisId);
      
      this.emit('complete', { result });
      return result;
      
    } catch (error) {
      this.emit('error', { error: error.message || error });
      throw new BPMNAnalysisError('Process analysis failed', error);
    }
  }

  /**
   * Upload BPMN process
   */
  async uploadProcess(request: ProcessUploadRequest): Promise<{ processId: string; name: string; status: string }> {
    const formData = new FormData();
    formData.append('file', request.file);
    formData.append('name', request.name);
    if (request.description) {
      formData.append('description', request.description);
    }

    const response = await this.makeRequest('/processes', {
      method: 'POST',
      body: formData
    });

    return response;
  }

  /**
   * Start security analysis
   */
  async startAnalysis(request: AnalysisRequest): Promise<{ analysisId: string; status: string; estimatedDuration?: number }> {
    const response = await this.makeRequest('/analysis', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(request)
    });

    return response;
  }

  /**
   * Get analysis result
   */
  async getAnalysisResult(analysisId: string): Promise<AnalysisResult> {
    return await this.makeRequest(`/analysis/${analysisId}`);
  }

  /**
   * Get analysis history for a process
   */
  async getAnalysisHistory(processId: string): Promise<{ processId: string; analyses: AnalysisResult[]; trend: any }> {
    return await this.makeRequest(`/processes/${processId}/analysis/history`);
  }

  /**
   * Test AI provider connectivity
   */
  async testAIProvider(provider: string, model: string): Promise<any> {
    const response = await this.makeRequest('/ai/test', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        provider,
        model,
        testPrompt: 'Test BPMN security analysis',
        timeout: 30
      })
    });

    return response;
  }

  /**
   * Generate security test cases
   */
  async generateTestCases(processId: string, options: {
    testTypes: string[];
    coverage: string;
    testFramework: string;
    outputFormat: string;
    includeEdgeCases?: boolean;
    generateMockData?: boolean;
    includeNegativeTests?: boolean;
  }): Promise<any> {
    const response = await this.makeRequest('/test-generation', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        processId,
        ...options
      })
    });

    return response;
  }

  /**
   * Health check
   */
  async healthCheck(): Promise<{ status: string; components: Record<string, string> }> {
    return await this.makeRequest('/health');
  }

  /**
   * Setup WebSocket connection for real-time updates
   */
  private setupWebSocket(): void {
    if (!this.config.websocketUrl) return;

    try {
      this.websocket = new WebSocket(this.config.websocketUrl);

      this.websocket.onopen = () => {
        console.log('BPMN Analysis WebSocket connected');
        this.isConnected = true;
        this.reconnectAttempts = 0;
        this.emit('websocket:connected');
        
        // Re-subscribe to previous analyses
        for (const analysisId of this.subscribedAnalysisIds) {
          this.subscribeToAnalysis(analysisId);
        }
      };

      this.websocket.onmessage = (event) => {
        try {
          const update: AnalysisProgressUpdate = JSON.parse(event.data);
          this.handleWebSocketMessage(update);
        } catch (error) {
          console.error('Failed to parse WebSocket message:', error);
        }
      };

      this.websocket.onerror = (error) => {
        console.error('WebSocket error:', error);
        this.emit('websocket:error', error);
      };

      this.websocket.onclose = (event) => {
        console.log('WebSocket connection closed:', event.code, event.reason);
        this.isConnected = false;
        this.emit('websocket:disconnected');
        
        // Attempt reconnection
        if (this.reconnectAttempts < this.maxReconnectAttempts) {
          this.scheduleReconnect();
        }
      };

    } catch (error) {
      console.error('Failed to setup WebSocket:', error);
    }
  }

  /**
   * Subscribe to analysis updates
   */
  private subscribeToAnalysis(analysisId: string): void {
    if (!this.websocket || this.websocket.readyState !== WebSocket.OPEN) {
      console.warn('WebSocket not connected, cannot subscribe to analysis:', analysisId);
      return;
    }

    this.subscribedAnalysisIds.add(analysisId);
    
    const message: WebSocketMessage = {
      type: 'SUBSCRIBE_ANALYSIS',
      analysisId
    };

    this.websocket.send(JSON.stringify(message));
    console.log('Subscribed to analysis updates:', analysisId);
  }

  /**
   * Handle WebSocket messages
   */
  private handleWebSocketMessage(update: AnalysisProgressUpdate): void {
    this.emit('analysis:update', update);

    switch (update.type) {
      case 'ANALYSIS_PROGRESS':
        this.emit('analysis:progress', {
          analysisId: update.analysisId,
          progress: update.progress,
          currentStep: update.currentStep,
          estimatedTimeRemaining: update.estimatedTimeRemaining
        });
        break;

      case 'ANALYSIS_COMPLETE':
        this.emit('analysis:complete', {
          analysisId: update.analysisId,
          summary: update.summary
        });
        this.subscribedAnalysisIds.delete(update.analysisId);
        break;

      case 'ANALYSIS_ERROR':
        this.emit('analysis:error', {
          analysisId: update.analysisId,
          error: update.error
        });
        this.subscribedAnalysisIds.delete(update.analysisId);
        break;

      case 'HEARTBEAT':
        this.emit('heartbeat', { timestamp: update.analysisId });
        break;
    }
  }

  /**
   * Check if WebSocket is connected
   */
  private isWebSocketConnected(): boolean {
    return this.isConnected && this.websocket?.readyState === WebSocket.OPEN;
  }

  /**
   * Schedule reconnection
   */
  private scheduleReconnect(): void {
    this.reconnectAttempts++;
    const delay = this.config.retryDelay * Math.pow(2, this.reconnectAttempts - 1); // Exponential backoff
    
    console.log(`Reconnecting WebSocket in ${delay}ms (attempt ${this.reconnectAttempts}/${this.maxReconnectAttempts})`);
    
    setTimeout(() => {
      this.setupWebSocket();
    }, delay);
  }

  /**
   * Make HTTP request with retry logic
   */
  private async makeRequest(endpoint: string, options: RequestInit = {}): Promise<any> {
    const url = this.config.baseUrl + endpoint;
    const headers = new Headers(options.headers || {});
    
    if (this.config.apiKey) {
      headers.set('X-API-Key', this.config.apiKey);
    }

    const requestOptions: RequestInit = {
      ...options,
      headers
    };

    for (let attempt = 0; attempt <= this.config.retryAttempts; attempt++) {
      try {
        const controller = new AbortController();
        const timeoutId = setTimeout(() => controller.abort(), this.config.timeout);

        const response = await fetch(url, {
          ...requestOptions,
          signal: controller.signal
        });

        clearTimeout(timeoutId);

        if (!response.ok) {
          const errorData = await response.json().catch(() => ({}));
          throw new HTTPError(response.status, response.statusText, errorData);
        }

        return await response.json();

      } catch (error) {
        if (error instanceof HTTPError && !this.isRetryableError(error)) {
          throw error;
        }

        if (attempt === this.config.retryAttempts) {
          throw new BPMNAnalysisError(`Request failed after ${this.config.retryAttempts + 1} attempts`, error);
        }

        // Wait before retry with exponential backoff
        const delay = this.config.retryDelay * Math.pow(2, attempt);
        await new Promise(resolve => setTimeout(resolve, delay));
      }
    }
  }

  /**
   * Wait for analysis completion
   */
  private async waitForAnalysisCompletion(analysisId: string, maxWaitTimeMs: number = 600000): Promise<AnalysisResult> {
    const startTime = Date.now();
    const pollInterval = 5000; // 5 seconds

    while (Date.now() - startTime < maxWaitTimeMs) {
      try {
        const result = await this.getAnalysisResult(analysisId);

        if (result.status === 'COMPLETED') {
          return result;
        } else if (result.status === 'FAILED') {
          throw new BPMNAnalysisError(`Analysis failed: ${result.errorMessage}`);
        }

        // Continue waiting
        await new Promise(resolve => setTimeout(resolve, pollInterval));

      } catch (error) {
        if (error instanceof BPMNAnalysisError) {
          throw error;
        }
        
        // Log error and continue
        console.warn('Error checking analysis status:', error);
        await new Promise(resolve => setTimeout(resolve, pollInterval));
      }
    }

    throw new BPMNAnalysisError(`Analysis timeout - took longer than ${maxWaitTimeMs / 1000} seconds`);
  }

  /**
   * Check if error is retryable
   */
  private isRetryableError(error: HTTPError): boolean {
    // Retry on server errors and rate limiting
    return error.status >= 500 || error.status === 429;
  }

  /**
   * Disconnect and cleanup
   */
  disconnect(): void {
    if (this.websocket) {
      this.websocket.close();
      this.websocket = undefined;
    }
    this.subscribedAnalysisIds.clear();
  }

  /**
   * Get connection status
   */
  getConnectionStatus(): { http: boolean; websocket: boolean } {
    return {
      http: true, // HTTP client is always available
      websocket: this.isWebSocketConnected()
    };
  }
}

// Custom error classes
export class BPMNAnalysisError extends Error {
  constructor(message: string, public cause?: any) {
    super(message);
    this.name = 'BPMNAnalysisError';
  }
}

export class HTTPError extends Error {
  constructor(
    public status: number,
    public statusText: string,
    public data: any = {}
  ) {
    super(`HTTP ${status}: ${statusText}`);
    this.name = 'HTTPError';
  }
}

// React Hook for easy integration
export function useBPMNAnalysis(client: BPMNAnalysisClient) {
  // This would be a React hook implementation
  // For now, it's a placeholder for the concept
  return {
    analyze: client.uploadAndAnalyzeProcess.bind(client),
    getResult: client.getAnalysisResult.bind(client),
    onProgress: (callback: (update: any) => void) => {
      client.on('analysis:progress', callback);
    },
    onComplete: (callback: (result: AnalysisResult) => void) => {
      client.on('analysis:complete', callback);
    },
    onError: (callback: (error: any) => void) => {
      client.on('error', callback);
    }
  };
}

export default BPMNAnalysisClient;