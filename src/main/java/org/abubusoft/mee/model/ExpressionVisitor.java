package org.abubusoft.mee.model;

import org.abubusoft.mee.grammars.CommandsBaseVisitor;
import org.abubusoft.mee.grammars.CommandsParser;
import org.abubusoft.mee.support.AssertMME;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.List;

public class ExpressionVisitor extends CommandsBaseVisitor<Double> {
  private final ExpressionContext context;

  public ExpressionVisitor(ExpressionContext context) {
    this.context = context;
  }

  @Override
  public Double visitExpressions(CommandsParser.ExpressionsContext ctx) {
    return super.visitExpressions(ctx);
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
    double value = evaluateOperand(ctx.variable(), ctx.expression(), ctx.num());

    return value;
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
      AssertMME.fail("Incosistent status");
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
        AssertMME.assertTrue(value != 0.0, "Division by 0");
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

    Double value = context.getVariable(name);
    if (value == null) {
      AssertMME.fail("Variable %s is not defined", name);
    }
    return value;
  }

  @Override
  public Double visitNum(CommandsParser.NumContext ctx) {
    return Double.parseDouble(ctx.getText());
  }
}
