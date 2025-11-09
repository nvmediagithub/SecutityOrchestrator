package org.example.infrastructure.services.llm;

import org.example.domain.exception.OpenRouterRateLimitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Rate limiter service implementing token bucket algorithm
 * Provides client-side rate limiting to prevent API quota exceeded errors
 */
@Service
public class RateLimiterService {
    
    private static final Logger logger = LoggerFactory.getLogger(RateLimiterService.class);
    
    private final ConcurrentHashMap<String, RateLimitBucket> buckets = new ConcurrentHashMap<>();
    
    /**
     * Acquire token for the specified key
     */
    public boolean acquireToken(String key, double tokens) {
        RateLimitBucket bucket = getOrCreateBucket(key);
        return bucket.tryConsume(tokens);
    }
    
    /**
     * Acquire token with blocking wait
     */
    public boolean acquireTokenBlocking(String key, double tokens, Duration timeout) 
            throws OpenRouterRateLimitException {
        long startTime = System.currentTimeMillis();
        long timeoutMillis = timeout.toMillis();
        
        while (true) {
            if (acquireToken(key, tokens)) {
                return true;
            }
            
            long elapsed = System.currentTimeMillis() - startTime;
            if (elapsed >= timeoutMillis) {
                throw new OpenRouterRateLimitException(
                    String.format("Rate limit exceeded for key '%s' - timeout after %dms", 
                                key, elapsed));
            }
            
            // Wait a bit before retrying
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new OpenRouterRateLimitException(
                    "Rate limit check interrupted: " + e.getMessage());
            }
        }
    }
    
    /**
     * Check current rate limit status
     */
    public RateLimitStatus getRateLimitStatus(String key) {
        RateLimitBucket bucket = buckets.get(key);
        if (bucket == null) {
            return new RateLimitStatus(key, 0, 0, 0, 0, RateLimitState.AVAILABLE);
        }
        return bucket.getStatus();
    }
    
    /**
     * Get all rate limit statuses
     */
    public java.util.Map<String, RateLimitStatus> getAllRateLimitStatuses() {
        java.util.Map<String, RateLimitStatus> statuses = new java.util.HashMap<>();
        buckets.forEach((key, bucket) -> {
            statuses.put(key, bucket.getStatus());
        });
        return statuses;
    }
    
    /**
     * Reset rate limiter for the specified key
     */
    public void resetRateLimiter(String key) {
        buckets.remove(key);
        logger.info("Rate limiter reset for key: {}", key);
    }
    
    /**
     * Create or get rate limit bucket
     */
    private RateLimitBucket getOrCreateBucket(String key) {
        return buckets.computeIfAbsent(key, k -> {
            logger.debug("Creating new rate limit bucket for key: {}", k);
            return new RateLimitBucket(1.0, 10.0, Duration.ofSeconds(60)); // 1 token per second, burst 10
        });
    }
    
    /**
     * Token bucket implementation
     */
    private static class RateLimitBucket {
        private final double capacity;
        private final double refillRate;
        private final Duration refillPeriod;
        private final ReentrantLock lock = new ReentrantLock();
        private final AtomicLong lastRefillTime = new AtomicLong(System.currentTimeMillis());
        private final AtomicInteger availableTokens = new AtomicInteger(0);
        
        public RateLimitBucket(double refillRate, double capacity, Duration refillPeriod) {
            this.refillRate = refillRate;
            this.capacity = capacity;
            this.refillPeriod = refillPeriod;
            this.lastRefillTime.set(System.currentTimeMillis());
            this.availableTokens.set((int) capacity); // Start full
        }
        
        public boolean tryConsume(double tokens) {
            lock.lock();
            try {
                refill();
                
                int currentTokens = availableTokens.get();
                if (currentTokens >= tokens) {
                    availableTokens.addAndGet((int) -tokens);
                    logger.debug("Consumed {} tokens, {} remaining", tokens, availableTokens.get());
                    return true;
                }
                
                logger.debug("Insufficient tokens: requested {}, available {}", tokens, currentTokens);
                return false;
                
            } finally {
                lock.unlock();
            }
        }
        
        private void refill() {
            long now = System.currentTimeMillis();
            long timeSinceRefill = now - lastRefillTime.get();
            long refillPeriodMs = refillPeriod.toMillis();
            
            if (timeSinceRefill >= refillPeriodMs) {
                // Calculate how many periods have passed
                long periodsPassed = timeSinceRefill / refillPeriodMs;
                double tokensToAdd = periodsPassed * refillRate;
                
                int currentTokens = availableTokens.get();
                int newTokens = Math.min((int) (currentTokens + tokensToAdd), (int) capacity);
                
                availableTokens.set(newTokens);
                lastRefillTime.set(now - (timeSinceRefill % refillPeriodMs));
                
                logger.debug("Refilled {} tokens, new total: {}", tokensToAdd, newTokens);
            }
        }
        
        public RateLimitStatus getStatus() {
            lock.lock();
            try {
                refill();
                
                int currentTokens = availableTokens.get();
                double utilizationRate = currentTokens / capacity;
                
                RateLimitState state = utilizationRate > 0.8 ? RateLimitState.LOW :
                                     utilizationRate > 0.5 ? RateLimitState.MEDIUM :
                                     RateLimitState.AVAILABLE;
                
                return new RateLimitStatus(
                    "bucket-" + System.identityHashCode(this),
                    currentTokens,
                    (int) capacity,
                    (int) refillRate,
                    (int) (refillPeriod.toSeconds()),
                    state
                );
            } finally {
                lock.unlock();
            }
        }
    }
    
    /**
     * Rate limit status information
     */
    public static class RateLimitStatus {
        private final String key;
        private final int availableTokens;
        private final int capacity;
        private final int refillRate;
        private final int refillPeriodSeconds;
        private final RateLimitState state;
        
        public RateLimitStatus(String key, int availableTokens, int capacity, 
                             int refillRate, int refillPeriodSeconds, RateLimitState state) {
            this.key = key;
            this.availableTokens = availableTokens;
            this.capacity = capacity;
            this.refillRate = refillRate;
            this.refillPeriodSeconds = refillPeriodSeconds;
            this.state = state;
        }
        
        // Getters
        public String getKey() { return key; }
        public int getAvailableTokens() { return availableTokens; }
        public int getCapacity() { return capacity; }
        public int getRefillRate() { return refillRate; }
        public int getRefillPeriodSeconds() { return refillPeriodSeconds; }
        public RateLimitState getState() { return state; }
        
        public double getUtilizationRate() {
            return (double) availableTokens / capacity;
        }
    }
    
    /**
     * Rate limit states
     */
    public enum RateLimitState {
        AVAILABLE,  // Plenty of tokens available
        MEDIUM,     // Moderate usage
        LOW,        // Low token availability
        EXCEEDED    // Rate limit exceeded
    }
}