package org.abubusoft.mee.server.model;

public interface CommandVisitable {
  CommandResponse accept(CommandVisitor visitor);
}