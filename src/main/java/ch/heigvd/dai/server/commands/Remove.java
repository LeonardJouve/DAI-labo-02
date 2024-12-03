/**
 * The {@code Remove} class handles the removal of password entries from a user's vault on the
 * server side. It validates the input and updates the server state accordingly.
 */
package ch.heigvd.dai.server.commands;

import ch.heigvd.dai.Command;
import ch.heigvd.dai.PassSecureException;
import ch.heigvd.dai.server.State;

public class Remove {

  /**
   * Removes a password entry from the user's vault based on the given command.
   *
   * @param state The current {@link State} of the server, representing the user's session and data.
   * @param command The {@link Command} object containing the details of the entry to remove. It
   *     must have a type {@code Command.Type.REMOVE} and the following required argument:
   *     <ul>
   *       <li><b>name</b>: The name of the entry to remove (cannot be null or empty).
   *     </ul>
   *
   * @throws PassSecureException If the {@code state} or {@code command} is null, the command type
   *     is invalid, or the required entry name is missing, empty, or not found.
   */
  public static void remove(State state, Command command) throws PassSecureException {
    // Validate the state and command objects
    if (state == null || command == null || command.getType() != Command.Type.REMOVE) {
      throw new PassSecureException(PassSecureException.Type.INVALID_ARGUMENT);
    }

    // Extract and validate the required argument
    String name = command.getString("name");
    if (name == null || name.isEmpty()) {
      throw new PassSecureException(PassSecureException.Type.INVALID_ARGUMENT);
    }

    // Remove the entry from the vault in the server's state
    state.removeVaultEntry(name);
  }
}
