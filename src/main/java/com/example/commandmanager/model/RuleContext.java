package com.example.commandmanager.model;

import java.util.HashMap;
import java.util.Map;

public class RuleContext {
  private Map<String, String> jobAttributes = new HashMap<>();
  private Map<String, String> tradeAttributes = new HashMap<>();
  private Map<String, String> productAttributes = new HashMap<>();

  public Map<String, String> getJobAttributes() {
    return jobAttributes;
  }

  public void setJobAttributes(Map<String, String> jobAttributes) {
    this.jobAttributes = jobAttributes;
  }

  public Map<String, String> getTradeAttributes() {
    return tradeAttributes;
  }

  public void setTradeAttributes(Map<String, String> tradeAttributes) {
    this.tradeAttributes = tradeAttributes;
  }

  public Map<String, String> getProductAttributes() {
    return productAttributes;
  }

  public void setProductAttributes(Map<String, String> productAttributes) {
    this.productAttributes = productAttributes;
  }
}
