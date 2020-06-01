package org.abubusoft.mee.server.model.compute;

import org.abubusoft.mee.server.model.compute.expression.ExpressionNode;

public class Expression {
  private final String expressionString;
  private final ExpressionNode treeNodeRoot;

  public Expression(String expressionString, ExpressionNode treeNodeRoot) {
    this.expressionString = expressionString;
    this.treeNodeRoot = treeNodeRoot;
  }

  @Override
  public String toString() {
    return expressionString;
  }

  public double evaluate(MultiVariableValue values) {
    return treeNodeRoot.evaluate(values);
  }
}
