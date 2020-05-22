package org.abubusoft.mee.server.model.compute;

import com.google.common.collect.Lists;
import org.abubusoft.mee.server.exceptions.AppAssert;
import org.abubusoft.mee.server.exceptions.InvalidVariableDefinitionException;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class VariableDefinitions {
  @Override
  public String toString() {
    return new StringJoiner(", ", VariableDefinitions.class.getSimpleName() + "[", "]")
            .add("variables=" + variables)
            .toString();
  }

  private final List<VariableDefinition> variables;

  public VariableDefinitions() {
    this.variables = new ArrayList<>();
  }

  public int size() {
    return variables.size();
  }

  public List<String> getKeysList() {
    return variables.stream().map(VariableDefinition::getName).collect(Collectors.toList());
  }

  public VariableDefinitions add(VariableDefinition variableDefinition) {
    variables.add(variableDefinition);
    return this;
  }

  public List<VariableValues> buildValues(ValuesType valuesType) {
    final List<String> keysList = getKeysList();
    if (valuesType == ValuesType.GRID) {

      List<List<Double>> values = variables.stream().map(VariableDefinition::getValues).collect(Collectors.toList());
      return Lists.cartesianProduct(values)
              .stream()
              .map(value -> VariableValues.Builder.create().addAll(keysList, value).build()).collect(Collectors.toList());
    } else {
      int firstCount = variables.get(0).getValues().size();
      String firstName = variables.get(0).getName();
      int currentCount;
      String currentName;
      List<List<Double>> values = new ArrayList<>();
      for (int i = 0; i < variables.size(); i++) {
        int finalI = i;
        currentCount = variables.get(i).getValues().size();
        currentName = variables.get(i).getName();
        AppAssert.assertTrue(firstCount == currentCount, InvalidVariableDefinitionException.class,
                "Variables '%s' and '%s' have different size (%s, %s)",
                firstName, currentName, firstCount, currentCount);
        values.add(variables.stream().map(item -> item.getValues().get(finalI)).collect(Collectors.toList()));
      }
      return values.stream().map(value -> VariableValues.Builder.create().addAll(keysList, value).build()).collect(Collectors.toList());
    }

  }

  public VariableDefinition get(String variableName) {
    return this.variables.stream().filter(item -> item.getName().equals(variableName)).findFirst().orElse(null);
  }
}
