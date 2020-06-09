package org.abubusoft.mee.server.services.impl;


import org.abubusoft.mee.server.exceptions.AppAssert;
import org.abubusoft.mee.server.exceptions.MalformedCommandException;
import org.abubusoft.mee.server.grammar.CommandsBaseListener;
import org.abubusoft.mee.server.grammar.CommandsParser;
import org.abubusoft.mee.server.model.Command;
import org.abubusoft.mee.server.services.ClientRequestParser;
import org.abubusoft.mee.server.support.CommandVisitor;
import org.abubusoft.mee.server.support.ParserRuleContextBuilder;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ClientRequestParserImpl implements ClientRequestParser {

  @Override
  public Command parse(String request) {
    if (!StringUtils.hasText(request)) {
      AppAssert.fail(MalformedCommandException.class, "No command specified");
    }

    ParseTreeWalker.DEFAULT.walk(new CommandsBaseListener(), ParserRuleContextBuilder.build(request, CommandsParser::parse));
    ParserRuleContext parser = ParserRuleContextBuilder.build(request, CommandsParser::command);
    CommandVisitor visitor = new CommandVisitor();
    return visitor.visit(parser);
  }

}
