package org.abubusoft.mee.server.support;

import org.abubusoft.mee.server.exceptions.AppAssert;
import org.abubusoft.mee.server.exceptions.InvalidVariableDefinitionException;
import org.abubusoft.mee.server.grammar.CommandsBaseVisitor;
import org.abubusoft.mee.server.grammar.CommandsParser;
import org.abubusoft.mee.server.model.Command;
import org.abubusoft.mee.server.model.ComputeCommand;
import org.abubusoft.mee.server.model.QuitCommand;
import org.abubusoft.mee.server.model.StatCommand;
import org.abubusoft.mee.server.model.compute.ComputationType;
import org.abubusoft.mee.server.model.compute.ValuesType;
import org.abubusoft.mee.server.model.compute.VariableDefinition;
import org.abubusoft.mee.server.model.stat.StatType;
import org.abubusoft.mee.server.services.ExpressionEvaluatorService;
import org.antlr.v4.runtime.RuleContext;

import java.util.stream.Collectors;

public class CommandVisitor extends CommandsBaseVisitor<Command> {
  private final ComputeCommand.Builder computeBuilder;

  public CommandVisitor(ExpressionEvaluatorService expressionEvaluatorService) {
    computeBuilder = ComputeCommand.Builder.create(expressionEvaluatorService);
  }

  public ComputeCommand.Builder getComputeBuilder() {
    return computeBuilder;
  }

  @Override
  public Command visitQuite_command(CommandsParser.Quite_commandContext ctx) {
    return new QuitCommand();
  }

  @Override
  public Command visitComputation_command(CommandsParser.Computation_commandContext ctx) {
    visit(ctx.computation_kind());
    visit(ctx.values_kind());
    visit(ctx.variable_values_function());
    visit(ctx.expressions());
    return computeBuilder.build();
  }

  @Override
  public Command visitComputation_kind(CommandsParser.Computation_kindContext ctx) {
    ComputationType value = ComputationType.valueOf(ctx.getText());
    computeBuilder.setComputationType(value);
    return null;
  }

  @Override
  public Command visitVariable_values(CommandsParser.Variable_valuesContext ctx) {
    try {
      computeBuilder.addVariableDefinition(VariableDefinition.Builder.create()
              .setName(ctx.variable().getText())
              .setInterval(
                      Double.parseDouble(ctx.variable_lower_value().getText()),
                      Double.parseDouble(ctx.variable_step_value().getText()),
                      Double.parseDouble(ctx.variable_upper_value().getText())).build());
    } catch (NumberFormatException e) {
      AppAssert.fail(InvalidVariableDefinitionException.class, "%s definition contains invalid char", ctx.variable().getText());
    }
    return null;
  }

  @Override
  public Command visitValues_kind(CommandsParser.Values_kindContext ctx) {
    ValuesType value = ValuesType.valueOf(ctx.getText());
    computeBuilder.setValuesType(value).build();
    return null;
  }

  @Override
  public Command visitVariable_values_function(CommandsParser.Variable_values_functionContext ctx) {
    return super.visitVariable_values_function(ctx);
  }

  @Override
  public Command visitExpressions(CommandsParser.ExpressionsContext ctx) {
    computeBuilder.setExpressionsList(ctx.expression().stream().map(RuleContext::getText).collect(Collectors.toList()));
    return null;
  }

  @Override
  public Command visitExpression(CommandsParser.ExpressionContext ctx) {
    return super.visitExpression(ctx);
  }

  @Override
  public Command visitStat_reqs_command(CommandsParser.Stat_reqs_commandContext ctx) {
    return new StatCommand(StatType.REQS);
  }

  @Override
  public Command visitStat_avg_time_command(CommandsParser.Stat_avg_time_commandContext ctx) {
    return new StatCommand(StatType.AVG_TIME);
  }

  @Override
  public Command visitStat_max_time_command(CommandsParser.Stat_max_time_commandContext ctx) {
    return new StatCommand(StatType.MAX_TIME);
  }

  @Override
  public Command visitStat_min_time_command(CommandsParser.Stat_min_time_commandContext ctx) {
    return new StatCommand(StatType.MIN_TIME);
  }
}
