package org.abubusoft.mee.server.support;

import org.abubusoft.mee.server.exceptions.AppAssert;
import org.abubusoft.mee.server.exceptions.UndefinedVariableException;
import org.abubusoft.mee.server.grammar.CommandsBaseVisitor;
import org.abubusoft.mee.server.grammar.CommandsParser;
import org.abubusoft.mee.server.model.compute.VariableValues;

import java.util.List;

public class ExpressionVariableCheckerVisitor extends CommandsBaseVisitor<Void> {
  private final VariableValues variableValues;
  private String expression;

  public ExpressionVariableCheckerVisitor(VariableValues variableValues) {
    this.variableValues = variableValues;
  }

  @Override
  public Void visitExpression(CommandsParser.ExpressionContext ctx) {
    expression = ctx.getText();
    List<CommandsParser.Mul_expressionContext> operandList = ctx.mul_expression();
    List<CommandsParser.Operator_add_subContext> operatorList = ctx.operator_add_sub();

    visit(operandList.get(0));
    for (int i = 0; i < operatorList.size(); i++) {
      visit(operandList.get(i + 1));
    }
    return null;
  }

  @Override
  public Void visitVariable(CommandsParser.VariableContext ctx) {
    String name = ctx.getText();

    Double value = variableValues.get(name);
    if (value == null) {
      AppAssert.fail(UndefinedVariableException.class, "Undefined variable '%s' used in '%s'", name, expression);
    }

    return null;
  }

}
