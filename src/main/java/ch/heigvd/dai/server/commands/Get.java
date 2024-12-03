/**
 * The {@code Get} class handles the retrieval of password entries from the user's vault on the
 * server side. It validates the input and fetches the requested entry from the server state.
 */
package ch.heigvd.dai.server.commands;

import ch.heigvd.dai.Command;
import ch.heigvd.dai.PassSecureException;
import ch.heigvd.dai.server.State;

public class Get {

  /**
   * Retrieves a password entry from the user's vault based on the given command.
   *
   * @param state The current {@link State} of the server, representing the user's session and data.
   * @param command The {@link Command} object containing the details of the entry to retrieve. It
   *     must have a type {@code Command.Type.GET} and the following required argument:
   *     <ul>
   *       <li><b>name</b>: The name of the entry to retrieve (cannot be null or empty).
   *     </ul>
   *
   * @return The password associated with the requested entry as a {@link String}.
   * @throws PassSecureException If the {@code state} or {@code command} is null, the command type
   *     is invalid, or the requested entry name is missing, empty, or cannot be found.
   */
  public static String get(State state, Command command) throws PassSecureException {
    // Validate the state and command objects
    if (state == null || command == null || command.getType() != Command.Type.GET) {
      throw new PassSecureException(PassSecureException.Type.INVALID_ARGUMENT);
    }

    // Extract and validate the required argument
    String name = command.getString("name");
    if (name == null || name.isEmpty()) {
      throw new PassSecureException(PassSecureException.Type.INVALID_ARGUMENT);
    }

    // Retrieve and return the password entry from the vault in the server's state
    return state.getVaultEntry(name);
  }
}
