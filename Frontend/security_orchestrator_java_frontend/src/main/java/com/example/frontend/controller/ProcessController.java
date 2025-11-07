package com.example.frontend.controller;

import com.example.frontend.dto.ProcessDto;
import com.example.frontend.service.BackendService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequestMapping("/processes")
public class ProcessController {

    private final BackendService backendService;

    public ProcessController(BackendService backendService) {
        this.backendService = backendService;
    }

    @GetMapping
    public String listProcesses(Model model) {
        var processes = backendService.getProcesses();
        model.addAttribute("processes", processes);
        return "processes/list";
    }

    @GetMapping("/{id}")
    public String viewProcess(@PathVariable String id, Model model) {
        ProcessDto process = backendService.getProcess(id);
        if (process == null) {
            return "redirect:/processes";
        }
        model.addAttribute("process", process);
        return "processes/detail";
    }

    @GetMapping("/create")
    public String createProcessForm() {
        return "processes/create";
    }

    @PostMapping("/create")
    public String createProcess(@RequestParam("file") MultipartFile file,
                              @RequestParam(value = "name", required = false) String name,
                              RedirectAttributes redirectAttributes) {
        try {
            ProcessDto process = backendService.createProcess(file, name);
            if (process != null) {
                redirectAttributes.addFlashAttribute("success", "Process created successfully!");
                return "redirect:/processes/" + process.getId();
            } else {
                redirectAttributes.addFlashAttribute("error", "Failed to create process.");
            }
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "File upload failed: " + e.getMessage());
        }
        return "redirect:/processes/create";
    }
}