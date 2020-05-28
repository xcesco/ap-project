package org.abubusoft.mee.server.model.compute;

import com.google.common.collect.Lists;
import org.abubusoft.mee.server.exceptions.AppAssert;
import org.abubusoft.mee.server.exceptions.InvalidVariableDefinitionException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class VariablesDefinition {
  private final List<VariableTuple> variables;

  public VariablesDefinition() {
    this.variables = new ArrayList<>();
  }

  public List<String> getVariableNameList() {
    return variables.stream().map(VariableTuple::getName).collect(Collectors.toList());
  }

  private boolean isAlreadyDefined(String name) {
    return variables.stream()
            .anyMatch(item -> item.getName().equals(name));
  }

  public VariablesDefinition add(VariableTuple variableTuple) {
    if (isAlreadyDefined(variableTuple.getName())) {
      String message = String.format("Variable '%s' is defined twice", variableTuple.getName());
      AppAssert.fail(InvalidVariableDefinitionException.class, message);
    }
    variables.add(variableTuple);
    return this;
  }

  public List<MultiVariableValue> buildValues(ValueType valueType) {
    return buildValuesAsStream(valueType).collect(Collectors.toList());
  }

  public Stream<MultiVariableValue> buildValuesAsStream(ValueType valueType) {
    final List<String> variableNames = getVariableNameList();
    if (valueType == ValueType.GRID) {
      List<List<Double>> values = variables.stream()
              .map(VariableTuple::getValues)
              .collect(Collectors.toList());
      return Lists.cartesianProduct(values)
              .stream()
              .map(value -> MultiVariableValue.Builder.create()
                      .addAll(variableNames, value).build());
    } else {
      int firstCount = variables.get(0).getValues().size();
      String firstName = variables.get(0).getName();

      // check variable interval size
      variables.forEach(variable -> {
        int currentCount = variable.getValues().size();
        String currentName = variable.getName();
        AppAssert.assertTrue(firstCount == currentCount, InvalidVariableDefinitionException.class,
                "Variables '%s' and '%s' have different size (%s, %s)",
                firstName, currentName, firstCount, currentCount);
      });

      Stream<List<Double>> stream = IntStream.range(0, firstCount)
              .mapToObj(index -> variables.stream()
                      .map(item -> item.getValues().get(index))
                      .collect(Collectors.toList()));

      return stream
              .map(value -> MultiVariableValue.Builder.create()
                      .addAll(variableNames, value).build());
    }
  }

  public VariableTuple get(String variableName) {
    return this.variables.stream().filter(item -> item.getName().equals(variableName)).findFirst().orElse(null);
  }
}
