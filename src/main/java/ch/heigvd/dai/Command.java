package ch.heigvd.dai;

import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

public class Command {
  private final static String ENCRYPTION_PASSWORD_ARGUMENT = "encryptionPassword";
  private final static String DECRYPTION_PASSWORD_ARGUMENT = "decryptionPassword";
  private final static String PASSWORD_ARGUMENT = "password";

  private final Type type;
  private final HashMap<String, String> arguments;

  public Command(Type type) {
    this(type, new HashMap<>());
  }

  public Command(Type type, HashMap<String, String> arguments) {
    this.type = type;
    this.arguments = arguments;
  }

  public enum Type { // Assigner a un string
    PING("PING"),
    PONG("PONG"),
    REGISTER("REGISTER"),
    LOGIN("LOGIN"),
    ADD("ADD"),
    GENERATE("GENERATE"),
    GET("GET"),
    DISCONNECT("DISCONNECT"),
    QUIT("QUIT"),
    OK("OK"),
    NOK("NOK");

    private final String type;

    Type(String type) {
      this.type = type.toUpperCase();
    }

    @Override
    public String toString() {
      return type;
    }
  };

  private static boolean isArgumentNameToken(String token) {
    return token.startsWith("--");
  }

  public static Command parse(String command) throws PassSecureException {
    String[] tokens = command.split(" ");
    if (tokens.length == 0) throw new PassSecureException(PassSecureException.Type.INVALID_ARGUMENT);

    final Type type = Type.valueOf(tokens[0]);
    HashMap<String, String> arguments = new HashMap<>();

    for (int i = 0; i < tokens.length - 1; ++i) {
      String token = tokens[i];
      if (!isArgumentNameToken(token)) continue;

      String argument = token.substring(2);
      if (i + 1 < tokens.length && !isArgumentNameToken(tokens[i + 1])) {
        arguments.put(argument, tokens[i + 1]);
      } else {
        arguments.put(argument, Boolean.valueOf(true).toString());
      }
    }

    return new Command(type, arguments);
  }

  public void encrypt() throws PassSecureException {
    if (!arguments.containsKey(ENCRYPTION_PASSWORD_ARGUMENT) || !arguments.containsKey(PASSWORD_ARGUMENT)) return;

    String encryptionPassword = arguments.get(ENCRYPTION_PASSWORD_ARGUMENT);
    String password = arguments.get(PASSWORD_ARGUMENT);

    try {
      String encryptedPassword = Cipher.encrypt(password, encryptionPassword);
      arguments.replace(PASSWORD_ARGUMENT, encryptedPassword);
    } catch (GeneralSecurityException e) {
      throw new PassSecureException(PassSecureException.Type.CIPHER_ERROR);
    }
  }

  public void decrypt() throws PassSecureException {
    if (!arguments.containsKey(DECRYPTION_PASSWORD_ARGUMENT) || !arguments.containsKey(PASSWORD_ARGUMENT)) return;

    String decryptionPassword = arguments.get(DECRYPTION_PASSWORD_ARGUMENT);
    String password = arguments.get(PASSWORD_ARGUMENT);

    try {
      String decryptedPassword = Cipher.decrypt(password, decryptionPassword);
      arguments.replace(PASSWORD_ARGUMENT, decryptedPassword);
    } catch (GeneralSecurityException e) {
      throw new PassSecureException(PassSecureException.Type.CIPHER_ERROR);
    }
  }

  public int getInt(String name) throws NumberFormatException {
    return Integer.parseInt(arguments.get(name));
  }

  public boolean getBoolean(String name) {
    return Boolean.parseBoolean(arguments.get(name));
  }

  public String getString(String name) {
    return arguments.get(name);
  }

  public Type getType() {
    return type;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(type);
    for (Map.Entry<String, String> entry : arguments.entrySet()) {
      if (entry.getKey().equals(ENCRYPTION_PASSWORD_ARGUMENT) || entry.getKey().equals(DECRYPTION_PASSWORD_ARGUMENT)) continue;

      sb.append(" --");
      sb.append(entry.getKey());
      sb.append(" ");
      sb.append(entry.getValue());
    }

    return sb.toString();
  }
}
