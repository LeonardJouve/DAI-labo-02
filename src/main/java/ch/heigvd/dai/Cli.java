/**
 * The Cli class represents the main entry point for the Pass-Secure application, a command-line
 * password manager that allows users to generate, store, and retrieve passwords securely. The class
 * is configured to handle different subcommands like adding, generating, and retrieving passwords.
 *
 * <p>This class also allows users to specify the path of the vault where the passwords will be
 * stored.
 *
 * @author Leonard Jouve
 * @author Ali Zoubir
 * @version 1.0.0
 */
package ch.heigvd.dai;

import ch.heigvd.dai.client.Client;
import ch.heigvd.dai.server.Server;
import java.nio.file.Path;
import picocli.CommandLine;

/**
 * The Cli class defines the command-line interface for Pass-Secure, a password manager. It manages
 * various subcommands for generating, adding, and retrieving passwords. The scope and options of
 * the command are inherited by the subcommands.
 */
/*
@CommandLine.Command(
    name = "Pass-Secure",
    description =
        "Pass-Secure is a Password Manager allowing you to generate, store and retrieve passwords",
    version = "1.0.0",
    subcommands = {
      Generate.class, // Subcommand for generating a new password
      Get.class, // Subcommand for retrieving an existing password
      Add.class, // Subcommand for adding a new password to the vault
    },
    scope = CommandLine.ScopeType.INHERIT, // Inherit options and parameters in subcommands
    mixinStandardHelpOptions = true // Include standard help options like --help and --version
    )
 */

@CommandLine.Command(
    description = "A small game to experiment with TCP.",
    version = "1.0.0",
    subcommands = {
      Client.class,
      Server.class,
    },
    scope = CommandLine.ScopeType.INHERIT,
    mixinStandardHelpOptions = true)
public class Cli {
  /**
   * The path to the vault where the passwords are stored. If not specified, it defaults to the
   * current directory.
   */
  @CommandLine.Option(
      names = {"-p", "--path"},
      description = "Path of the vault used to store and retrieve passwords.",
      required = false,
      defaultValue = "./")
  private String path;

  /**
   * Retrieves the vault path as a Path object.
   *
   * @return The Path representing the location of the vault.
   */
  public Path getPath() {
    return Path.of(path);
  }
}
