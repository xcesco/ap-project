package org.abubusoft.mee.server.model.compute.expression;

import org.abubusoft.mee.server.exceptions.AppRuntimeException;
import org.abubusoft.mee.server.grammar.CommandsParser;
import org.abubusoft.mee.server.model.compute.Expression;
import org.abubusoft.mee.server.model.compute.MultiVariableValue;
import org.abubusoft.mee.server.support.ExpressionBuilderVisitor;
import org.abubusoft.mee.server.support.ParserRuleContextBuilder;
import org.antlr.v4.runtime.ParserRuleContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExpressionBuilderVisitorTest {
  @Test
  public void testBuild() {
    String expressionString = "(x0+1)";
    try {
      ParserRuleContext parser = ParserRuleContextBuilder.build(expressionString, CommandsParser::evaluate);
      MultiVariableValue multiVariableValue = MultiVariableValue.Builder
              .create()
              .add("x0", 1)
              .build();
      ExpressionBuilderVisitor visitor = new ExpressionBuilderVisitor(multiVariableValue, expressionString);
      ExpressionNode expressionNode = visitor.visit(parser);
      Expression expression = new Expression(expressionString, expressionNode);

      double value = expression.evaluate(multiVariableValue);
      assertEquals(2.0, value);
    } catch (AppRuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new AppRuntimeException(e.getMessage());
    }

  }
}
