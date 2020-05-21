package org.abubusoft.mee.server.support;

import org.abubusoft.mee.server.grammar.CommandsBaseVisitor;
import org.abubusoft.mee.server.grammar.CommandsParser;
import org.abubusoft.mee.server.model.compute.VariableValues;

import java.util.List;
import java.util.stream.Collectors;

public class ExpressionsVisitor extends CommandsBaseVisitor<List<Double>> {
  private final VariableValues variableValues;

  public ExpressionsVisitor(VariableValues variableValues) {
    this.variableValues = variableValues;
  }

  @Override
  public List<Double> visitExpressions(CommandsParser.ExpressionsContext ctx) {
    return ctx.expression().stream().map(item -> (new ExpressionVisitor(variableValues))
            .visit(item))
            .collect(Collectors.toList());
  }

}
