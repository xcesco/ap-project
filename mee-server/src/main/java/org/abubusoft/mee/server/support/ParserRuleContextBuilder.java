package org.abubusoft.mee.server.support;

import org.abubusoft.mee.server.exceptions.AppAssert;
import org.abubusoft.mee.server.exceptions.MalformedCommandException;
import org.abubusoft.mee.server.grammar.CommandsLexer;
import org.abubusoft.mee.server.grammar.CommandsParser;
import org.antlr.v4.runtime.*;

public abstract class ParserRuleContextBuilder {
  private ParserRuleContextBuilder() {
  }

  public interface RuleChooser {
    ParserRuleContext choose(CommandsParser parser);
  }

  public static ParserRuleContext build(final String input, final RuleChooser chooser) {
    CommandsLexer lexer = new CommandsLexer(CharStreams.fromString(input));
    lexer.removeErrorListeners();
    lexer.addErrorListener(new BaseErrorListener() {
      @Override
      public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        AppAssert.fail(MalformedCommandException.class, "Unespected char at pos %s", charPositionInLine);
      }
    });

    CommonTokenStream tokens = new CommonTokenStream(lexer);
    CommandsParser parser = new CommandsParser(tokens);

    // to avoid display error on System.err
    parser.setErrorHandler(new AppErrorStrategy());
    parser.removeErrorListeners();
    parser.addErrorListener(new BaseErrorListener() {
      @Override
      public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
                              int charPositionInLine, String msg, RecognitionException e) {
        AppAssert.fail(MalformedCommandException.class, "Unespected char at pos %s", charPositionInLine);
      }
    });

    return chooser.choose(parser);
  }
}
