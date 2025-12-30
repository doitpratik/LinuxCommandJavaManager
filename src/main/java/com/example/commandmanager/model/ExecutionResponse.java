package com.example.commandmanager.model;

import java.time.Duration;
import java.util.List;

public class ExecutionResponse {
  private int exitCode;
  private List<String> command;
  private String stdout;
  private String stderr;
  private Duration duration;

  public ExecutionResponse(int exitCode, List<String> command, String stdout, String stderr, Duration duration) {
    this.exitCode = exitCode;
    this.command = command;
    this.stdout = stdout;
    this.stderr = stderr;
    this.duration = duration;
  }

  public int getExitCode() {
    return exitCode;
  }

  public List<String> getCommand() {
    return command;
  }

  public String getStdout() {
    return stdout;
  }

  public String getStderr() {
    return stderr;
  }

  public Duration getDuration() {
    return duration;
  }
}
