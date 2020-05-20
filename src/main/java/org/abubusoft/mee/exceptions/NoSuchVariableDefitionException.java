package org.abubusoft.mee.exceptions;

public class NoSuchVariableDefitionException extends AppRuntimeException {
  public NoSuchVariableDefitionException(String variableName) {
    super(String.format("No variable %s is defined", variableName));
  }
}
