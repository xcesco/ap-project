package org.abubusoft.mee.server.model.compute;

import org.abubusoft.mee.server.exceptions.InvalidVariableDefinitionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VariablesDefinitionTest {

  @Test
  public void testWrongDefinitions() {
    // step > 0
    Assertions.assertThrows(InvalidVariableDefinitionException.class, () -> {
      builVariableDefinitions(1, 0, 3);
    });

    Assertions.assertThrows(InvalidVariableDefinitionException.class, () -> {
      builVariableDefinitions(3, -1, 1);
    });

    Assertions.assertThrows(InvalidVariableDefinitionException.class, () -> {
      builVariableDefinitions(3, 1, 1);
    });


    assertEquals(1, builVariableDefinitions(1, 4, 3));
  }

  private int builVariableDefinitions(int i, int i2, int i3) {
    VariablesDefinition variablesDefinition = new VariablesDefinition();
    variablesDefinition
            .add(VariableValuesRange.Builder.create()
                    .setName("x0").setInterval(i, i2, i3).build());

    return variablesDefinition.getVariableValuesRange("x0").buildValuesList().size();
  }

  @Test
  void testGridValuesGeneration() {
    VariablesDefinition variablesDefinition = new VariablesDefinition();
    variablesDefinition
            .add(VariableValuesRange.Builder.create()
                    .setName("x0").setInterval(1, 1, 10).build())
            .add(VariableValuesRange.Builder.create()
                    .setName("x1").setInterval(11, 1, 20).build()
            );

    List<MultiVariableValue> values = variablesDefinition.buildValues(ValueType.GRID);

    assertEquals(values.size(), 100);
    assertEquals(values.get(0).getVariableValue("x0"), 1);
    assertEquals(values.get(0).getVariableValue("x1"), 11);
    assertEquals(values.get(99).getVariableValue("x0"), 10);
    assertEquals(values.get(99).getVariableValue("x1"), 20);
  }

  @Test
  void testListValuesGeneration() {
    VariablesDefinition variablesDefinition = new VariablesDefinition();
    variablesDefinition
            .add(VariableValuesRange.Builder.create()
                    .setName("x0").setInterval(1, 1, 10).build())
            .add(VariableValuesRange.Builder.create()
                    .setName("x1").setInterval(11, 1, 20).build()
            );

    List<MultiVariableValue> values = variablesDefinition.buildValues(ValueType.LIST);

    assertEquals(values.size(), 10);
    assertEquals(values.get(0).getVariableValue("x0"), 1);
    assertEquals(values.get(0).getVariableValue("x1"), 11);
    assertEquals(values.get(9).getVariableValue("x0"), 10);
    assertEquals(values.get(9).getVariableValue("x1"), 20);
  }

  @Test
  void testWrongListValues() {
    VariablesDefinition variablesDefinition = new VariablesDefinition();
    variablesDefinition
            .add(VariableValuesRange.Builder.create()
                    .setName("x0").setInterval(1, 1, 10).build())
            .add(VariableValuesRange.Builder.create()
                    .setName("x1").setInterval(1, 1, 20).build()
            );
    {
      List<MultiVariableValue> values = variablesDefinition.buildValues(ValueType.GRID);
      assertEquals(values.get(0).getVariableValue("x1"), 1);
    }
    Assertions.assertThrows(InvalidVariableDefinitionException.class, () -> {
      variablesDefinition.buildValues(ValueType.LIST);
    });

  }
}