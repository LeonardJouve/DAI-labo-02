/**
 * This class provides a method to generate secure passwords based on the given command parameters.
 */
package ch.heigvd.dai.client.commands;

import ch.heigvd.dai.Command;
import ch.heigvd.dai.PassSecureException;
import java.util.Random;

public class Generate {

  /**
   * Generates a random password based on the specifications provided in the given {@link Command}.
   *
   * @param command The command object containing the password generation specifications. It must
   *     have a type {@code Command.Type.GENERATE}. The following attributes are used:
   *     <ul>
   *       <li><b>length</b>: an integer specifying the desired password length (default is 15 if <=
   *           0).
   *       <li><b>special</b>: a boolean indicating whether to include special characters.
   *     </ul>
   *
   * @return A randomly generated password as a {@link String}.
   * @throws PassSecureException if the {@code command} is null, does not have the correct type, or
   *     contains invalid arguments.
   */
  public static String generate(Command command) throws PassSecureException {
    // Validate the command and ensure it has the correct type for password generation
    if (command == null || command.getType() != Command.Type.GENERATE)
      throw new PassSecureException(PassSecureException.Type.INVALID_ARGUMENT);

    // Retrieve the desired password length, defaulting to 15 if an invalid length is specified
    int length = command.getInt("length");
    boolean includeSpecialChar = command.getBoolean("special");
    if (length <= 0) length = 15;

    // Initialize the random number generator and password builder
    Random rand = new Random();
    StringBuilder password = new StringBuilder();

    // Define the character sets for password generation
    char[] specialChars = {
      '!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '-', '_', '=', '+', '[', ']', '{', '}', ';',
      ':', ',', '.', '<', '>', '/', '?', '\\', '|', '\'', '"', '`', '~'
    };
    int nAlphabet = 'z' - 'a'; // Number of alphabetic characters
    int nDigits = 10; // Number of numeric digits
    int numberOfCharacters =
        (2 * nAlphabet + nDigits); // Total characters (uppercase + lowercase + digits)
    int max = (includeSpecialChar ? numberOfCharacters + specialChars.length : numberOfCharacters);
    int min = 0;

    // Generate the password by randomly selecting characters from the defined sets
    for (int i = 0; i < length; i++) {
      int randomNum = rand.nextInt((max - min) + 1) + min;
      if (randomNum > numberOfCharacters) {
        // Append a special character if within the special characters range
        password.append(specialChars[randomNum - numberOfCharacters - 1]);
      } else {
        // Append a digit, uppercase letter, or lowercase letter based on the range
        if (randomNum < nDigits) {
          password.append((char) (randomNum + '0'));
        } else if (randomNum <= nDigits + nAlphabet) {
          password.append((char) ((randomNum - nDigits) + 'A'));
        } else {
          password.append((char) ((randomNum - nDigits - nAlphabet) + 'a'));
        }
      }
    }

    // Return the generated password
    return password.toString();
  }
}
