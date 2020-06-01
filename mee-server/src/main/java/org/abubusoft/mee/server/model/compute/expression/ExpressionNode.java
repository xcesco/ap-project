package org.abubusoft.mee.server.model.compute.expression;

import org.abubusoft.mee.server.model.compute.MultiVariableValue;

public interface ExpressionNode {
  double evaluate(MultiVariableValue multiVariableValue);
}
