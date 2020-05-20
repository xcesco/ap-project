package org.abubusoft.mee.server.services;

import org.abubusoft.mee.server.exceptions.MalformedCommandException;
import org.abubusoft.mee.server.grammar.CommandsParser;
import org.abubusoft.mee.server.grammar.ExpressionVisitor;
import org.abubusoft.mee.server.grammar.ParserRuleContextBuilder;
import org.abubusoft.mee.server.model.VariableValues;
import org.antlr.v4.runtime.ParserRuleContext;

public class ExpressionEvaluator {
  public double execute(VariableValues variableValues, String input) throws MalformedCommandException {
    ParserRuleContext parser = ParserRuleContextBuilder.build(input, CommandsParser::expression);
    ExpressionVisitor visitor = new ExpressionVisitor(variableValues);
    return visitor.visit(parser);
  }
}
