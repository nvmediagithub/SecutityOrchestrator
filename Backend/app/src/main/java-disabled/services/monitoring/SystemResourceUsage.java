package org.example.infrastructure.services.monitoring;

/**
 * System resource usage information
 */
public class SystemResourceUsage {
    private final double cpuUsage;
    private final double memoryUsage;
    private final double diskUsage;
    private final long activeConnections;
    private final int threadCount;
    
    public SystemResourceUsage(double cpuUsage, double memoryUsage, double diskUsage,
                              long activeConnections, int threadCount) {
        this.cpuUsage = cpuUsage;
        this.memoryUsage = memoryUsage;
        this.diskUsage = diskUsage;
        this.activeConnections = activeConnections;
        this.threadCount = threadCount;
    }
    
    public double getCpuUsage() {
        return cpuUsage;
    }
    
    public double getMemoryUsage() {
        return memoryUsage;
    }
    
    public double getDiskUsage() {
        return diskUsage;
    }
    
    public long getActiveConnections() {
        return activeConnections;
    }
    
    public int getThreadCount() {
        return threadCount;
    }
    
    @Override
    public String toString() {
        return "SystemResourceUsage{" +
                "cpuUsage=" + cpuUsage +
                "%, memoryUsage=" + memoryUsage +
                "%, diskUsage=" + diskUsage +
                "%, activeConnections=" + activeConnections +
                ", threadCount=" + threadCount +
                '}';
    }
}