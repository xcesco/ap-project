package org.abubusoft.mee.server.model.compute.expression;

import org.abubusoft.mee.server.model.compute.MultiVariableValue;

public class VariableNode implements ExpressionNode {
  private final String name;

  public VariableNode(String name) {
    this.name = name;
  }

  @Override
  public double evaluate(MultiVariableValue multiVariableValue) {
    return multiVariableValue.getVariableValue(name);
  }
}
