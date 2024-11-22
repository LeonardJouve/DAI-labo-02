/**
 * The Get class is a command-line utility that allows users to retrieve a stored password for a
 * specific vault entry. It can handle both encrypted and plain text vault entries depending on
 * whether the decryption password is provided.
 *
 * <p>This class supports: - Retrieving passwords from the vault. - Decrypting passwords if an
 * encryption password is specified. - Verifying if the vault entry exists before attempting to
 * retrieve it.
 *
 * @author Leonard Jouve
 * @author Ali Zoubir
 * @version 1.0
 */
package ch.heigvd.dai.server.commands;

import ch.heigvd.dai.Cli;
import java.io.IOException;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.concurrent.Callable;
import picocli.CommandLine;

/**
 * The Get class defines the command to retrieve a stored password from the vault based on the name
 * provided by the user. It can also decrypt the password if necessary.
 */
@CommandLine.Command(name = "get", description = "Get password for specific name")
public class Get implements Callable<Integer> {

  /** Reference to the parent Cli command which provides shared options and configurations. */
  @CommandLine.ParentCommand private Cli parent;

  /** The name of the vault entry whose password is to be retrieved. */
  @CommandLine.Parameters(index = "0", description = "Name")
  private String name;

  /**
   * The password used to decrypt the vault. If not provided, the vault entry is treated as plain
   * text.
   */
  @CommandLine.Option(
      names = {"-p", "--password"},
      description = "Password used to decrypt vault.",
      required = false,
      defaultValue = "")
  private String password;

  /**
   * The main method that runs when the "get" command is executed. It retrieves the password stored
   * in the vault for the given name, decrypting it if a password is provided.
   *
   * @return 0 if the operation is successful, 1 if an error occurs.
   */
  @Override
  public Integer call() {
    try {
      System.out.println(name);
      Path path = File.getVaultEntryPath(parent.getPath(), name);

      // Check if the vault entry exists
      if (!File.exists(path)) {
        System.err.println("Vault entry with name : " + name + " does not exist.");
        return 1;
      }

      // Retrieve the password, decrypt if needed
      String recoveredPassword = password.isEmpty() ? File.read(path) : File.read(path, password);
      System.out.println("Stored password : " + recoveredPassword);
    } catch (IOException | GeneralSecurityException e) {
      // Handle errors related to file operations or decryption
      System.err.println("An error occurred : " + e.getMessage());
      return 1;
    }

    return 0;
  }
}
