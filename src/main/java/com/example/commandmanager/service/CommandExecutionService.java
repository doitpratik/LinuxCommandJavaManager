package com.example.commandmanager.service;

import com.example.commandmanager.config.CommandPolicyProperties;
import com.example.commandmanager.model.ExecutionRequest;
import com.example.commandmanager.model.ExecutionResponse;
import com.example.commandmanager.util.ParameterResolver;
import com.example.commandmanager.util.ShellCommandBuilder;
import com.example.commandmanager.util.StreamCollector;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class CommandExecutionService {
  private final CommandValidator validator;
  private final CommandPolicyProperties policy;
  private final ParameterResolver resolver = new ParameterResolver();
  private final ShellCommandBuilder shellCommandBuilder = new ShellCommandBuilder();

  public CommandExecutionService(CommandValidator validator, CommandPolicyProperties policy) {
    this.validator = validator;
    this.policy = policy;
  }

  public ExecutionResponse execute(ExecutionRequest request) {
    validator.validate(request);
    ExecutionRequest resolved = resolveParameters(request);
    if (resolved.getCommandMode() == com.example.commandmanager.model.CommandMode.SHELL_COMMAND) {
      String rawCommand = resolver.resolveValue(resolved.getRawCommand(), resolved.getRuleContext());
      return runShellCommand(rawCommand, resolved);
    }
    List<String> command = buildCommand(resolved);
    return runCommand(command, resolved);
  }

  private ExecutionRequest resolveParameters(ExecutionRequest request) {
    resolver.resolveMap(request.getParameters(), request.getRuleContext());
    resolver.resolveMap(request.getEnvironment(), request.getRuleContext());
    request.setArgs(resolver.resolveList(request.getArgs(), request.getRuleContext()));
    return request;
  }

  private List<String> buildCommand(ExecutionRequest request) {
    List<String> command = new ArrayList<>();
    if (request.getSudoUser() != null) {
      command.add("sudo");
      command.add("-u");
      command.add(request.getSudoUser());
    }

    command.add(request.getExecutable());

    for (Map.Entry<String, String> entry : request.getParameters().entrySet()) {
      command.add(entry.getKey());
      if (entry.getValue() != null && !entry.getValue().isBlank()) {
        command.add(entry.getValue());
      }
    }

    command.addAll(request.getArgs());
    return command;
  }

  private ExecutionResponse runShellCommand(String rawCommand, ExecutionRequest request) {
    String shell = request.getShell() == null ? policy.getAllowedShells().get(0) : request.getShell();
    String commandString = rawCommand;
    if (request.getEnvSetupExecutable() != null) {
      commandString = "source " + request.getEnvSetupExecutable() + " && " + commandString;
    }
    return runCommand(List.of(shell, "-lc", commandString), request);
  }

  private ExecutionResponse runCommand(List<String> command, ExecutionRequest request) {
    List<String> processCommand = command;
    if (request.getShell() != null || request.getEnvSetupExecutable() != null) {
      if (!policy.isAllowShellExecution()) {
        throw new IllegalArgumentException("Shell execution is disabled by policy");
      }
      String shell = request.getShell() == null ? policy.getAllowedShells().get(0) : request.getShell();
      String commandString = shellCommandBuilder.buildCommand(command);
      if (request.getEnvSetupExecutable() != null) {
        commandString = "source " + request.getEnvSetupExecutable() + " && " + commandString;
      }
      processCommand = List.of(shell, "-lc", commandString);
    }

    ProcessBuilder builder = new ProcessBuilder(processCommand);
    builder.redirectErrorStream(false);
    builder.environment().putAll(request.getEnvironment());
    if (request.getWorkingDirectory() != null && !request.getWorkingDirectory().isBlank()) {
      builder.directory(new java.io.File(request.getWorkingDirectory()));
    }

    Instant start = Instant.now();
    try {
      Process process = builder.start();
      StreamCollector stdout = new StreamCollector(process.getInputStream());
      StreamCollector stderr = new StreamCollector(process.getErrorStream());
      Thread stdoutThread = new Thread(stdout);
      Thread stderrThread = new Thread(stderr);
      stdoutThread.start();
      stderrThread.start();
      boolean finished = process.waitFor(request.getTimeout().toSeconds(), java.util.concurrent.TimeUnit.SECONDS);
      if (!finished) {
        process.destroyForcibly();
        return new ExecutionResponse(124, processCommand, stdout.getOutput(), "Timeout exceeded", durationSince(start));
      }
      stdoutThread.join();
      stderrThread.join();
      return new ExecutionResponse(process.exitValue(), processCommand, stdout.getOutput(), stderr.getOutput(), durationSince(start));
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return new ExecutionResponse(1, processCommand, "", "Command execution interrupted", durationSince(start));
    } catch (IOException e) {
      return new ExecutionResponse(1, processCommand, "", e.getMessage(), durationSince(start));
    }
  }

  private Duration durationSince(Instant start) {
    return Duration.between(start, Instant.now());
  }
}
