package org.abubusoft.mee.server.support;

import org.abubusoft.mee.server.exceptions.AppAssert;
import org.abubusoft.mee.server.exceptions.EvaluationExpressionException;
import org.abubusoft.mee.server.exceptions.UndefinedVariableException;
import org.abubusoft.mee.server.grammar.CommandsBaseVisitor;
import org.abubusoft.mee.server.grammar.CommandsParser;
import org.abubusoft.mee.server.model.compute.MultiVariableValue;

import java.util.List;

public class ExpressionVisitor extends CommandsBaseVisitor<Double> {
  private final MultiVariableValue multiVariableValue;
  private final String expression;

  public ExpressionVisitor(MultiVariableValue multiVariableValue, String expression) {
    this.expression = expression;
    this.multiVariableValue = multiVariableValue;
  }

  @Override
  public Double visitEvaluate(CommandsParser.EvaluateContext ctx) {
    return visitExpression(ctx.expression());
  }

  @Override
  public Double visitExpression(CommandsParser.ExpressionContext ctx) {
    CommandsParser.VariableContext varContext = ctx.variable();
    CommandsParser.NumberContext numContext = ctx.number();
    List<CommandsParser.ExpressionContext> expr = ctx.expression();
    CommandsParser.OperatorContext operator = ctx.operator();

    double result = 0;

    if (varContext != null) {
      result = visitVariable(varContext);
    } else if (numContext != null) {
      result = visitNumber(numContext);
    } else {
      double operand1 = visit(expr.get(0));
      double operand2 = visit(expr.get(1));

      if (operator.OP_ADD() != null) {
        result = operand1 + operand2;
      } else if (operator.OP_MINUS() != null) {
        result = operand1 - operand2;
      } else if (operator.OP_MUL() != null) {
        result = operand1 * operand2;
      } else if (operator.OP_DIV() != null) {
        result = operand1 / operand2;
      } else if (operator.OP_POW() != null) {
        result = Math.pow(operand1, operand2);
      } else {
        AppAssert.fail(EvaluationExpressionException.class, "Inconsistent status");
      }
    }
    return result;
  }

  @Override
  public Double visitVariable(CommandsParser.VariableContext ctx) {
    String name = ctx.getText();

    Double value = multiVariableValue.get(name);
    if (value == null) {
      AppAssert.fail(UndefinedVariableException.class, "Undefined variable '%s' is used in expression '%s'", name, expression);
    }
    return value;
  }

  @Override
  public Double visitNumber(CommandsParser.NumberContext ctx) {
    return Double.parseDouble(ctx.getText());
  }
}
