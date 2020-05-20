package org.abubusoft.mee.model.commands;

import org.abubusoft.mee.grammar.CommandsBaseVisitor;
import org.abubusoft.mee.grammar.CommandsParser;
import org.abubusoft.mee.model.VariableDefinition;
import org.antlr.v4.runtime.RuleContext;

import java.util.stream.Collectors;

public class CommandVisitor extends CommandsBaseVisitor<Command> {
  private final ComputeCommand.Builder computeBuilder;

  public CommandVisitor() {
    computeBuilder = ComputeCommand.Builder.create();
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
    computeBuilder.addVariableDefinition(VariableDefinition.Builder.create()
            .setName(ctx.variable().getText())
            .setInterval(
                    Double.parseDouble(ctx.variable_lower_value().getText()),
                    Double.parseDouble(ctx.variable_step_value().getText()),
                    Double.parseDouble(ctx.variable_upper_value().getText())).build());
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
    //TODO variable check
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
}