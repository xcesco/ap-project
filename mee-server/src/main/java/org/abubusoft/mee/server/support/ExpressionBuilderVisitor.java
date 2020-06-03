package org.abubusoft.mee.server.support;

import org.abubusoft.mee.server.exceptions.AppAssert;
import org.abubusoft.mee.server.exceptions.EvaluationExpressionException;
import org.abubusoft.mee.server.exceptions.UndefinedVariableException;
import org.abubusoft.mee.server.grammar.CommandsBaseVisitor;
import org.abubusoft.mee.server.grammar.CommandsParser;
import org.abubusoft.mee.server.model.compute.MultiVariableValue;
import org.abubusoft.mee.server.model.compute.expression.*;

import java.util.List;

public class ExpressionBuilderVisitor extends CommandsBaseVisitor<ExpressionNode> {
  private final MultiVariableValue multiVariableValue;
  private final String expression;

  public ExpressionBuilderVisitor(MultiVariableValue multiVariableValue, String expression) {
    this.expression = expression;
    this.multiVariableValue = multiVariableValue;
  }

  @Override
  public ExpressionNode visitEvaluate(CommandsParser.EvaluateContext ctx) {
    return visitExpression(ctx.expression());
  }

  @Override
  public ExpressionNode visitExpression(CommandsParser.ExpressionContext ctx) {
    CommandsParser.VariableContext varContext = ctx.variable();
    CommandsParser.NumberContext numContext = ctx.number();
    List<CommandsParser.ExpressionContext> expr = ctx.expression();
    CommandsParser.OperatorContext operator = ctx.operator();

    ExpressionNode result = null;

    if (varContext != null) {
      result = visitVariable(varContext);
    } else if (numContext != null) {
      result = visitNumber(numContext);
    } else {
      ExpressionNode operandLeft = visit(expr.get(0));
      ExpressionNode operandRight = visit(expr.get(1));

      if (operator.OP_ADD() != null) {
        result = new OperatorNode(OperatorNode.Type.SUM, operandLeft, operandRight);
      } else if (operator.OP_MINUS() != null) {
        result = new OperatorNode(OperatorNode.Type.SUBTRACTION, operandLeft, operandRight);
      } else if (operator.OP_MUL() != null) {
        result = new OperatorNode(OperatorNode.Type.MULTIPLICATION, operandLeft, operandRight);
      } else if (operator.OP_DIV() != null) {
        result = new OperatorNode(OperatorNode.Type.DIVISION, operandLeft, operandRight);
      } else if (operator.OP_POW() != null) {
        result = new OperatorNode(OperatorNode.Type.POWER, operandLeft, operandRight);
      } else {
        AppAssert.fail(EvaluationExpressionException.class, "Inconsistent status");
      }
    }
    return result;
  }

  @Override
  public ExpressionNode visitVariable(CommandsParser.VariableContext ctx) {
    String name = ctx.getText();

    Double value = multiVariableValue.getVariableValue(name);
    if (value == null) {
      AppAssert.fail(UndefinedVariableException.class, "Undefined variable '%s' is used in expression '%s'", name, expression);
    }
    return new VariableNode(name);
  }

  @Override
  public ExpressionNode visitNumber(CommandsParser.NumberContext ctx) {
    return new ConstantNode(Double.parseDouble(ctx.getText()));
  }
}
