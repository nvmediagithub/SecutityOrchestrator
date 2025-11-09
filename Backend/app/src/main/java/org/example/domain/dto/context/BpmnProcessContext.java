package org.example.domain.dto.context;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BpmnProcessContext {
    private String processId;
    private String processName;
    private Map<String, Object> processVariables;
    private Map<String, Object> flowElements;
}