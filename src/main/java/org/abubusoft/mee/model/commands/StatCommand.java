package org.abubusoft.mee.model.commands;

import org.abubusoft.mee.model.CommandType;

public class StatCommand extends AbstractCommand {
  private final StatType subType;

  public StatCommand(StatType subtype) {
    super(CommandType.STAT);
    this.subType = subtype;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("StatCommand{");
    sb.append("subType=").append(subType);
    sb.append('}');
    return sb.toString();
  }

  public StatType getSubType() {
    return subType;
  }

  @Override
  public void execute() {

  }
}
