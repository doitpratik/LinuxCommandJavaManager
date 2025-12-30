package com.example.commandmanager.service;

import com.example.commandmanager.config.CommandPolicyProperties;
import com.example.commandmanager.model.ExecutionRequest;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class CommandValidator {
  private final CommandPolicyProperties policy;

  public CommandValidator(CommandPolicyProperties policy) {
    this.policy = policy;
  }

  public void validate(ExecutionRequest request) {
    ensureAllowlistConfigured();
    requireAllowedExecutable(request.getExecutable());
    if (request.getShell() != null) {
      requireShellAllowed(request.getShell());
    }
    if (request.getSudoUser() != null && !policy.isAllowSudo()) {
      throw new IllegalArgumentException("Sudo execution is disabled by policy");
    }
    validateNoDeniedTokens(request.getExecutable());
    validateNoDeniedTokens(request.getArgs());
    validateNoDeniedTokens(request.getParameters());
    validateNoDeniedTokens(request.getEnvironment());
    if (request.getEnvSetupExecutable() != null && !policy.isAllowShellExecution()) {
      throw new IllegalArgumentException("Env setup executable requires shell execution to be enabled");
    }
  }

  private void ensureAllowlistConfigured() {
    if (policy.getAllowedExecutables() == null || policy.getAllowedExecutables().isEmpty()) {
      throw new IllegalArgumentException("No allowed executables configured. Set command.policy.allowed-executables.");
    }
  }

  private void requireAllowedExecutable(String executable) {
    if (!policy.getAllowedExecutables().contains(executable)) {
      throw new IllegalArgumentException("Executable is not allowed by policy: " + executable);
    }
  }

  private void requireShellAllowed(String shell) {
    if (!policy.isAllowShellExecution()) {
      throw new IllegalArgumentException("Shell execution is disabled by policy");
    }
    if (!policy.getAllowedShells().contains(shell)) {
      throw new IllegalArgumentException("Shell is not allowed by policy: " + shell);
    }
  }

  private void validateNoDeniedTokens(Collection<String> values) {
    if (values == null) {
      return;
    }
    for (String value : values) {
      validateNoDeniedTokens(value);
    }
  }

  private void validateNoDeniedTokens(Map<String, String> values) {
    if (values == null) {
      return;
    }
    for (Map.Entry<String, String> entry : values.entrySet()) {
      validateNoDeniedTokens(entry.getKey());
      validateNoDeniedTokens(entry.getValue());
    }
  }

  private void validateNoDeniedTokens(String value) {
    if (value == null) {
      return;
    }
    for (String token : policy.getDeniedTokens()) {
      if (value.contains(token)) {
        throw new IllegalArgumentException("Token is not allowed by policy: " + token);
      }
    }
  }
}
