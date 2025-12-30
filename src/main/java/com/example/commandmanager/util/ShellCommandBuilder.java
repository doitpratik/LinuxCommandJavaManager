package com.example.commandmanager.util;

import java.util.ArrayList;
import java.util.List;

public class ShellCommandBuilder {
  public String buildCommand(List<String> command) {
    List<String> escaped = new ArrayList<>();
    for (String part : command) {
      escaped.add(escapeSingleQuotes(part));
    }
    return String.join(" ", escaped);
  }

  private String escapeSingleQuotes(String value) {
    if (value == null) {
      return "''";
    }
    return "'" + value.replace("'", "'\\''") + "'";
  }
}
