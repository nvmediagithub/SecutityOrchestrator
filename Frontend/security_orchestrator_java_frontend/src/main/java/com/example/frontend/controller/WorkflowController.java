package com.example.frontend.controller;

import com.example.frontend.dto.ExecutionDto;
import com.example.frontend.dto.ProcessDto;
import com.example.frontend.dto.WorkflowDto;
import com.example.frontend.service.BackendService;
import com.example.frontend.service.WebSocketService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/workflows")
public class WorkflowController {

    private final BackendService backendService;
    private final WebSocketService webSocketService;

    public WorkflowController(BackendService backendService, WebSocketService webSocketService) {
        this.backendService = backendService;
        this.webSocketService = webSocketService;
    }

    @GetMapping
    public String listWorkflows(Model model) {
        var workflows = backendService.getWorkflows();
        model.addAttribute("workflows", workflows);
        return "workflows/list";
    }

    @GetMapping("/{id}")
    public String viewWorkflow(@PathVariable String id, Model model) {
        WorkflowDto workflow = backendService.getWorkflow(id);
        if (workflow == null) {
            return "redirect:/workflows";
        }
        model.addAttribute("workflow", workflow);
        return "workflows/detail";
    }

    @GetMapping("/create")
    public String createWorkflowForm(Model model) {
        var processes = backendService.getProcesses();
        model.addAttribute("processes", processes);
        return "workflows/create";
    }

    @PostMapping("/create")
    public String createWorkflow(@RequestParam String name,
                               @RequestParam String processId,
                               @RequestParam(required = false) String testCaseIds,
                               RedirectAttributes redirectAttributes) {
        List<String> testCases = testCaseIds != null && !testCaseIds.isEmpty()
            ? Arrays.asList(testCaseIds.split(","))
            : List.of();

        WorkflowDto workflow = backendService.createWorkflow(name, processId, testCases);
        if (workflow != null) {
            redirectAttributes.addFlashAttribute("success", "Workflow created successfully!");
            return "redirect:/workflows/" + workflow.getId();
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to create workflow.");
            return "redirect:/workflows/create";
        }
    }

    @PostMapping("/{id}/execute")
    public String executeWorkflow(@PathVariable String id, RedirectAttributes redirectAttributes) {
        ExecutionDto execution = backendService.executeWorkflow(id);
        if (execution != null) {
            redirectAttributes.addFlashAttribute("success", "Workflow execution started!");
            return "redirect:/executions/" + execution.getExecutionId();
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to start workflow execution.");
            return "redirect:/workflows/" + id;
        }
    }

    @GetMapping("/{id}/monitor")
    public String monitorWorkflowExecution(@PathVariable String id, Model model) {
        WorkflowDto workflow = backendService.getWorkflow(id);
        ExecutionDto execution = backendService.getExecutionStatus(id);

        model.addAttribute("workflow", workflow);
        model.addAttribute("execution", execution);

        return "workflows/monitor";
    }
}