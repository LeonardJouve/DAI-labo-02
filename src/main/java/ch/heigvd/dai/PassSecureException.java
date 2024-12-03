/**
 * The {@code PassSecureException} class represents exceptions specific to the pass-secure system.
 * It encapsulates various error types and provides descriptive error messages.
 */
package ch.heigvd.dai;

public class PassSecureException extends Exception {

  private final Type type;

  /**
   * Constructs a new {@code PassSecureException} with the specified type.
   *
   * @param type The {@link Type} of the exception.
   */
  public PassSecureException(Type type) {
    this.type = type;
  }

  /** Represents the different types of exceptions that can occur in the pass-secure system. */
  public enum Type {
    BAD_RESPONSE("bad_response"),
    INVALID_ARGUMENT("invalid_argument"),
    SOCKET_EXCEPTION("socket_exception"),
    USER_ALREADY_EXISTS("user_already_exists"),
    INVALID_CREDENTIALS("invalid_credentials"),
    USER_ALREADY_CONNECTED("user_already_connected"),
    SERVER_ERROR("server_error"),
    UNAUTHORIZED("unauthorized"),
    CIPHER_ERROR("cipher_error"),
    INVALID_COMMAND("invalid_command"),
    ENTRY_ALREADY_EXISTS("entry_already_exists"),
    ENTRY_NOT_FOUND("entry_not_found");

    private final String type;

    Type(String value) {
      this.type = value;
    }

    /**
     * Returns a string representation of the exception type.
     *
     * @return The string representation of the exception type.
     */
    @Override
    public String toString() {
      return this.type;
    }
  }

  /**
   * Returns a descriptive message for the exception.
   *
   * @return The message associated with the exception type.
   */
  @Override
  public String getMessage() {
    return type.toString();
  }
}
