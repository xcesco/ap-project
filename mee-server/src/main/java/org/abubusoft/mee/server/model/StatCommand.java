package org.abubusoft.mee.server.model;

import org.abubusoft.mee.server.model.stat.StatType;

public class StatCommand extends Command {
  private final StatType subType;

  public StatCommand(StatType subtype) {
    super(CommandType.STAT);
    this.subType = subtype;
  }

  @Override
  public CommandResponse accept(CommandVisitor visitor) {
    return visitor.visit(this);
  }

  public StatType getSubType() {
    return subType;
  }

}
