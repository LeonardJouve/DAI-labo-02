/**
 * The {@code Register} class handles user registration on the server side. It validates input
 * credentials and updates the server's state to register a new user.
 */
package ch.heigvd.dai.server.commands;

import ch.heigvd.dai.Command;
import ch.heigvd.dai.PassSecureException;
import ch.heigvd.dai.server.State;

public class Register {

  /**
   * Registers a new user based on the provided registration command and updates the server state to
   * store the user's credentials.
   *
   * @param state The current {@link State} of the server, representing the session and user data.
   * @param command The {@link Command} object containing the registration details. It must have a
   *     type {@code Command.Type.REGISTER} and the following required arguments:
   *     <ul>
   *       <li><b>username</b>: The username of the new user (cannot be null or empty).
   *       <li><b>password</b>: The password for the new user (cannot be null or empty).
   *     </ul>
   *
   * @throws PassSecureException If the {@code state} or {@code command} is null, the command type
   *     is invalid, or the required credentials are missing, empty, or invalid.
   */
  public static void register(State state, Command command) throws PassSecureException {
    // Validate the state and command objects
    if (state == null || command == null || command.getType() != Command.Type.REGISTER) {
      throw new PassSecureException(PassSecureException.Type.INVALID_ARGUMENT);
    }

    // Extract and validate the required credentials
    String username = command.getString("username");
    String password = command.getString("password");
    if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
      throw new PassSecureException(PassSecureException.Type.INVALID_ARGUMENT);
    }

    // Register the new user in the server's state
    state.register(username, password);
  }
}
