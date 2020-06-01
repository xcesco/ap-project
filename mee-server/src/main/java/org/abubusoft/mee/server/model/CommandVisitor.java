package org.abubusoft.mee.server.model;

public interface CommandVisitor {
  CommandResponse visit(QuitCommand command);

  CommandResponse visit(ComputeCommand command);

  CommandResponse visit(StatCommand command);
}