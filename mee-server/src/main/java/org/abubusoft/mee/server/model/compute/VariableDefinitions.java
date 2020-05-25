package org.abubusoft.mee.server.model.compute;

import com.google.common.collect.Lists;
import org.abubusoft.mee.server.exceptions.AppAssert;
import org.abubusoft.mee.server.exceptions.InvalidVariableDefinitionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VariableDefinitions {
  private static final Logger logger = LoggerFactory
          .getLogger(VariableDefinitions.class);

  private final List<VariableDefinition> variables;

  public VariableDefinitions() {
    this.variables = new ArrayList<>();
  }

  public List<String> getKeysList() {
    return variables.stream().map(VariableDefinition::getName).collect(Collectors.toList());
  }

  private boolean isAlreadyDefined(String name) {
    return variables.stream().filter(item -> item.getName().equals(name)).findFirst().map(item -> true).orElse(false);
  }

  public VariableDefinitions add(VariableDefinition variableDefinition) {
    if (isAlreadyDefined(variableDefinition.getName())) {
      String message = String.format("Variable '%s' is defined twice", variableDefinition.getName());
      AppAssert.fail(InvalidVariableDefinitionException.class, message);
    }
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

      // check variable interval size
      for (int i = 0; i < variables.size(); i++) {
        currentCount = variables.get(i).getValues().size();
        currentName = variables.get(i).getName();
        AppAssert.assertTrue(firstCount == currentCount, InvalidVariableDefinitionException.class,
                "Variables '%s' and '%s' have different size (%s, %s)",
                firstName, currentName, firstCount, currentCount);
      }

      // fills tuples
      for (int i = 0; i < firstCount; i++) {
        int finalI = i;
        List<Double> valuesTuple = variables.stream()
                .map(item -> item.getValues().get(finalI))
                .collect(Collectors.toList());

        values.add(valuesTuple);
      }

      // build variable values list
      List<VariableValues> result = values.stream()
              .map(value -> VariableValues.Builder.create().addAll(keysList, value).build())
              .collect(Collectors.toList());

      return result;
    }

  }

  public VariableDefinition get(String variableName) {
    return this.variables.stream().filter(item -> item.getName().equals(variableName)).findFirst().orElse(null);
  }
}
