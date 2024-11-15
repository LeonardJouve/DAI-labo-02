package ch.heigvd.dai.server.commands;

import ch.heigvd.dai.Command;
import ch.heigvd.dai.Hasher;
import ch.heigvd.dai.PassSecureException;
import ch.heigvd.dai.server.State;

import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

public class Login {
    public static void login(State state, Command command) throws PassSecureException {
        if (command == null || command.getType() != Command.Type.LOGIN)
            throw new PassSecureException(PassSecureException.Type.INVALID_ARGUMENT);

        String name = command.getString("username");
        String password = command.getString("password");

        if (name == null || name.isEmpty() || password == null || password.isEmpty())
            throw new PassSecureException(PassSecureException.Type.INVALID_ARGUMENT);

        try {
            String passwordHash = Hasher.hash(password);
            String storedHash = File.read(Path.of(state.getVaultPath() + name + "/" + name + ".hs"));

            state.setLoggedIn(passwordHash.equals(storedHash));
            state.setUserName(name);
            state.setPassword(passwordHash);

        } catch (NoSuchAlgorithmException e) {
            throw new PassSecureException(PassSecureException.Type.ENCRYPTION_EXCEPTION);
        } catch (IOException e) {
            throw new PassSecureException(PassSecureException.Type.FILE_EXCEPTION);
        }
    }
}
