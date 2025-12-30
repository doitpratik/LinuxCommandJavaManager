package com.example.commandmanager.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExecutionRequest {
  @NotBlank
  private String executable;
  private List<String> args = new ArrayList<>();
  private Map<String, String> parameters = new HashMap<>();
  private Map<String, String> environment = new HashMap<>();
  private String sudoUser;
  private String shell;
  private String envSetupExecutable;
  private Duration timeout = Duration.ofSeconds(30);
  @NotNull
  @Valid
  private RuleContext ruleContext = new RuleContext();

  public String getExecutable() {
    return executable;
  }

  public void setExecutable(String executable) {
    this.executable = executable;
  }

  public List<String> getArgs() {
    return args;
  }

  public void setArgs(List<String> args) {
    this.args = args;
  }

  public Map<String, String> getParameters() {
    return parameters;
  }

  public void setParameters(Map<String, String> parameters) {
    this.parameters = parameters;
  }

  public Map<String, String> getEnvironment() {
    return environment;
  }

  public void setEnvironment(Map<String, String> environment) {
    this.environment = environment;
  }

  public String getSudoUser() {
    return sudoUser;
  }

  public void setSudoUser(String sudoUser) {
    this.sudoUser = sudoUser;
  }

  public String getShell() {
    return shell;
  }

  public void setShell(String shell) {
    this.shell = shell;
  }

  public String getEnvSetupExecutable() {
    return envSetupExecutable;
  }

  public void setEnvSetupExecutable(String envSetupExecutable) {
    this.envSetupExecutable = envSetupExecutable;
  }

  public Duration getTimeout() {
    return timeout;
  }

  public void setTimeout(Duration timeout) {
    this.timeout = timeout;
  }

  public RuleContext getRuleContext() {
    return ruleContext;
  }

  public void setRuleContext(RuleContext ruleContext) {
    this.ruleContext = ruleContext;
  }
}
