package org.abubusoft.mee.server.support;

import org.abubusoft.mee.server.exceptions.AppAssert;
import org.abubusoft.mee.server.exceptions.AppRuntimeException;
import org.abubusoft.mee.server.exceptions.MalformedCommandException;
import org.abubusoft.mee.server.grammar.CommandsLexer;
import org.abubusoft.mee.server.grammar.CommandsParser;
import org.antlr.v4.runtime.*;

public abstract class ParserRuleContextBuilder {
  public interface RuleChooser {
    ParserRuleContext choose(CommandsParser parser) throws MalformedCommandException;
  }

  public static ParserRuleContext build(final String input, final RuleChooser chooser) throws MalformedCommandException {
    CommandsLexer lexer = new CommandsLexer(CharStreams.fromString(input));
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    CommandsParser parser = new CommandsParser(tokens);

    parser.removeErrorListeners();
    parser.addErrorListener(new CommandBaseErrorListener() {
      @Override
      public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
                              int charPositionInLine, String msg, RecognitionException e) {
        AppAssert.fail("Unespected char at pos %s", charPositionInLine);
      }
    });

    try {
      ParserRuleContext context = chooser.choose(parser);
      return context;
    } catch (AppRuntimeException e) {
      throw new MalformedCommandException(e.getMessage());
    }

  }
}
