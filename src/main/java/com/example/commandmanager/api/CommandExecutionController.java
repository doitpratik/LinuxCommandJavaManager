package com.example.commandmanager.api;

import com.example.commandmanager.model.ExecutionRequest;
import com.example.commandmanager.model.ExecutionResponse;
import com.example.commandmanager.service.CommandExecutionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/commands")
public class CommandExecutionController {
  private final CommandExecutionService executionService;

  public CommandExecutionController(CommandExecutionService executionService) {
    this.executionService = executionService;
  }

  @PostMapping("/execute")
  public ResponseEntity<ExecutionResponse> execute(@Valid @RequestBody ExecutionRequest request) {
    ExecutionResponse response = executionService.execute(request);
    return ResponseEntity.ok(response);
  }
}
