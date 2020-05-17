package org.abubusoft.mee.model;

import java.util.HashMap;
import java.util.Map;

public class ExpressionContext {
  private final Map<String, Double> variables;

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

  public boolean hasVariable(String name) {
    return variables.containsKey(name);
  }
}
