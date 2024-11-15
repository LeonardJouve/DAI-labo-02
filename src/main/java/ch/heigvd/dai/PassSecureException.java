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
        FILE_EXCEPTION("file reading/writing error"),
        ENCRYPTION_EXCEPTION("encryption error");

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
