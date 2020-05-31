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
  private final List<VariableValuesRange> variableValuesRanges;

  public VariablesDefinition() {
    this.variableValuesRanges = new ArrayList<>();
  }

  public List<String> getVariableNameList() {
    return variableValuesRanges.stream().map(VariableValuesRange::getName).collect(Collectors.toList());
  }

  private boolean isAlreadyDefined(String name) {
    return variableValuesRanges.stream()
            .anyMatch(item -> item.getName().equals(name));
  }

  public VariablesDefinition add(VariableValuesRange variableValuesList) {
    if (isAlreadyDefined(variableValuesList.getName())) {
      String message = String.format("Variable '%s' is defined twice", variableValuesList.getName());
      AppAssert.fail(InvalidVariableDefinitionException.class, message);
    }
    variableValuesRanges.add(variableValuesList);
    return this;
  }

  public List<MultiVariableValue> buildValues(ValueType valueType) {
    return buildValuesAsStream(valueType).collect(Collectors.toList());
  }

  public Stream<MultiVariableValue> buildValuesAsStream(ValueType valueType) {
    final List<String> variableNames = getVariableNameList();
    if (valueType == ValueType.GRID) {
      List<List<Double>> values = variableValuesRanges.stream()
              .map(VariableValuesRange::getValues)
              .collect(Collectors.toList());
      return Lists.cartesianProduct(values)
              .stream()
              .map(value -> MultiVariableValue.Builder.create()
                      .addAll(variableNames, value).build());
    } else {
      int firstCount = variableValuesRanges.get(0).getSize();
      String firstName = variableValuesRanges.get(0).getName();

      // check variable interval size
      checkVariableRangeSize(firstCount, firstName);

      Stream<List<Double>> stream = IntStream.range(0, firstCount)
              .mapToObj(index -> variableValuesRanges.stream()
                      .map(item -> item.get(index))
                      .collect(Collectors.toList()));

      return stream.map(value -> MultiVariableValue.Builder.create().addAll(variableNames, value).build());
    }
  }

  private void checkVariableRangeSize(int firstCount, String firstName) {
    variableValuesRanges.forEach(variable -> {
      int currentCount = variable.getSize();
      String currentName = variable.getName();
      AppAssert.assertTrue(firstCount == currentCount, InvalidVariableDefinitionException.class,
              "Variables '%s' and '%s' have different size (%s, %s)",
              firstName, currentName, firstCount, currentCount);
    });
  }

  public VariableValuesRange get(String variableName) {
    return this.variableValuesRanges.stream().filter(item -> item.getName().equals(variableName)).findFirst().orElse(null);
  }
}
