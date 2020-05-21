package org.abubusoft.mee.server.model;

import org.abubusoft.mee.server.model.compute.ValuesType;
import org.abubusoft.mee.server.model.compute.VariableDefinition;
import org.abubusoft.mee.server.model.compute.VariableDefinitions;
import org.abubusoft.mee.server.model.compute.VariableValues;
import org.junit.jupiter.api.Test;

import java.util.List;

class VariableDefinitionsTest {

  @Test
  void buildValues() {
    VariableDefinitions variableDefinitions = new VariableDefinitions();
    variableDefinitions
            .add(VariableDefinition.Builder.create()
                    .setName("x0").setInterval(1, 1, 3).build())
            .add(VariableDefinition.Builder.create()
                    .setName("x1").setInterval(2, 2, 6).build()
            );

    List<VariableValues> values = variableDefinitions.buildValues(ValuesType.GRID);

    values.stream().forEach(result -> {
      System.out.println(result + " ");
    });
  }

  @Test
  void buildListValues() {
    VariableDefinitions variableDefinitions = new VariableDefinitions();
    variableDefinitions
            .add(VariableDefinition.Builder.create()
                    .setName("x0").setInterval(1, 1, 3).build())
            .add(VariableDefinition.Builder.create()
                    .setName("x1").setInterval(2, 2, 6).build()
            );

    List<VariableValues> values = variableDefinitions.buildValues(ValuesType.LIST);

    values.stream().forEach(result -> {
      System.out.println(result + " ");
    });
  }
}