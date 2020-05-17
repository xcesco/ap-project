package org.abubusoft.mee.model;

import org.abubusoft.mee.grammar.CommandListener;
import org.abubusoft.mee.grammars.CommandsParser;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class CommandAnalyzer implements Analyzer<Void> {
  public Void execute(String input) {
    ParseTreeWalker.DEFAULT.walk(new CommandListener() {

    }, Analyzer.buildParser(input, CommandsParser::parse).a);

    return null;
  }
}
