package org.abubusoft.mee.server.model;

import org.abubusoft.mee.server.exceptions.AppRuntimeException;
import org.abubusoft.mee.server.exceptions.MalformedCommandException;
import org.abubusoft.mee.server.services.ExpressionEvaluator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExpressionWithVariablesDefinitionParserImplTest {
  ExpressionEvaluator evaluator = new ExpressionEvaluator();

  @Test
  public void testNumbersEvaluation() throws MalformedCommandException {
    Assertions.assertThrows(AppRuntimeException.class, () -> evaluateExpression("x5", 1.0, 0, 1.0));
    evaluateExpression("x", 1.0, 0, 1.0);
    evaluateExpression("-x", 2.0, 0, -2);
    evaluateExpression("1e2*y1", 0, 2, 200.0);
    evaluateExpression("x+y1", 1, 2, 3.0);
  }

  @Test
  public void testNumbersSumSubEvaluation() throws MalformedCommandException {
    evaluateExpression("x+1", 1, 0, 2.0);
    evaluateExpression("-1+y1", 0, 0, -1.0);
    evaluateExpression("-1+x+2+y1", 1, 2, 4.0);
  }

  @Test
  public void testNumbersMulDivEvaluation() throws MalformedCommandException {
    evaluateExpression("x*y1", 2, 4, 8.0);
    evaluateExpression("-x*2", 4, 0, -8.0);
    evaluateExpression("x/1", 2, 0, 2.0);
    evaluateExpression("8/x/2", 2, 0, 2.0);

    Assertions.assertThrows(AppRuntimeException.class, () -> evaluateExpression("2/x", 0, 0, 2.0));
  }

  @Test
  public void testNumbers4OpsEvaluation() throws MalformedCommandException {
    evaluateExpression("1+x*2", 2, 0, 5.0);
    evaluateExpression("(1+y1)*2", 0, 2, 6.0);
    evaluateExpression("-(x+y1)*2", 1, 2, -6.0);
    evaluateExpression("(-1+x)*y1", 2, 3, 3.0);
  }

  @Test
  public void testNumbersPowEvaluation() throws MalformedCommandException {
    evaluateExpression("x^y1", 2, 1, 2.0);
    evaluateExpression("y1^x^2", 2, 2, 16.0);
    evaluateExpression("2^y1*y1", 0, 2, 8.0);
  }

  @Test
  public void testMixEvaluation() throws MalformedCommandException {
    evaluateExpression("(1+x)^2", 1, 1, 4.0);
    evaluateExpression("(y1+1)^2-1", 1, 1, 3.0);
  }

  private void evaluateExpression(String input, double variableValueX, double variableValueY1, double aspectedValue) throws MalformedCommandException {
    double evaluationResult = evaluator.execute(VariableValues.Builder.create()
            .add("x", variableValueX)
            .add("y1", variableValueY1)
            .build(), input);
    assertEquals(aspectedValue, evaluationResult, input + "=" + aspectedValue);
  }

}