package org.abubusoft.mee.server.model.compute.expression;

import org.abubusoft.mee.server.model.compute.MultiVariableValue;

import java.util.function.BinaryOperator;

public class OperatorNode implements ExpressionNode {
  private final ExpressionNode operandLeft;
  private final ExpressionNode operandRight;

  private final Type type;

  public OperatorNode(Type type, ExpressionNode operandLeft, ExpressionNode operandRight) {
    this.operandLeft = operandLeft;
    this.operandRight = operandRight;
    this.type = type;
  }

  @Override
  public double evaluate(MultiVariableValue multiVariableValue) {
    return type.function.apply(operandLeft.evaluate(multiVariableValue),
            operandRight.evaluate(multiVariableValue));
  }

  public enum Type {
    SUM(Double::sum),
    SUBTRACTION((a, b) -> a - b),
    MULTIPLICATION((a, b) -> a * b),
    DIVISION((a, b) -> a / b),
    POWER(Math::pow);

    private final BinaryOperator<Double> function;

    Type(BinaryOperator<Double> function) {
      this.function = function;
    }

  }
}
