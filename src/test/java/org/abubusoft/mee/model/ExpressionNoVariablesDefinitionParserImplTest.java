package org.abubusoft.mee.model;

import org.abubusoft.mee.exceptions.AppRuntimeException;
import org.abubusoft.mee.exceptions.MalformedCommandException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExpressionNoVariablesDefinitionParserImplTest {
  ExpressionEvaluator evaluator = new ExpressionEvaluator();

  @Test
  public void testNumbersEvaluation() throws MalformedCommandException {
    evaluateExpression("1", 1.0);
    evaluateExpression("-2", -2.0);
    evaluateExpression("1e2", 100.0);
    evaluateExpression("1e-2", .01);
    evaluateExpression("1e-3", .001);
    evaluateExpression("-1e-3", -.001);
    evaluateExpression("20", 20.0);
    evaluateExpression("2e2", 2e2);
  }

  @Test
  public void testNumbersSumSubEvaluation() throws MalformedCommandException {
    evaluateExpression("1+1", 2.0);
    evaluateExpression("-1+1", .0);
    evaluateExpression("-1+1+2", 2.0);
  }

  @Test
  public void testNumbersMulDivEvaluation() throws MalformedCommandException {
    evaluateExpression("1*1", 1.0);
    evaluateExpression("-1*1", -1.0);
    evaluateExpression("2/1", 2.0);
    evaluateExpression("8/2/2", 2.0);

    Assertions.assertThrows(AppRuntimeException.class, () -> evaluateExpression("2/0", 2.0));
  }

  @Test
  public void testNumbers4OpsEvaluation() throws MalformedCommandException {
    evaluateExpression("1+2*2", 5.0);
    evaluateExpression("(1+2)*2", 6.0);
    evaluateExpression("-(1+2)*2", -6.0);
    evaluateExpression("(-1+2)*2", 2.0);
  }

  @Test
  public void testNumbersPowEvaluation() throws MalformedCommandException {
    evaluateExpression("2^1", 2.0);
    evaluateExpression("2^2^2", 16.0);
    evaluateExpression("2^2*2", 8.0);
  }

  @Test
  public void testMixEvaluation() throws MalformedCommandException {
    evaluateExpression("(1+1)^2", 4.0);
    evaluateExpression("(1+1)^2-1", 3.0);
  }

  private void evaluateExpression(String input, double aspectedValue) throws MalformedCommandException {
    double evaluationResult = evaluator.execute(new VariableValues(), input);
    assertEquals(aspectedValue, evaluationResult, input + "=" + aspectedValue);
  }

}