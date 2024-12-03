/**
 * The {@code Add} class handles the logic for adding entries to a user's password vault on the
 * server side. It validates the input, processes the command, and updates the server state.
 */
package ch.heigvd.dai.server.commands;

import ch.heigvd.dai.Command;
import ch.heigvd.dai.PassSecureException;
import ch.heigvd.dai.server.State;

public class Add {

  /**
   * Processes an "ADD" command by adding a new entry to the user's password vault.
   *
   * @param state The current {@link State} of the server, representing the user's session and data.
   * @param command The {@link Command} object containing the details of the entry to add. It must
   *     have a type {@code Command.Type.ADD} and the following required arguments:
   *     <ul>
   *       <li><b>name</b>: The name of the entry (cannot be null or empty).
   *       <li><b>password</b>: The password associated with the entry (cannot be null or empty).
   *     </ul>
   *     Optionally, it can contain:
   *     <ul>
   *       <li><b>overwrite</b>: A boolean indicating whether to overwrite an existing entry with
   *           the same name.
   *     </ul>
   *
   * @throws PassSecureException If the {@code state} or {@code command} is null, the command type
   *     is invalid, required arguments are missing or empty, or the server encounters an error
   *     while adding the entry.
   */
  public static void add(State state, Command command) throws PassSecureException {
    // Validate the state and command objects
    if (state == null || command == null || command.getType() != Command.Type.ADD) {
      throw new PassSecureException(PassSecureException.Type.INVALID_ARGUMENT);
    }

    // Extract and validate the required arguments
    String name = command.getString("name");
    String password = command.getString("password");
    boolean overwrite = command.getBoolean("overwrite");

    if (name == null || name.isEmpty() || password == null || password.isEmpty()) {
      throw new PassSecureException(PassSecureException.Type.INVALID_ARGUMENT);
    }

    // Add the entry to the vault in the server's state
    state.addVaultEntry(name, password, overwrite);
  }
}
