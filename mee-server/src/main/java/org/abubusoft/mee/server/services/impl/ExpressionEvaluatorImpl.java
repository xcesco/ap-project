package org.abubusoft.mee.server.services.impl;

import org.abubusoft.mee.server.exceptions.MalformedCommandException;
import org.abubusoft.mee.server.grammar.CommandsParser;
import org.abubusoft.mee.server.model.compute.VariableValues;
import org.abubusoft.mee.server.services.ExpressionEvaluator;
import org.abubusoft.mee.server.support.ExpressionVisitor;
import org.abubusoft.mee.server.support.ParserRuleContextBuilder;
import org.antlr.v4.runtime.ParserRuleContext;
import org.springframework.stereotype.Service;

@Service
public class ExpressionEvaluatorImpl implements ExpressionEvaluator {
  public double execute(VariableValues variableValues, String input) throws MalformedCommandException {
    ParserRuleContext parser = ParserRuleContextBuilder.build(input, CommandsParser::expression);
    ExpressionVisitor visitor = new ExpressionVisitor(variableValues);
    return visitor.visit(parser);
  }
}
