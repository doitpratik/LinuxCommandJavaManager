package com.example.commandmanager.util;

import com.example.commandmanager.model.RuleContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParameterResolver {
  private static final Pattern TOKEN_PATTERN = Pattern.compile("\\$\\{(job|trade|product)\\.([^}]+)}");

  public Map<String, String> resolveMap(Map<String, String> params, RuleContext context) {
    params.replaceAll((key, value) -> resolveValue(value, context));
    return params;
  }

  public List<String> resolveList(List<String> values, RuleContext context) {
    List<String> resolved = new ArrayList<>();
    for (String value : values) {
      resolved.add(resolveValue(value, context));
    }
    return resolved;
  }

  public String resolveValue(String value, RuleContext context) {
    if (value == null) {
      return null;
    }
    Matcher matcher = TOKEN_PATTERN.matcher(value);
    StringBuffer result = new StringBuffer();
    while (matcher.find()) {
      String scope = matcher.group(1);
      String key = matcher.group(2);
      String replacement = lookup(scope, key, context);
      matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
    }
    matcher.appendTail(result);
    return result.toString();
  }

  private String lookup(String scope, String key, RuleContext context) {
    return switch (scope) {
      case "job" -> getRequired(context.getJobAttributes(), key, "job");
      case "trade" -> getRequired(context.getTradeAttributes(), key, "trade");
      case "product" -> getRequired(context.getProductAttributes(), key, "product");
      default -> throw new IllegalArgumentException("Unsupported scope: " + scope);
    };
  }

  private String getRequired(Map<String, String> source, String key, String scope) {
    String value = source.get(key);
    if (value == null) {
      throw new IllegalArgumentException("Missing " + scope + " attribute for key: " + key);
    }
    return value;
  }
}
