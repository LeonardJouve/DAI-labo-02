package ch.heigvd.dai;

public class PassSecureException extends Exception {
    private final Type type;

    public PassSecureException(Type type) {
        this.type = type;
    }

    public enum Type {
        BAD_RESPONSE("bad response"),
        INVALID_ARGUMENT("invalid argument"),
        SOCKET_EXCEPTION("socket exception"),
        USER_ALREADY_EXISTS("user already exists"),
        INVALID_CREDENTIALS("invalid credentials"),
        USER_ALREADY_CONNECTED("user already connected"),
        SERVER_ERROR("server error"),
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
