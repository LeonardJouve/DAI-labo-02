package ch.heigvd.dai.server.commands;

import ch.heigvd.dai.Command;
import ch.heigvd.dai.Hasher;

import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

public class Login {

    static boolean login(Command command) throws NoSuchAlgorithmException, IOException {
        if (command == null || command.getType() != Command.Type.LOGIN)
            throw new PassSecureException(PassSecureException.Type.INVALID_ARGUMENT);

        String name = command.getString("username");
        String password = command.getString("password");

        if (name == null || name.isEmpty() || password == null || password.isEmpty())
            throw new PassSecureException(PassSecureException.Type.INVALID_ARGUMENT);

        String passwordHash = Hasher.hash(password);
        String storedHash = File.read(Path.of("./" + name + ".hs")); // "./" temporaire -> recuperer de picocli à l'avenir

        return (passwordHash.equals(storedHash));

    }
}
