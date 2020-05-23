package org.abubusoft.mee.server.model.compute;

import org.abubusoft.mee.server.exceptions.InvalidVariableDefinitionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class VariableDefinitionsTest {

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

    Assertions.assertThrows(InvalidVariableDefinitionException.class, () -> {
      builVariableDefinitions(1, 4, 3);
    });

  }

  private void builVariableDefinitions(int i, int i2, int i3) {
    VariableDefinitions variableDefinitions = new VariableDefinitions();
    variableDefinitions
            .add(VariableDefinition.Builder.create()
                    .setName("x0").setInterval(i, i2, i3).build());
  }

  @Test
  void testGridValuesGeneration() {
    VariableDefinitions variableDefinitions = new VariableDefinitions();
    variableDefinitions
            .add(VariableDefinition.Builder.create()
                    .setName("x0").setInterval(1, 1, 10).build())
            .add(VariableDefinition.Builder.create()
                    .setName("x1").setInterval(11, 1, 20).build()
            );

    List<VariableValues> values = variableDefinitions.buildValues(ValuesType.GRID);

    Assertions.assertEquals(values.size(), 100);
    Assertions.assertEquals(values.get(0).get("x0"), 1);
    Assertions.assertEquals(values.get(0).get("x1"), 11);
    Assertions.assertEquals(values.get(99).get("x0"), 10);
    Assertions.assertEquals(values.get(99).get("x1"), 20);
  }

  @Test
  void testListValuesGeneration() {
    VariableDefinitions variableDefinitions = new VariableDefinitions();
    variableDefinitions
            .add(VariableDefinition.Builder.create()
                    .setName("x0").setInterval(1, 1, 10).build())
            .add(VariableDefinition.Builder.create()
                    .setName("x1").setInterval(11, 1, 20).build()
            );

    List<VariableValues> values = variableDefinitions.buildValues(ValuesType.LIST);

    Assertions.assertEquals(values.size(), 10);
    Assertions.assertEquals(values.get(0).get("x0"), 1);
    Assertions.assertEquals(values.get(0).get("x1"), 11);
    Assertions.assertEquals(values.get(9).get("x0"), 10);
    Assertions.assertEquals(values.get(9).get("x1"), 20);
  }

  @Test
  void testWrongListValues() {
    VariableDefinitions variableDefinitions = new VariableDefinitions();
    variableDefinitions
            .add(VariableDefinition.Builder.create()
                    .setName("x0").setInterval(1, 1, 10).build())
            .add(VariableDefinition.Builder.create()
                    .setName("x1").setInterval(1, 1, 20).build()
            );
    {
      List<VariableValues> values = variableDefinitions.buildValues(ValuesType.GRID);
      Assertions.assertEquals(values.get(0).get("x1"), 1);
    }
    Assertions.assertThrows(InvalidVariableDefinitionException.class, () -> {
      variableDefinitions.buildValues(ValuesType.LIST);
    });

  }
}