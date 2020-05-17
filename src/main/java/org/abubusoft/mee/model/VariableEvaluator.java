package org.abubusoft.mee.model;

import org.abubusoft.mee.grammar.CommandListener;
import org.abubusoft.mee.grammars.CommandsParser;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class VariableEvaluator implements Evaluator<VariableDefinitionContext, VariableDefinition> {
  public VariableDefinition execute(VariableDefinitionContext context, String input) {
    final VariableDefinition.Builder builder = VariableDefinition.Builder.create();
    ParseTreeWalker.DEFAULT.walk(new CommandListener() {
      @Override
      public void enterVariable_values(CommandsParser.Variable_valuesContext ctx) {
        builder
                .setName(ctx.variable().getText())
                .setInterval(
                        ctx.variable_lower_value().getText(),
                        ctx.variable_step_value().getText(),
                        ctx.variable_upper_value().getText());
      }
    }, Evaluator.buildParser(input, CommandsParser::variable_values).a);

    return builder.build();
  }

  public VariableDefinition execute(String input) {
    return execute(new VariableDefinitionContext(), input);
  }
}
