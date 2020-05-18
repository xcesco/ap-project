package org.abubusoft.mee.model;

import org.abubusoft.mee.exceptions.AppAssert;
import org.abubusoft.mee.exceptions.AppRuntimeException;
import org.abubusoft.mee.exceptions.MalformedCommandException;
import org.abubusoft.mee.grammar.CommandBaseErrorListener;
import org.abubusoft.mee.grammar.CommandsLexer;
import org.abubusoft.mee.grammar.CommandsParser;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.Pair;

import java.util.BitSet;

public interface Evaluator<C, E> {
  E execute(C context, String input) throws MalformedCommandException;

  static Pair<ParserRuleContext, CommonTokenStream> buildParser(final String input, final VariableEvaluator.RuleChooser chooser) throws MalformedCommandException {
    CommandsLexer lexer = new CommandsLexer(CharStreams.fromString(input));
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    CommandsParser parser = new CommandsParser(tokens);

    parser.removeErrorListeners();
    parser.addErrorListener(new CommandBaseErrorListener() {
      @Override
      public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
                              int charPositionInLine, String msg, RecognitionException e) {
        AppAssert.fail("unespected char at pos %s in command '%s'", charPositionInLine, input);
      }
    });

    try {
      ParserRuleContext context = chooser.choose(parser);
      return new Pair<>(context, tokens);
    } catch (AppRuntimeException e) {
      throw new MalformedCommandException(e.getMessage());
    }

  }

  interface RuleChooser {
    ParserRuleContext choose(CommandsParser parser) throws MalformedCommandException;
  }
}
