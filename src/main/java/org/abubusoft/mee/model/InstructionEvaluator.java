package org.abubusoft.mee.model;

import org.abubusoft.mee.grammar.CommandListener;
import org.abubusoft.mee.grammars.CommandsParser;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class InstructionEvaluator implements Evaluator<InstructionContext, Void> {
  public Void execute(InstructionContext context, String input) {
    ParseTreeWalker.DEFAULT.walk(new CommandListener() {

    }, Evaluator.buildParser(input, CommandsParser::parse).a);

    return null;
  }

  public void execute(String input) {
    execute(new InstructionContext(), input);
  }
}
