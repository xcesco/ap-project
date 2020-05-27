package org.abubusoft.mee.server.support;

import org.abubusoft.mee.server.exceptions.AppAssert;
import org.abubusoft.mee.server.exceptions.EvaluationExpressionException;
import org.abubusoft.mee.server.exceptions.UndefinedVariableException;
import org.abubusoft.mee.server.grammar.CommandsBaseVisitor;
import org.abubusoft.mee.server.grammar.CommandsParser;
import org.abubusoft.mee.server.model.compute.VariablesValue;

import java.util.List;

public class ExpressionVisitor extends CommandsBaseVisitor<Double> {
  private final VariablesValue variablesValue;
  private final String expression;

  public ExpressionVisitor(VariablesValue variablesValue, String expression) {
    this.expression = expression;
    this.variablesValue = variablesValue;
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
        AppAssert.assertTrue(operand2 != 0.0, EvaluationExpressionException.class, "Division by 0 in '%s' with %s", expression, this.variablesValue.toString());
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

    Double value = variablesValue.get(name);
    if (value == null) {
      AppAssert.fail(UndefinedVariableException.class, "Undefined variable '%s' used in '%s'", name, expression);
    }
    return value;
  }

  @Override
  public Double visitNumber(CommandsParser.NumberContext ctx) {
    return Double.parseDouble(ctx.getText());
  }
}
