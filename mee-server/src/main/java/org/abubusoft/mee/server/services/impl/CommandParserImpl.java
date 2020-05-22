package org.abubusoft.mee.server.services.impl;


import org.abubusoft.mee.server.exceptions.AppAssert;
import org.abubusoft.mee.server.exceptions.MalformedCommandException;
import org.abubusoft.mee.server.grammar.CommandsBaseListener;
import org.abubusoft.mee.server.grammar.CommandsParser;
import org.abubusoft.mee.server.model.Command;
import org.abubusoft.mee.server.model.ComputeCommand;
import org.abubusoft.mee.server.model.compute.VariableDefinition;
import org.abubusoft.mee.server.services.CommandParser;
import org.abubusoft.mee.server.services.ExpressionEvaluator;
import org.abubusoft.mee.server.support.CommandVisitor;
import org.abubusoft.mee.server.support.ParserRuleContextBuilder;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class CommandParserImpl implements CommandParser {

  @Autowired
  public CommandParserImpl(ExpressionEvaluator expressionEvaluator) {
    this.expressionEvaluator = expressionEvaluator;
  }

  private ExpressionEvaluator expressionEvaluator;

  public Command parse(String input) throws MalformedCommandException {
    if (!StringUtils.hasText(input)) {
      AppAssert.failWithMalformedException("No command specified.");
    }
    ParserRuleContext parser = ParserRuleContextBuilder.build(input, CommandsParser::command);
    // check syntax errors
    ParseTreeWalker.DEFAULT.walk(new CommandsBaseListener(), ParserRuleContextBuilder.build(input, CommandsParser::parse));
    CommandVisitor visitor = new CommandVisitor(expressionEvaluator);
    return visitor.visit(parser);
  }

  public VariableDefinition parseVariableDefinition(String variableName, String input) throws MalformedCommandException {
    ParserRuleContext parser = ParserRuleContextBuilder.build(input, CommandsParser::variable_values);
    CommandVisitor visitor = new CommandVisitor(expressionEvaluator);
    visitor.visit(parser);
    ComputeCommand command = visitor.getComputeBuilder().build();
    return command.getVariableDefinition(variableName);
  }

}
