/**
 * The {@code Login} class handles user authentication on the server side. It validates input
 * credentials and performs the login operation by updating the server's state.
 */
package ch.heigvd.dai.server.commands;

import ch.heigvd.dai.Command;
import ch.heigvd.dai.PassSecureException;
import ch.heigvd.dai.server.State;

public class Login {

  /**
   * Authenticates a user based on the provided login command and updates the server state to
   * reflect the authenticated session.
   *
   * @param state The current {@link State} of the server, representing the session and user data.
   * @param command The {@link Command} object containing the login credentials. It must have a type
   *     {@code Command.Type.LOGIN} and the following required arguments:
   *     <ul>
   *       <li><b>username</b>: The username of the user (cannot be null or empty).
   *       <li><b>password</b>: The password for authentication (cannot be null or empty).
   *     </ul>
   *
   * @throws PassSecureException If the {@code state} or {@code command} is null, the command type
   *     is invalid, or the required credentials are missing, empty, or incorrect.
   */
  public static void login(State state, Command command) throws PassSecureException {
    // Validate the state and command objects
    if (state == null || command == null || command.getType() != Command.Type.LOGIN) {
      throw new PassSecureException(PassSecureException.Type.INVALID_ARGUMENT);
    }

    // Extract and validate the required credentials
    String username = command.getString("username");
    String password = command.getString("password");
    if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
      throw new PassSecureException(PassSecureException.Type.INVALID_ARGUMENT);
    }

    // Perform the login operation in the server's state
    state.login(username, password);
  }
}
