package org.abubusoft.mee.server.support;

import org.abubusoft.mee.server.exceptions.AppAssert;
import org.abubusoft.mee.server.exceptions.EvaluationExpressionException;
import org.abubusoft.mee.server.exceptions.UndefinedVariableException;
import org.abubusoft.mee.server.grammar.CommandsBaseVisitor;
import org.abubusoft.mee.server.grammar.CommandsParser;
import org.abubusoft.mee.server.model.compute.VariablesValue;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.List;

public class ExpressionVisitor extends CommandsBaseVisitor<Double> {
  private final VariablesValue variablesValue;
  private final String expression;

  public ExpressionVisitor(VariablesValue variablesValue, String expression) {
    this.expression=expression;
    this.variablesValue = variablesValue;
  }

  @Override
  public Double visitExpression(CommandsParser.ExpressionContext ctx) {
    List<CommandsParser.Mul_expressionContext> operandList = ctx.mul_expression();
    List<CommandsParser.Operator_add_subContext> operatorList = ctx.operator_add_sub();

    double result = visit(operandList.get(0));
    double value;
    for (int i = 0; i < operatorList.size(); i++) {
      value = visit(operandList.get(i + 1));
      if (operatorList.get(i).OP_ADD() != null) {
        result += value;
      } else {
        result -= value;
      }
    }
    return result;
  }

  @Override
  public Double visitOperand_left(CommandsParser.Operand_leftContext ctx) {
    double value = evaluateOperand(ctx.variable(), ctx.expression(), ctx.num());

    return ctx.OP_MINUS() != null ? -value : value;
  }

  @Override
  public Double visitOperand_right(CommandsParser.Operand_rightContext ctx) {
    return evaluateOperand(ctx.variable(), ctx.expression(), ctx.num());
  }

  private double evaluateOperand(CommandsParser.VariableContext variable, CommandsParser.ExpressionContext expression, CommandsParser.NumContext num) {
    double value;
    ParserRuleContext subContext = null;
    if (variable != null) {
      subContext = variable;
    } else if (expression != null) {
      subContext = expression;
    } else if (num != null) {
      subContext = num;
    } else {
      AppAssert.fail(EvaluationExpressionException.class, "Inconsistent status");
    }
    value = visit(subContext);

    return value;
  }

  @Override
  public Double visitMul_expression(CommandsParser.Mul_expressionContext ctx) {
    List<CommandsParser.Pow_expressionContext> operandList = ctx.pow_expression();
    List<CommandsParser.Operator_mul_divContext> operatorList = ctx.operator_mul_div();

    double result = visit(operandList.get(0));
    double value;
    for (int i = 0; i < operatorList.size(); i++) {
      value = visit(operandList.get(i + 1));
      if (operatorList.get(i).OP_MUL() != null) {
        result *= value;
      } else {
        AppAssert.assertTrue(value != 0.0, EvaluationExpressionException.class, "Division by 0 in '%s' with %s", expression, this.variablesValue.toString());
        result = result / value;
      }
    }

    return result;
  }

  @Override
  public Double visitPow_expression(CommandsParser.Pow_expressionContext ctx) {
    List<CommandsParser.Operand_rightContext> operandList = ctx.operand_right();
    List<CommandsParser.Operator_powContext> operatorList = ctx.operator_pow();

    double result = visit(ctx.operand_left());
    double value;
    for (int i = 0; i < operatorList.size(); i++) {
      value = visit(operandList.get(i));
      if (operatorList.get(i).OP_POW() != null) {
        result = Math.pow(result, value);
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
  public Double visitNum(CommandsParser.NumContext ctx) {
    return Double.parseDouble(ctx.getText());
  }
}
