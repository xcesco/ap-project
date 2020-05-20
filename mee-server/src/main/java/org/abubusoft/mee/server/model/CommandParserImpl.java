package org.abubusoft.mee.server.model;


import org.abubusoft.mee.server.exceptions.AppAssert;
import org.abubusoft.mee.server.exceptions.MalformedCommandException;
import org.abubusoft.mee.server.grammar.CommandsBaseListener;
import org.abubusoft.mee.server.grammar.CommandsParser;
import org.abubusoft.mee.server.grammar.ExpressionVisitor;
import org.abubusoft.mee.server.grammar.ParserRuleContextBuilder;
import org.abubusoft.mee.server.model.commands.Command;
import org.abubusoft.mee.server.model.commands.CommandVisitor;
import org.abubusoft.mee.server.model.commands.ComputeCommand;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class CommandParserImpl implements CommandParser {

  public Command parse(String input) throws MalformedCommandException {
    if (!StringUtils.hasText(input)) {
      AppAssert.failWithMalformedException("No command specified.");
    }
    ParserRuleContext parser = ParserRuleContextBuilder.build(input, CommandsParser::command);
    // check syntax errors
    ParseTreeWalker.DEFAULT.walk(new CommandsBaseListener(), ParserRuleContextBuilder.build(input, CommandsParser::parse));
    CommandVisitor visitor = new CommandVisitor();
    return visitor.visit(parser);
  }

  VariableDefinition parseVariableDefinition(String variableName, String input) throws MalformedCommandException {
    ParserRuleContext parser = ParserRuleContextBuilder.build(input, CommandsParser::variable_values);
    CommandVisitor visitor = new CommandVisitor();
    visitor.visit(parser);
    ComputeCommand command = visitor.getComputeBuilder().build();
    return command.getVariableDefinition(variableName);
  }

  double evaluateExpression(VariableValues variableValues, String input) throws MalformedCommandException {
    ParserRuleContext parser = ParserRuleContextBuilder.build(input, CommandsParser::expression);
    ExpressionVisitor visitor = new ExpressionVisitor(variableValues);
    return visitor.visit(parser);
  }


}
