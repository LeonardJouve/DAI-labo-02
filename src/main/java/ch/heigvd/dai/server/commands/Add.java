package ch.heigvd.dai.server.commands;

import ch.heigvd.dai.Command;
import ch.heigvd.dai.PassSecureException;
import ch.heigvd.dai.server.State;

public class Add {
  public static void add(State state, Command command) throws PassSecureException {
    if (state == null || command == null || command.getType() != Command.Type.ADD)
      throw new PassSecureException(PassSecureException.Type.INVALID_ARGUMENT);

    String name = command.getString("name");
    String password = command.getString("password");
    boolean overwrite = command.getBoolean("overwrite");

    if (name == null || name.isEmpty() || password == null || password.isEmpty())
      throw new PassSecureException(PassSecureException.Type.INVALID_ARGUMENT);

    state.addVaultEntry(name, password, overwrite);
  }
}
