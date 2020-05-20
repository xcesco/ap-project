package org.abubusoft.mee.model;

import org.abubusoft.mee.exceptions.MalformedCommandException;
import org.abubusoft.mee.grammar.CommandsParser;
import org.antlr.v4.runtime.ParserRuleContext;

public class ExpressionEvaluator {
  public double execute(VariableValues variableValues, String input) throws MalformedCommandException {
    ParserRuleContext parser = CommandParserImpl.buildParser(input, CommandsParser::expression);
    ExpressionVisitor visitor = new ExpressionVisitor(variableValues);
    return visitor.visit(parser);
  }
}
