package ch.heigvd.dai.server.commands;

import ch.heigvd.dai.Command;
import ch.heigvd.dai.PassSecureException;
import ch.heigvd.dai.server.State;

public class Get {
  public static String get(State state, Command command) throws PassSecureException {
    if (state == null || command == null || command.getType() != Command.Type.GET)
      throw new PassSecureException(PassSecureException.Type.INVALID_ARGUMENT);

    String name = command.getString("name");

    if (name == null || name.isEmpty())
      throw new PassSecureException(PassSecureException.Type.INVALID_ARGUMENT);

    return state.getVaultEntry(name);
  }
}
