package org.abubusoft.mee.server.services.impl;

import org.abubusoft.mee.server.exceptions.AppRuntimeException;
import org.abubusoft.mee.server.grammar.CommandsParser;
import org.abubusoft.mee.server.model.compute.VariablesValue;
import org.abubusoft.mee.server.services.ExpressionEvaluatorService;
import org.abubusoft.mee.server.support.CommandResponseUtils;
import org.abubusoft.mee.server.support.ExpressionVariableCheckerVisitor;
import org.abubusoft.mee.server.support.ExpressionVisitor;
import org.abubusoft.mee.server.support.ParserRuleContextBuilder;
import org.antlr.v4.runtime.ParserRuleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ExpressionEvaluatorServiceImpl implements ExpressionEvaluatorService {
  private static final Logger logger = LoggerFactory
          .getLogger(ExpressionEvaluatorServiceImpl.class);

  public double evaluate(VariablesValue variablesValue, String expression) {
    try {
      ParserRuleContext parser = ParserRuleContextBuilder.build(expression, CommandsParser::evaluate);
      ExpressionVisitor visitor = new ExpressionVisitor(variablesValue, expression);
      double value = visitor.visit(parser);
      logger.trace(String.format("'%s' with %s = %s", expression, variablesValue, CommandResponseUtils.formatValue(value)));
      return value;
    } catch (AppRuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new AppRuntimeException(e.getMessage());
    }
  }

  public void validate(VariablesValue variablesValue, String input) {
    try {
      ParserRuleContext parser = ParserRuleContextBuilder.build(input, CommandsParser::evaluate);
      ExpressionVariableCheckerVisitor visitor = new ExpressionVariableCheckerVisitor(variablesValue);
      visitor.visit(parser);
    } catch (AppRuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new AppRuntimeException(e.getMessage());
    }
  }
}
