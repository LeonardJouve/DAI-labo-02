/**
 * The Main class is the entry point for the Pass-Secure password manager application. It sets up
 * the command-line interface (CLI) by using the Picocli library to parse and execute the
 * user-provided commands. The application supports generating, adding, and retrieving passwords
 * through various subcommands.
 *
 * <p>This class handles: - Retrieving the name of the jar file being executed. - Configuring and
 * executing the CLI with case-insensitive command options. - Exiting the application with the
 * appropriate exit code after command execution.
 *
 * @author Leonard Jouve
 * @author Ali Zoubir
 * @version 1.0
 */
package ch.heigvd.dai;

import java.io.File;
import picocli.CommandLine;

/**
 * The Main class initializes and runs the Pass-Secure CLI. It configures the command-line interface
 * to accept various subcommands and executes the user input.
 */
public class Main {

  /**
   * The main method serves as the entry point for the Pass-Secure application. It sets up the
   * command-line interface using the Picocli library and executes the user's command.
   *
   * @param args The command-line arguments provided by the user.
   */
  public static void main(String[] args) {
    // Retrieve the name of the jar file being executed
    String jarFilename =
        new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath())
            .getName();

    // Create a new instance of the CLI and configure it
    Cli cli = new Cli();
    int exitCode =
        new CommandLine(cli)
            .setCommandName(jarFilename) // Set the command name to the jar file name
            .setCaseInsensitiveEnumValuesAllowed(true) // Allow case-insensitive enum values
            .execute(args); // Execute the provided commands

    // Exit the application with the appropriate exit code
    System.exit(exitCode);
  }
}
