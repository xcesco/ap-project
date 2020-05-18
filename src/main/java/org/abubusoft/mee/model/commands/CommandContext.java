package org.abubusoft.mee.model.commands;

public class CommandContext {
  private final ComputeCommand.Builder computeBuilder=new ComputeCommand.Builder();

  public ComputeCommand.Builder getComputeBuilder() {
    return computeBuilder;
  }
}
