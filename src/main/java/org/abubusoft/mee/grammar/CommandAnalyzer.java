package org.abubusoft.mee.grammar;

import org.abubusoft.mee.grammars.CommandsLexer;
import org.abubusoft.mee.grammars.CommandsParser;
import org.abubusoft.mee.support.AssertMME;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class CommandAnalyzer {
  public static void execute(String input) {
    /** The walker. */
    ParseTreeWalker walker = new ParseTreeWalker();
    walker.walk(new CommandListener() {

    }, prepareParser(input).a);
  }

  protected static Pair<ParserRuleContext, CommonTokenStream> prepareParser(final String input) {
    CommandsLexer lexer = new CommandsLexer(CharStreams.fromString(input));
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    CommandsParser parser = new CommandsParser(tokens);

    parser.removeErrorListeners();
    parser.addErrorListener(new CommandBaseErrorListener() {
      @Override
      public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
                              int charPositionInLine, String msg, RecognitionException e) {
//        AssertKripton.assertTrue(false,
//                jqlContext.getContextDescription() + ": unespected char at pos %s of SQL '%s'",
//                charPositionInLine, jql);
        AssertMME.fail("unespected char at pos %s in command '%s'", charPositionInLine, input);
      }
    });

    ParserRuleContext context = parser.parse();
    return new Pair<>(context, tokens);
  }
}
