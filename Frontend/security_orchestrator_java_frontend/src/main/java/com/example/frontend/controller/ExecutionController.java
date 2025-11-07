package com.example.frontend.controller;

import com.example.frontend.dto.ExecutionDto;
import com.example.frontend.dto.WorkflowDto;
import com.example.frontend.service.BackendService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/executions")
public class ExecutionController {

    private final BackendService backendService;

    public ExecutionController(BackendService backendService) {
        this.backendService = backendService;
    }

    @GetMapping("/{executionId}")
    public String viewExecution(@PathVariable String executionId, Model model) {
        // Note: In a real implementation, you'd need an execution lookup by ID
        // For now, we'll assume executionId includes workflow reference
        String workflowId = executionId.replace("exec-", "").split("-")[0];

        WorkflowDto workflow = backendService.getWorkflow(workflowId);
        ExecutionDto execution = backendService.getExecutionStatus(workflowId);

        model.addAttribute("workflow", workflow);
        model.addAttribute("execution", execution);

        return "executions/detail";
    }

    @GetMapping("/{executionId}/results")
    public String viewExecutionResults(@PathVariable String executionId, Model model) {
        // Similar to above
        String workflowId = executionId.replace("exec-", "").split("-")[0];

        WorkflowDto workflow = backendService.getWorkflow(workflowId);
        ExecutionDto execution = backendService.getExecutionStatus(workflowId);

        model.addAttribute("workflow", workflow);
        model.addAttribute("execution", execution);

        return "executions/results";
    }
}