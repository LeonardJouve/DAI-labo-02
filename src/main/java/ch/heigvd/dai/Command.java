package ch.heigvd.dai;

import java.util.HashMap;

public class Command {
    private final Type type;
    private final HashMap<String, String> arguments;

    private Command(Type type, HashMap<String, String> arguments) {
        this.type = type;
        this.arguments = arguments;
    }

    public enum Type {
        PING,
        PONG,
        REGISTER,
        LOGIN,
        ADD,
        GENERATE,
        GET,
        DISCONNECT,
        QUIT,
    };

    private static boolean isArgumentNameToken(String token) {
        return token.startsWith("--");
    }

    public static Command parse(String command) throws IllegalArgumentException {
        String[] tokens = command.split(" ");
        if (tokens.length == 0) throw new IllegalArgumentException("Invalid command.");

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
}
