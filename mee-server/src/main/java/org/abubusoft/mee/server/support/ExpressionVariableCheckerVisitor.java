package org.abubusoft.mee.server.support;

import org.abubusoft.mee.server.exceptions.AppAssert;
import org.abubusoft.mee.server.exceptions.UndefinedVariableException;
import org.abubusoft.mee.server.grammar.CommandsParser;
import org.abubusoft.mee.server.grammar.CommandsBaseVisitor;
import org.abubusoft.mee.server.model.compute.VariablesValue;

/**
 * Visit the expresion to check if all used variables are used.
 */
public class ExpressionVariableCheckerVisitor extends CommandsBaseVisitor<Void> {
  private final VariablesValue variablesValue;
  private String expression;

  public ExpressionVariableCheckerVisitor(VariablesValue variablesValue) {
    this.variablesValue = variablesValue;
  }

  @Override
  public Void visitExpression(CommandsParser.ExpressionContext ctx) {
    expression = ctx.getText();

    if (ctx.variable() != null) {
      visit(ctx.variable());
    }
    return null;
  }

  @Override
  public Void visitVariable(CommandsParser.VariableContext ctx) {
    String name = ctx.getText();

    Double value = variablesValue.get(name);
    if (value == null) {
      AppAssert.fail(UndefinedVariableException.class, "Undefined variable '%s' used in '%s'", name, expression);
    }

    return null;
  }

}
