package org.abubusoft.mee.server.services.impl;


import org.abubusoft.mee.server.exceptions.AppAssert;
import org.abubusoft.mee.server.exceptions.MalformedCommandException;
import org.abubusoft.mee.server.grammar.CommandsBaseListener;
import org.abubusoft.mee.server.grammar.CommandsParser;
import org.abubusoft.mee.server.model.Command;
import org.abubusoft.mee.server.model.ComputeCommand;
import org.abubusoft.mee.server.model.compute.VariableValuesRange;
import org.abubusoft.mee.server.services.ClientRequestParser;
import org.abubusoft.mee.server.services.ExpressionEvaluator;
import org.abubusoft.mee.server.support.CommandVisitor;
import org.abubusoft.mee.server.support.ParserRuleContextBuilder;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ClientRequestParserImpl implements ClientRequestParser {

  @Autowired
  public ClientRequestParserImpl(ExpressionEvaluator expressionEvaluator) {
    this.expressionEvaluator = expressionEvaluator;
  }

  private final ExpressionEvaluator expressionEvaluator;

  public Command parse(String request) {
    if (!StringUtils.hasText(request)) {
      AppAssert.fail(MalformedCommandException.class, "No command specified");
    }

    ParseTreeWalker.DEFAULT.walk(new CommandsBaseListener(), ParserRuleContextBuilder.build(request, CommandsParser::parse));
    CommandVisitor visitor = new CommandVisitor(expressionEvaluator);
    ParserRuleContext parser = ParserRuleContextBuilder.build(request, CommandsParser::command);
    return visitor.visit(parser);
  }

  public VariableValuesRange parseVariableDefinition(String variableName, String request) {
    ParserRuleContext parser = ParserRuleContextBuilder.build(request, CommandsParser::variable_values);
    CommandVisitor visitor = new CommandVisitor(expressionEvaluator);
    visitor.visit(parser);
    ComputeCommand command = visitor.getComputeBuilder().build();
    return command.getVariableDefinition(variableName);
  }

}
