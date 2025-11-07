package com.example.frontend.controller;

import com.example.frontend.dto.ProcessDto;
import com.example.frontend.dto.WorkflowDto;
import com.example.frontend.service.BackendService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final BackendService backendService;

    public HomeController(BackendService backendService) {
        this.backendService = backendService;
    }

    @GetMapping("/")
    public String home(Model model) {
        // Fetch processes and workflows from backend
        List<ProcessDto> processes = backendService.getProcesses();
        List<WorkflowDto> workflows = backendService.getWorkflows();

        model.addAttribute("processes", processes);
        model.addAttribute("workflows", workflows);

        return "home";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        return home(model); // Same as home for now
    }
}