package ch.heigvd.dai;

public class PassSecureException extends Exception {
  private final Type type;

  public PassSecureException(Type type) {
    this.type = type;
  }

  public enum Type {
    BAD_RESPONSE("bad_response"),
    INVALID_ARGUMENT("invalid_argument"),
    SOCKET_EXCEPTION("socket_exception"),
    USER_ALREADY_EXISTS("user_already_exists"),
    INVALID_CREDENTIALS("invalid_credentials"),
    USER_ALREADY_CONNECTED("user_already_connected"),
    SERVER_ERROR("server_error"),
    UNAUTHORIZED("unauthorized");

    private final String type;

    Type(String value) {
      this.type = value;
    }

    @Override
    public String toString() {
      return this.type;
    }
  }

  @Override
  public String getMessage() {
    return type.toString();
  }
}
