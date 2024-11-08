/**
 * The Add class is a command-line utility for adding a new password entry to a vault. It implements
 * the Callable interface to enable execution as a command. This class is part of a command-line
 * interface (CLI) application that manages passwords securely.
 *
 * <p>This class allows users to: - Add a new password to the vault. - Overwrite an existing entry
 * if the corresponding option is specified. - Encrypt the password if a password is provided for
 * encryption.
 *
 * @author Leonard Jouve
 * @author Ali Zoubir
 * @version 1.0
 */
package ch.heigvd.dai.server.commands;

import java.io.IOException;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.concurrent.Callable;

import ch.heigvd.dai.Cli;
import picocli.CommandLine;

/**
 * The Add class is a command-line utility for adding a new password entry to a vault. It implements
 * the Callable interface to enable execution as a command. This class is part of a command-line
 * interface (CLI) application that manages passwords securely.
 */
@CommandLine.Command(name = "add", description = "Add a new password to the vault")
public class Add implements Callable<Integer> {

  /** A reference to the parent command (Cli), which provides shared options and functionality. */
  @CommandLine.ParentCommand private Cli parent;

  /** The name of the password entry to be added to the vault. This is a required parameter. */
  @CommandLine.Parameters(index = "0", description = "Name")
  private String name;

  /**
   * The new password to be added. If not provided, it defaults to an empty string. This is the
   * password that will be stored in the vault.
   */
  @CommandLine.Parameters(index = "1", description = "New password to be added.", defaultValue = "")
  private String addPassword;

  /**
   * Option to overwrite an existing vault entry if one with the same name already exists. If this
   * option is not set, and the entry exists, the operation will fail.
   */
  @CommandLine.Option(
      names = {"-o", "--overwrite"},
      description = "Overwrite already existing vault entry if exists.",
      required = false,
      defaultValue = "false")
  private boolean hasOverwrite;

  /**
   * The password used to encrypt the vault. If not provided, the password will be stored in plain
   * text.
   */
  @CommandLine.Option(
      names = {"-p", "--password"},
      description = "Password used to encrypt vault.",
      required = false,
      defaultValue = "")
  private String password;

  /**
   * The main logic of the command. It handles adding a new password entry to the vault, optionally
   * encrypting it and dealing with overwriting existing entries if required.
   *
   * @return 0 if the operation is successful, 1 if an error occurs or if the vault entry already
   *     exists without the overwrite option.
   */
  @Override
  public Integer call() {
    try {
      System.out.println(name);

      // Determine the path to the vault entry based on the provided name
      Path path = File.getVaultEntryPath(parent.getPath(), name);

      // Check if the entry already exists and handle the overwrite option
      if (File.exists(path) && !hasOverwrite) {
        System.err.println(
            "Vault entry with name : "
                + name
                + " already exists. Use -o, --overwrite option if you want to replace the password.");
        return 1;
      }

      // If no encryption password is provided, store the password in plain text
      if (password.isEmpty()) {
        File.write(path, addPassword);
        System.out.println("Password added to the vault : " + addPassword);
      }
      // If an encryption password is provided, encrypt the password before storing it
      else {
        File.write(path, addPassword, password);
        System.out.println("Encrypted password added to the vault : " + addPassword);
      }
    } catch (IOException | GeneralSecurityException e) {
      // Handle errors related to file operations or encryption
      System.err.println("An error occurred : " + e.getMessage());
      return 1;
    }

    // Return 0 indicating success
    return 0;
  }
}
