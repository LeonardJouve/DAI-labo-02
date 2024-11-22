package ch.heigvd.dai.server.commands;

import ch.heigvd.dai.Command;
import ch.heigvd.dai.PassSecureException;
import ch.heigvd.dai.server.State;

public class Login {
  public static void login(State state, Command command) throws PassSecureException {
    if (state == null || command == null || command.getType() != Command.Type.LOGIN)
      throw new PassSecureException(PassSecureException.Type.INVALID_ARGUMENT);

    String username = command.getString("username");
    String password = command.getString("password");

    if (username == null || username.isEmpty() || password == null || password.isEmpty())
      throw new PassSecureException(PassSecureException.Type.INVALID_ARGUMENT);

    state.login(username, password);
  }
}
