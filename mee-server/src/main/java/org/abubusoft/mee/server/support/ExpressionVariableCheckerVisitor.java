package org.abubusoft.mee.server.support;

import org.abubusoft.mee.server.exceptions.AppAssert;
import org.abubusoft.mee.server.exceptions.UndefinedVariableException;
import org.abubusoft.mee.server.grammar.CommandsBaseVisitor;
import org.abubusoft.mee.server.grammar.CommandsParser;
import org.abubusoft.mee.server.model.compute.ValuesTuple;

import java.util.List;

/**
 * Visit the expresion to check if all used variables are used.
 */
public class ExpressionVariableCheckerVisitor extends CommandsBaseVisitor<Void> {
  private final ValuesTuple valuesTuple;
  private String expression;

  public ExpressionVariableCheckerVisitor(ValuesTuple valuesTuple) {
    this.valuesTuple = valuesTuple;
  }

  @Override
  public Void visitExpression(CommandsParser.ExpressionContext ctx) {
    expression = ctx.getText();
    List<CommandsParser.Mul_expressionContext> operandList = ctx.mul_expression();
    operandList.stream().forEach(this::visit);
    return null;
  }

  @Override
  public Void visitVariable(CommandsParser.VariableContext ctx) {
    String name = ctx.getText();

    Double value = valuesTuple.get(name);
    if (value == null) {
      AppAssert.fail(UndefinedVariableException.class, "Undefined variable '%s' used in '%s'", name, expression);
    }

    return null;
  }

}
