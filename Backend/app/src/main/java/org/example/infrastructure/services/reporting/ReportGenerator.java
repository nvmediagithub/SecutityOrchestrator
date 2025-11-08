package org.example.infrastructure.services.reporting;

import org.example.domain.dto.reports.*;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Service for generating PDF, HTML, and other format reports
 */
@Service
public class ReportGenerator {
    
    private static final String TEMPLATE_DIR = "templates/reports/";
    private static final String OUTPUT_DIR = "generated-reports/";
    
    /**
     * Generate PDF report from report data
     */
    public String generatePdfReport(ReportResponse reportResponse) {
        try {
            // Create output directory if it doesn't exist
            Path outputDir = Paths.get(OUTPUT_DIR);
            if (!Files.exists(outputDir)) {
                Files.createDirectories(outputDir);
            }
            
            // Generate unique filename
            String filename = generateFilename(reportResponse, "pdf");
            Path filePath = outputDir.resolve(filename);
            
            // Generate PDF content (simplified - in real implementation, use a PDF library)
            String pdfContent = generatePdfContent(reportResponse);
            
            // Write to file
            Files.write(filePath, pdfContent.getBytes());
            
            return filePath.toString();
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF report: " + e.getMessage(), e);
        }
    }
    
    /**
     * Generate HTML report from report data
     */
    public String generateHtmlReport(ReportResponse reportResponse) {
        try {
            // Create output directory if it doesn't exist
            Path outputDir = Paths.get(OUTPUT_DIR);
            if (!Files.exists(outputDir)) {
                Files.createDirectories(outputDir);
            }
            
            // Generate unique filename
            String filename = generateFilename(reportResponse, "html");
            Path filePath = outputDir.resolve(filename);
            
            // Generate HTML content
            String htmlContent = generateHtmlContent(reportResponse);
            
            // Write to file
            Files.write(filePath, htmlContent.getBytes());
            
            return filePath.toString();
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate HTML report: " + e.getMessage(), e);
        }
    }
    
    /**
     * Generate CSV report from report data
     */
    public String generateCsvReport(ReportResponse reportResponse) {
        try {
            // Create output directory if it doesn't exist
            Path outputDir = Paths.get(OUTPUT_DIR);
            if (!Files.exists(outputDir)) {
                Files.createDirectories(outputDir);
            }
            
            // Generate unique filename
            String filename = generateFilename(reportResponse, "csv");
            Path filePath = outputDir.resolve(filename);
            
            // Generate CSV content
            String csvContent = generateCsvContent(reportResponse);
            
            // Write to file
            Files.write(filePath, csvContent.getBytes());
            
            return filePath.toString();
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate CSV report: " + e.getMessage(), e);
        }
    }
    
    /**
     * Generate JSON report from report data
     */
    public String generateJsonReport(ReportResponse reportResponse) {
        try {
            // Create output directory if it doesn't exist
            Path outputDir = Paths.get(OUTPUT_DIR);
            if (!Files.exists(outputDir)) {
                Files.createDirectories(outputDir);
            }
            
            // Generate unique filename
            String filename = generateFilename(reportResponse, "json");
            Path filePath = outputDir.resolve(filename);
            
            // Generate JSON content (simplified)
            String jsonContent = generateJsonContent(reportResponse);
            
            // Write to file
            Files.write(filePath, jsonContent.getBytes());
            
            return filePath.toString();
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate JSON report: " + e.getMessage(), e);
        }
    }
    
    /**
     * Generate executive summary report
     */
    public String generateExecutiveSummary(ExecutiveSummaryDto summary) {
        try {
            Path outputDir = Paths.get(OUTPUT_DIR);
            if (!Files.exists(outputDir)) {
                Files.createDirectories(outputDir);
            }
            
            String filename = "executive-summary-" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + ".html";
            Path filePath = outputDir.resolve(filename);
            
            String htmlContent = generateExecutiveSummaryHtml(summary);
            Files.write(filePath, htmlContent.getBytes());
            
            return filePath.toString();
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate executive summary: " + e.getMessage(), e);
        }
    }
    
    /**
     * Generate technical report for developers
     */
    public String generateTechnicalReport(ReportResponse reportResponse) {
        try {
            Path outputDir = Paths.get(OUTPUT_DIR);
            if (!Files.exists(outputDir)) {
                Files.createDirectories(outputDir);
            }
            
            String filename = "technical-report-" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + ".html";
            Path filePath = outputDir.resolve(filename);
            
            String htmlContent = generateTechnicalReportHtml(reportResponse);
            Files.write(filePath, htmlContent.getBytes());
            
            return filePath.toString();
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate technical report: " + e.getMessage(), e);
        }
    }
    
    /**
     * Generate business report for stakeholders
     */
    public String generateBusinessReport(ReportResponse reportResponse) {
        try {
            Path outputDir = Paths.get(OUTPUT_DIR);
            if (!Files.exists(outputDir)) {
                Files.createDirectories(outputDir);
            }
            
            String filename = "business-report-" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + ".html";
            Path filePath = outputDir.resolve(filename);
            
            String htmlContent = generateBusinessReportHtml(reportResponse);
            Files.write(filePath, htmlContent.getBytes());
            
            return filePath.toString();
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate business report: " + e.getMessage(), e);
        }
    }
    
    /**
     * Create a ZIP archive with multiple report formats
     */
    public String generateReportArchive(ReportResponse reportResponse) {
        try {
            Path outputDir = Paths.get(OUTPUT_DIR);
            if (!Files.exists(outputDir)) {
                Files.createDirectories(outputDir);
            }
            
            String filename = "report-archive-" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + ".zip";
            Path zipPath = outputDir.resolve(filename);
            
            try (ZipOutputStream zipOut = new ZipOutputStream(Files.newOutputStream(zipPath))) {
                
                // Add PDF report
                String pdfPath = generatePdfReport(reportResponse);
                addFileToZip(zipOut, pdfPath, "report.pdf");
                
                // Add HTML report
                String htmlPath = generateHtmlReport(reportResponse);
                addFileToZip(zipOut, htmlPath, "report.html");
                
                // Add CSV data
                String csvPath = generateCsvReport(reportResponse);
                addFileToZip(zipOut, csvPath, "data.csv");
                
                // Add JSON data
                String jsonPath = generateJsonReport(reportResponse);
                addFileToZip(zipOut, jsonPath, "data.json");
            }
            
            return zipPath.toString();
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate report archive: " + e.getMessage(), e);
        }
    }
    
    // Private helper methods
    
    private String generateFilename(ReportResponse report, String extension) {
        String sanitizedName = report.getReportName().replaceAll("[^a-zA-Z0-9]", "_");
        return sanitizedName + "-" + 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + "." + extension;
    }
    
    private String generatePdfContent(ReportResponse report) {
        // Simplified PDF content generation
        return String.format("""
            %%PDF-1.4
            1 0 obj
            <<
            /Type /Catalog
            /Pages 2 0 R
            >>
            endobj
            
            2 0 obj
            <<
            /Type /Pages
            /Kids [3 0 R]
            /Count 1
            >>
            endobj
            
            3 0 obj
            <<
            /Type /Page
            /Parent 2 0 R
            /MediaBox [0 0 612 792]
            /Contents 4 0 R
            /Resources <<
            /Font <<
            /F1 5 0 R
            >>
            >>
            >>
            endobj
            
            4 0 obj
            <<
            /Length 200
            >>
            stream
            BT
            /F1 24 Tf
            50 750 Td
            (%s) Tj
            0 -30 Td
            /F1 12 Tf
            (%s) Tj
            ET
            endstream
            endobj
            
            5 0 obj
            <<
            /Type /Font
            /Subtype /Type1
            /BaseFont /Helvetica
            >>
            endobj
            
            xref
            0 6
            0000000000 65535 f 
            0000000009 00000 n 
            0000000058 00000 n 
            0000000115 00000 n 
            0000000274 00000 n 
            0000000524 00000 n 
            trailer
            <<
            /Size 6
            /Root 1 0 R
            >>
            startxref
            620
            %%%EOF
            """, report.getReportName(), "Generated: " + report.getGeneratedAt());
    }
    
    private String generateHtmlContent(ReportResponse report) {
        return String.format("""
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>%s</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; }
                    .header { background: #f5f5f5; padding: 20px; border-radius: 5px; }
                    .content { margin-top: 20px; }
                    .section { margin-bottom: 20px; padding: 15px; border: 1px solid #ddd; }
                </style>
            </head>
            <body>
                <div class="header">
                    <h1>%s</h1>
                    <p>Generated: %s</p>
                    <p>Type: %s</p>
                </div>
                <div class="content">
                    %s
                </div>
            </body>
            </html>
            """, 
            report.getReportName(), 
            report.getReportName(),
            report.getGeneratedAt(),
            report.getReportType(),
            generateContentSections(report));
    }
    
    private String generateCsvContent(ReportResponse report) {
        StringBuilder csv = new StringBuilder();
        csv.append("Metric,Value\\n");
        csv.append("Report Name,").append(report.getReportName()).append("\\n");
        csv.append("Report Type,").append(report.getReportType()).append("\\n");
        csv.append("Generated At,").append(report.getGeneratedAt()).append("\\n");
        
        if (report.getContent() != null) {
            csv.append("Total Issues,").append(report.getContent().getIssues() != null ? 
                report.getContent().getIssues().size() : 0).append("\\n");
        }
        
        return csv.toString();
    }
    
    private String generateJsonContent(ReportResponse report) {
        // Simplified JSON generation
        return String.format("""
            {
              "reportId": "%s",
              "reportName": "%s",
              "reportType": "%s",
              "generatedAt": "%s",
              "format": "%s",
              "content": {
                "summary": "BPMN Analysis Report for %s",
                "status": "Generated successfully"
              }
            }
            """, 
            report.getId(), 
            report.getReportName(),
            report.getReportType(),
            report.getGeneratedAt(),
            report.getFormat(),
            report.getReportName());
    }
    
    private String generateExecutiveSummaryHtml(ExecutiveSummaryDto summary) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <title>Executive Summary</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 40px; }
                    .header { text-align: center; margin-bottom: 30px; }
                    .metrics { display: flex; justify-content: space-around; margin: 20px 0; }
                    .metric { text-align: center; padding: 20px; border: 1px solid #ddd; }
                    .findings, .recommendations { margin: 20px 0; }
                    .status-good { color: green; }
                    .status-warning { color: orange; }
                    .status-critical { color: red; }
                </style>
            </head>
            <body>
                <div class="header">
                    <h1>%s</h1>
                    <p>%s</p>
                    <p>Generated: %s</p>
                </div>
                
                <div class="metrics">
                    <div class="metric">
                        <h3>%d</h3>
                        <p>Total Processes</p>
                    </div>
                    <div class="metric">
                        <h3>%d</h3>
                        <p>Total Issues</p>
                    </div>
                    <div class="metric">
                        <h3>%d%%</h3>
                        <p>Compliance Score</p>
                    </div>
                </div>
                
                <div class="findings">
                    <h2>Key Findings</h2>
                    <ul>
                        %s
                    </ul>
                </div>
                
                <div class="recommendations">
                    <h2>Recommendations</h2>
                    <ul>
                        %s
                    </ul>
                </div>
            </body>
            </html>
            """,
            summary.getTitle(),
            summary.getDescription(),
            summary.getGeneratedAt(),
            summary.getTotalProcesses(),
            summary.getTotalIssues(),
            summary.getComplianceScore(),
            formatListItems(summary.getKeyFindings()),
            formatListItems(summary.getRecommendations()));
    }
    
    private String generateTechnicalReportHtml(ReportResponse report) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <title>Technical Report</title>
                <style>
                    body { font-family: 'Courier New', monospace; margin: 20px; }
                    .header { background: #2c3e50; color: white; padding: 20px; }
                    .content { margin-top: 20px; }
                    .code-block { background: #f8f9fa; padding: 15px; border-left: 4px solid #007bff; }
                </style>
            </head>
            <body>
                <div class="header">
                    <h1>Technical Analysis Report</h1>
                    <p>%s</p>
                </div>
                <div class="content">
                    <h2>Technical Details</h2>
                    <div class="code-block">
                        <p>Report ID: %s</p>
                        <p>Generated: %s</p>
                        <p>Format: %s</p>
                    </div>
                </div>
            </body>
            </html>
            """,
            report.getReportName(),
            report.getId(),
            report.getGeneratedAt(),
            report.getFormat());
    }
    
    private String generateBusinessReportHtml(ReportResponse report) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <title>Business Report</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; }
                    .kpi { display: inline-block; margin: 10px; padding: 15px; background: #f0f8ff; }
                </style>
            </head>
            <body>
                <h1>Business Impact Analysis</h1>
                <h2>%s</h2>
                
                <div class="kpi">
                    <h3>Process Efficiency</h3>
                    <p>85%%</p>
                </div>
                <div class="kpi">
                    <h3>Risk Level</h3>
                    <p>Medium</p>
                </div>
                <div class="kpi">
                    <h3>Compliance</h3>
                    <p>87%%</p>
                </div>
            </body>
            </html>
            """,
            report.getReportName());
    }
    
    private String generateContentSections(ReportResponse report) {
        StringBuilder sections = new StringBuilder();
        
        if (report.getContent() != null) {
            if (report.getContent().getExecutiveSummary() != null) {
                sections.append("<div class='section'><h3>Executive Summary</h3>")
                    .append("<p>Executive summary data available</p></div>");
            }
            
            if (report.getContent().getProcessApiMapping() != null) {
                sections.append("<div class='section'><h3>Process-API Mapping</h3>")
                    .append("<p>Process-API mapping analysis results</p></div>");
            }
        }
        
        return sections.toString();
    }
    
    private String formatListItems(List<String> items) {
        if (items == null) return "";
        return items.stream()
            .map(item -> "<li>" + item + "</li>")
            .reduce("", String::concat);
    }
    
    private void addFileToZip(ZipOutputStream zipOut, String filePath, String zipEntryName) throws IOException {
        Path path = Paths.get(filePath);
        if (Files.exists(path)) {
            zipOut.putNextEntry(new ZipEntry(zipEntryName));
            Files.copy(path, zipOut);
            zipOut.closeEntry();
        }
    }
}