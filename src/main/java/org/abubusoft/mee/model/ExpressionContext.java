package org.abubusoft.mee.model;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ExpressionContext {
  private final Map<String, Double> variables;

  @Override
  public String toString() {
    String mapAsString = variables.keySet().stream()
            .map(key -> key + "=" + variables.get(key))
            .collect(Collectors.joining(", ", "{", "}"));
    return mapAsString;
  }

  public ExpressionContext(Map<String, Double> variables) {
    this.variables = variables;
  }

  public static class Builder {
    private final Map<String, Double> variables = new HashMap<>();

    public static Builder create() {
      return new Builder();
    }

    public Builder add(String variable, double value) {
      variables.put(variable, value);
      return this;
    }

    public ExpressionContext build() {
      return new ExpressionContext(variables);
    }
  }

  public Double getVariable(String name) {
    return variables.get(name);
  }
}
