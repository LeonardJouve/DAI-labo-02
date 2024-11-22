/**
 * The Generate class is a command-line utility for generating new passwords. It allows users to
 * specify the length and inclusion of special characters in the generated password. The class also
 * provides options to add the generated password to a vault and handle overwriting of existing
 * vault entries.
 *
 * <p>This class supports the following functionalities: - Generating random passwords. - Adding the
 * generated password to the vault. - Overwriting existing vault entries when specified.
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
import java.util.Random;
import java.util.concurrent.Callable;
import picocli.CommandLine;

/**
 * The Generate class defines the command to generate a new password based on user specifications.
 * It can also save the generated password to a vault, optionally encrypting it.
 */
@CommandLine.Command(name = "generate", description = "Generate new password")
public class Generate implements Callable<Integer> {

  /** Reference to the parent Cli command which provides shared options and configurations. */
  @CommandLine.ParentCommand protected Cli parent;

  /** The name of the password entry to be generated and optionally saved. */
  @CommandLine.Parameters(index = "0", description = "Name")
  private String name;

  /** The length of the password to be generated. Defaults to 14 characters. */
  @CommandLine.Option(
      names = {"-l", "--length"},
      description = "Length of the password to be generated.",
      required = false,
      defaultValue = "14")
  private int length;

  /** Option to include special characters in the generated password. */
  @CommandLine.Option(
      names = {"-s", "--special"},
      description = "Generate with special characters true or false.",
      required = false)
  private boolean hasSpecial;

  /** Option to overwrite an existing vault entry if one with the same name already exists. */
  @CommandLine.Option(
      names = {"-o", "--overwrite"},
      description = "Overwrite already existing vault entry if exists.",
      required = false,
      defaultValue = "false")
  private boolean hasOverwrite;

  /** Option to add the generated password to the vault. */
  @CommandLine.Option(
      names = {"-a", "--add"},
      description = "Add generated password to the vault true or false.",
      required = false)
  private boolean hasAdd;

  /** The password used to decrypt the vault. */
  @CommandLine.Option(
      names = {"-p", "--password"},
      description = "Password used to decrypt vault.",
      required = false,
      defaultValue = "")
  private String password;

  /**
   * Generates a random password with the specified length and the option to include special
   * characters.
   *
   * @param nChars The number of characters in the password.
   * @param hasSpecialChar If true, includes special characters in the generated password.
   * @return The generated password as a String.
   */
  public String generatePassword(int nChars, Boolean hasSpecialChar) {
    Random rand = new Random();
    StringBuilder password = new StringBuilder();

    char[] specialChars = {
      '!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '-', '_', '=', '+', '[', ']', '{', '}', ';',
      ':', ',', '.', '<', '>', '/', '?', '\\', '|', '\'', '"', '`', '~'
    };
    int nAlphabet = 'z' - 'a';
    int nDigits = 10;
    int numberOfCharacters = (2 * nAlphabet + nDigits);
    int max = (hasSpecialChar ? numberOfCharacters + specialChars.length : numberOfCharacters);
    int min = 0;

    // Generate the password by randomly selecting characters, digits, or special characters
    for (int i = 0; i < nChars; i++) {
      int randomNum = rand.nextInt((max - min) + 1) + min;
      if (randomNum > numberOfCharacters) {
        password.append(specialChars[randomNum - numberOfCharacters - 1]);
      } else {
        if (randomNum < nDigits) {
          password.append((char) (randomNum + '0'));
        } else if (randomNum <= nDigits + nAlphabet) {
          password.append((char) ((randomNum - nDigits) + 'A'));
        } else {
          password.append((char) ((randomNum - nDigits - nAlphabet) + 'a'));
        }
      }
    }

    return password.toString();
  }

  /**
   * Main method that runs when the "generate" command is executed. It generates a password based on
   * user specifications and optionally adds it to the vault.
   *
   * @return 0 if the operation is successful, 1 if an error occurs.
   */
  @Override
  public Integer call() {
    try {
      System.out.println(name);
      String s = this.generatePassword(length, hasSpecial);
      System.out.println("Generated password : " + s);

      if (hasAdd) {
        Path path = File.getVaultEntryPath(parent.getPath(), name);

        if (File.exists(path) && !hasOverwrite) {
          System.err.println(
              "Vault entry with name: "
                  + name
                  + " already exists. Use -o, --overwrite option if you want to replace the password.");
          return 1;
        }

        // Save the generated password either encrypted or in plain text
        if (password.isEmpty()) {
          File.write(path, s);
          System.out.println("Successfully saved password");
        } else {
          File.write(path, s, password);
          System.out.println("Successfully saved encrypted password");
        }
      }
    } catch (IOException | GeneralSecurityException e) {
      // Handle any errors that occur during password generation or storage
      System.err.println("An error occurred : " + e.getMessage());
      return 1;
    }

    return 0;
  }
}
