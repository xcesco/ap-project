package org.abubusoft.mee.server.model.compute.expression;

import org.abubusoft.mee.server.model.compute.MultiVariableValue;

public class ConstantNode implements ExpressionNode {
  private final double value;

  public ConstantNode(double value) {
    this.value = value;
  }

  @Override
  public double evaluate(MultiVariableValue multiVariableValue) {
    return value;
  }
}
