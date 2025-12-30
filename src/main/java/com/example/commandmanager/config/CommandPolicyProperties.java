package com.example.commandmanager.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "command.policy")
public class CommandPolicyProperties {
  private List<String> allowedExecutables = new ArrayList<>();
  private List<String> allowedShells = List.of("/bin/bash", "/bin/sh");
  private List<String> deniedTokens = List.of(";", "&&", "||", "|", ">", "<");
  private boolean allowShellExecution = false;
  private boolean allowSudo = false;

  public List<String> getAllowedExecutables() {
    return allowedExecutables;
  }

  public void setAllowedExecutables(List<String> allowedExecutables) {
    this.allowedExecutables = allowedExecutables;
  }

  public List<String> getAllowedShells() {
    return allowedShells;
  }

  public void setAllowedShells(List<String> allowedShells) {
    this.allowedShells = allowedShells;
  }

  public List<String> getDeniedTokens() {
    return deniedTokens;
  }

  public void setDeniedTokens(List<String> deniedTokens) {
    this.deniedTokens = deniedTokens;
  }

  public boolean isAllowShellExecution() {
    return allowShellExecution;
  }

  public void setAllowShellExecution(boolean allowShellExecution) {
    this.allowShellExecution = allowShellExecution;
  }

  public boolean isAllowSudo() {
    return allowSudo;
  }

  public void setAllowSudo(boolean allowSudo) {
    this.allowSudo = allowSudo;
  }
}
