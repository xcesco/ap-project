package org.abubusoft.mee.server.model.compute;

import org.abubusoft.mee.server.exceptions.AppAssert;
import org.abubusoft.mee.server.exceptions.AppRuntimeException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class VariableValues {
  private final Map<String, Double> values;

  VariableValues(Map<String, Double> values) {
    this.values = values;
  }

  public int size() {
    return values.size();
  }

  public Double get(String name) {
    return values.get(name);
  }

  public static class Builder {
    /**
     * LinkedHashMap is used to respect insertion order
     */
    private final Map<String, Double> values = new LinkedHashMap<>();

    public static Builder create() {
      return new Builder();
    }

    public Builder add(String variableName, double variableValue) {
      values.put(variableName, variableValue);
      return this;
    }

    public Builder addAll(List<String> keysList, List<Double> valuesList) {
      AppAssert.assertTrue(keysList.size() == valuesList.size(), AppRuntimeException.class, "Invalid variable values definition");
      for (int i = 0; i < keysList.size(); i++) {
        add(keysList.get(i), valuesList.get(i));
      }
      return this;
    }

    public VariableValues build() {
      return new VariableValues(values);
    }
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", VariableValues.class.getSimpleName() + "[", "]")
            .add("values=" + values)
            .toString();
  }
}
