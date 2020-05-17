package org.abubusoft.mee.model;

import org.abubusoft.mee.grammar.CommandBaseErrorListener;
import org.abubusoft.mee.grammars.CommandsLexer;
import org.abubusoft.mee.grammars.CommandsParser;
import org.abubusoft.mee.support.AssertMME;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.Pair;

public interface Evaluator<C, E> {
  E execute(C context, String input);

  static Pair<ParserRuleContext, CommonTokenStream> buildParser(final String input, final VariableEvaluator.RuleChooser chooser) {
    CommandsLexer lexer = new CommandsLexer(CharStreams.fromString(input));
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    CommandsParser parser = new CommandsParser(tokens);

    parser.removeErrorListeners();
    parser.addErrorListener(new CommandBaseErrorListener() {
      @Override
      public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
                              int charPositionInLine, String msg, RecognitionException e) {
        AssertMME.fail("unespected char at pos %s in command '%s'", charPositionInLine, input);
      }
    });

    ParserRuleContext context = chooser.choose(parser);
    return new Pair<>(context, tokens);
  }

  interface RuleChooser {
    ParserRuleContext choose(CommandsParser parser);
  }
}
