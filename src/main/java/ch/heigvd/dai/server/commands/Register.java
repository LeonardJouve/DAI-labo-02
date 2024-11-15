package ch.heigvd.dai.server.commands;

import ch.heigvd.dai.Command;
import ch.heigvd.dai.Hasher;
import ch.heigvd.dai.server.commands.File;

import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;


public class Register {


    static void register(Command command) throws NoSuchAlgorithmException, IOException {
        if (command == null || command.getType() != Command.Type.REGISTER)
            throw new PassSecureException(PassSecureException.Type.INVALID_ARGUMENT);

        String name = command.getString("username");
        String password = command.getString("password");

        if (name == null || name.isEmpty() || password == null || password.isEmpty())
            throw new PassSecureException(PassSecureException.Type.INVALID_ARGUMENT);

        String passwordHash = Hasher.hash(password);

        File.write(Path.of("./" + name + ".hs"), passwordHash); // "./" temporaire -> recuperer de picocli à l'avenir


    }
}


