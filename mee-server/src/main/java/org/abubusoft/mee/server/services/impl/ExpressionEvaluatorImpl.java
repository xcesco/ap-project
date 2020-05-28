package org.abubusoft.mee.server.services.impl;

import org.abubusoft.mee.server.exceptions.AppRuntimeException;
import org.abubusoft.mee.server.grammar.CommandsParser;
import org.abubusoft.mee.server.model.compute.MultiVariableValue;
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

  public double evaluate(MultiVariableValue multiVariableValue, String expression) {
    try {
      ParserRuleContext parser = ParserRuleContextBuilder.build(expression, CommandsParser::evaluate);
      ExpressionVisitor visitor = new ExpressionVisitor(multiVariableValue, expression);
      double value = visitor.visit(parser);
      logger.trace(String.format("'%s' with %s = %s", expression, multiVariableValue, CommandResponseUtils.formatValue(value)));
      return value;
    } catch (AppRuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new AppRuntimeException(e.getMessage());
    }
  }
}
