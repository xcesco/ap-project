package org.abubusoft.mee.server.services.impl;

import org.abubusoft.mee.server.exceptions.MalformedCommandException;
import org.abubusoft.mee.server.grammar.CommandsParser;
import org.abubusoft.mee.server.model.compute.VariableValues;
import org.abubusoft.mee.server.services.ExpressionEvaluator;
import org.abubusoft.mee.server.support.CommandResponseUtils;
import org.abubusoft.mee.server.support.ExpressionVisitor;
import org.abubusoft.mee.server.support.ParserRuleContextBuilder;
import org.antlr.v4.runtime.ParserRuleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ExpressionEvaluatorImpl implements ExpressionEvaluator {
  private static final Logger logger = LoggerFactory
          .getLogger(ExpressionEvaluatorImpl.class);

  public double execute(VariableValues variableValues, String input) throws MalformedCommandException {
    try {
      ParserRuleContext parser = ParserRuleContextBuilder.build(input, CommandsParser::expression);
      ExpressionVisitor visitor = new ExpressionVisitor(variableValues);
      double value = visitor.visit(parser);
      logger.trace(String.format("'%s' with %s = %s", input, variableValues, CommandResponseUtils.formatValue(value)));
      return value;
    } catch (Exception e) {
      logger.error(String.format("'%s' with %s raised error '%s'", input, variableValues, e.getMessage()));
      throw e;
    }
  }
}
