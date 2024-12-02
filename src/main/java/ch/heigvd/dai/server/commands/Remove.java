package ch.heigvd.dai.server.commands;

import ch.heigvd.dai.Command;
import ch.heigvd.dai.PassSecureException;
import ch.heigvd.dai.server.State;

public class Remove {
  public static void remove(State state, Command command) throws PassSecureException {
    if (state == null || command == null || command.getType() != Command.Type.REMOVE)
      throw new PassSecureException(PassSecureException.Type.INVALID_ARGUMENT);

    String name = command.getString("name");

    if (name == null || name.isEmpty())
      throw new PassSecureException(PassSecureException.Type.INVALID_ARGUMENT);

    state.removeVaultEntry(name);
  }
}
