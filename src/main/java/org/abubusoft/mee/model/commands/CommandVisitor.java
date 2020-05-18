package org.abubusoft.mee.model.commands;

import org.abubusoft.mee.grammar.CommandsBaseVisitor;
import org.abubusoft.mee.grammar.CommandsParser;
import org.abubusoft.mee.model.Command;

public class CommandVisitor extends CommandsBaseVisitor<Command> {
  private final CommandContext context;

  public CommandVisitor(CommandContext context) {
    this.context = context;
  }

  @Override
  public Command visitQuite_command(CommandsParser.Quite_commandContext ctx) {
    return new QuitCommand();
  }

  @Override
  public Command visitComputation_command(CommandsParser.Computation_commandContext ctx) {
    visit(ctx.computation_kind());
    visit(ctx.values_kind());
    visit(ctx.expressions());
    return context.getComputeBuilder().build();
  }

  @Override
  public Command visitComputation_kind(CommandsParser.Computation_kindContext ctx) {
    ComputationType value=ComputationType.valueOf(ctx.getText());
    context.getComputeBuilder().setComputationType(value);
    return null;
  }

  @Override
  public Command visitValues_kind(CommandsParser.Values_kindContext ctx) {
    ValuesType value=ValuesType.valueOf(ctx.getText());
    context.getComputeBuilder().setValuesType(value).build();
    return null;
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
