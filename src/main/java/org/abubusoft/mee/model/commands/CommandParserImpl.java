package org.abubusoft.mee.model.commands;

import org.abubusoft.mee.exceptions.MalformedCommandException;
import org.abubusoft.mee.grammar.CommandsBaseListener;
import org.abubusoft.mee.grammar.CommandsParser;
import org.abubusoft.mee.model.Command;
import org.abubusoft.mee.model.CommandParser;
import org.abubusoft.mee.model.Evaluator;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.springframework.stereotype.Component;

@Component
public class CommandParserImpl implements CommandParser {
  public Command parse(String input) throws MalformedCommandException  {
    Pair<ParserRuleContext, CommonTokenStream> parser = Evaluator.buildParser(input, CommandsParser::command);

    ParseTreeWalker.DEFAULT.walk(new CommandsBaseListener(), Evaluator.buildParser(input, CommandsParser::parse).a);

    CommandVisitor visitor = new CommandVisitor(new CommandContext());
    return visitor.visit(parser.a);
  }

}
