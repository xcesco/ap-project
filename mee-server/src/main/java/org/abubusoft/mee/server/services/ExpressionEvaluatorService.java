package org.abubusoft.mee.server.services;

import org.abubusoft.mee.server.exceptions.MalformedCommandException;
import org.abubusoft.mee.server.model.compute.VariableValues;

public interface ExpressionEvaluatorService {
  double evaluate(VariableValues variableValues, String input) throws MalformedCommandException;

  void validate(VariableValues variableValues, String input) throws MalformedCommandException;
}
