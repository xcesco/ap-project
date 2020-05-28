package org.abubusoft.mee.server.services;

import org.abubusoft.mee.server.model.compute.MultiVariableValue;

public interface ExpressionEvaluator {
  double evaluate(MultiVariableValue multiVariableValue, String input);
}
