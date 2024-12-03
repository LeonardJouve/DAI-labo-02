/**
 * The {@code Command} class represents a command sent between the client and server in the
 * pass-secure system. It supports parsing, encryption, decryption, and retrieval of arguments.
 */
package ch.heigvd.dai;

import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

public class Command {

  private static final String ENCRYPTION_PASSWORD_ARGUMENT = "encryptionPassword";
  private static final String DECRYPTION_PASSWORD_ARGUMENT = "decryptionPassword";
  private static final String PASSWORD_ARGUMENT = "password";

  private final Type type;
  private final HashMap<String, String> arguments;

  /**
   * Constructs a new {@code Command} with the specified type and an empty argument map.
   *
   * @param type The {@link Type} of the command.
   */
  public Command(Type type) {
    this(type, new HashMap<>());
  }

  /**
   * Constructs a new {@code Command} with the specified type and argument map.
   *
   * @param type The {@link Type} of the command.
   * @param arguments A map of arguments associated with the command.
   */
  public Command(Type type, HashMap<String, String> arguments) {
    this.type = type;
    this.arguments = arguments;
  }

  /** Represents the different types of commands supported. */
  public enum Type {
    PING("PING"),
    REGISTER("REGISTER"),
    LOGIN("LOGIN"),
    ADD("ADD"),
    GENERATE("GENERATE"),
    GET("GET"),
    REMOVE("REMOVE"),
    DISCONNECT("DISCONNECT"),
    QUIT("QUIT"),
    OK("OK"),
    NOK("NOK"),
    HELP("HELP");

    private final String type;

    Type(String type) {
      this.type = type;
    }

    @Override
    public String toString() {
      return type;
    }
  }

  /**
   * Parses a command string and returns the corresponding {@code Command} object.
   *
   * @param command The string representation of the command.
   * @return The parsed {@code Command}.
   * @throws PassSecureException If the command is invalid or cannot be parsed.
   */
  public static Command parse(String command) throws PassSecureException {
    String[] tokens = command.split(" ");
    if (tokens.length == 0)
      throw new PassSecureException(PassSecureException.Type.INVALID_ARGUMENT);

    Type type;
    try {
      type = Type.valueOf(tokens[0]);
    } catch (IllegalArgumentException e) {
      throw new PassSecureException(PassSecureException.Type.INVALID_COMMAND);
    }
    HashMap<String, String> arguments = new HashMap<>();

    for (int i = 0; i < tokens.length; ++i) {
      String token = tokens[i];
      if (!isArgumentNameToken(token)) continue;

      String argument = token.substring(2);
      if (i + 1 < tokens.length && !isArgumentNameToken(tokens[i + 1])) {
        arguments.put(argument, tokens[i + 1]);
        ++i;
      } else {
        arguments.put(argument, Boolean.valueOf(true).toString());
      }
    }

    return new Command(type, arguments);
  }

  /**
   * Encrypts the password argument if present, using the provided encryption password.
   *
   * @throws PassSecureException If encryption fails or required arguments are missing.
   */
  public void encrypt() throws PassSecureException {
    if (!arguments.containsKey(ENCRYPTION_PASSWORD_ARGUMENT)
        || !arguments.containsKey(PASSWORD_ARGUMENT)) return;

    String encryptionPassword = arguments.get(ENCRYPTION_PASSWORD_ARGUMENT);
    String password = arguments.get(PASSWORD_ARGUMENT);

    try {
      String encryptedPassword = Cipher.encrypt(password, encryptionPassword);
      arguments.replace(PASSWORD_ARGUMENT, encryptedPassword);
    } catch (GeneralSecurityException e) {
      throw new PassSecureException(PassSecureException.Type.CIPHER_ERROR);
    }
  }

  /**
   * Decrypts the provided password using the decryption password argument, if present.
   *
   * @param password The encrypted password to decrypt.
   * @return The decrypted password.
   * @throws PassSecureException If decryption fails or required arguments are missing.
   */
  public String decrypt(String password) throws PassSecureException {
    if (!arguments.containsKey(DECRYPTION_PASSWORD_ARGUMENT)) return password;

    String decryptionPassword = arguments.get(DECRYPTION_PASSWORD_ARGUMENT);

    try {
      return Cipher.decrypt(password, decryptionPassword);
    } catch (GeneralSecurityException e) {
      throw new PassSecureException(PassSecureException.Type.CIPHER_ERROR);
    }
  }

  /**
   * Retrieves an integer argument by name. Returns 0 if the argument is missing or invalid.
   *
   * @param name The name of the argument.
   * @return The integer value of the argument, or 0 if not found or invalid.
   */
  public int getInt(String name) {
    String value = arguments.get(name);
    if (value == null) return 0;

    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  /**
   * Retrieves a boolean argument by name. Returns false if the argument is missing or invalid.
   *
   * @param name The name of the argument.
   * @return The boolean value of the argument, or false if not found or invalid.
   */
  public boolean getBoolean(String name) {
    String value = arguments.get(name);
    if (value == null) return false;

    return Boolean.parseBoolean(value);
  }

  /**
   * Retrieves a string argument by name.
   *
   * @param name The name of the argument.
   * @return The value of the argument, or null if not found.
   */
  public String getString(String name) {
    return arguments.get(name);
  }

  /**
   * Gets the type of the command.
   *
   * @return The {@link Type} of the command.
   */
  public Type getType() {
    return type;
  }

  /**
   * Gets all the arguments of the command.
   *
   * @return A {@link HashMap} containing the arguments.
   */
  public HashMap<String, String> getArguments() {
    return arguments;
  }

  /**
   * Converts the command to a string representation.
   *
   * @return A string representation of the command.
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(type);
    for (Map.Entry<String, String> entry : arguments.entrySet()) {
      if (entry.getKey().equals(ENCRYPTION_PASSWORD_ARGUMENT)
          || entry.getKey().equals(DECRYPTION_PASSWORD_ARGUMENT)) continue;

      sb.append(" --");
      sb.append(entry.getKey());
      sb.append(" ");
      sb.append(entry.getValue());
    }

    return sb.toString();
  }

  /**
   * Checks if a token represents an argument name.
   *
   * @param token The token to check.
   * @return True if the token starts with "--", otherwise false.
   */
  private static boolean isArgumentNameToken(String token) {
    return token.startsWith("--");
  }
}
