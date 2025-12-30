package com.example.commandmanager.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class StreamCollector implements Runnable {
  private final InputStream inputStream;
  private final StringBuilder buffer = new StringBuilder();

  public StreamCollector(InputStream inputStream) {
    this.inputStream = inputStream;
  }

  @Override
  public void run() {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
      String line;
      while ((line = reader.readLine()) != null) {
        buffer.append(line).append(System.lineSeparator());
      }
    } catch (IOException ignored) {
      buffer.append("<stream read error>").append(System.lineSeparator());
    }
  }

  public String getOutput() {
    return buffer.toString().trim();
  }
}
