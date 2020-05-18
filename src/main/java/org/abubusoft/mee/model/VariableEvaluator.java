package org.abubusoft.mee.model;

import org.abubusoft.mee.exceptions.MalformedCommandException;
import org.abubusoft.mee.grammar.CommandsBaseListener;
import org.abubusoft.mee.grammar.CommandsParser;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class VariableEvaluator implements Evaluator<VariableDefinitionContext, VariableDefinition> {
  public VariableDefinition execute(VariableDefinitionContext context, String input) throws MalformedCommandException {
    final VariableDefinition.Builder builder = VariableDefinition.Builder.create();
    ParseTreeWalker.DEFAULT.walk(new CommandsBaseListener() {
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

  public VariableDefinition execute(String input) throws MalformedCommandException {
    return execute(new VariableDefinitionContext(), input);
  }
}
