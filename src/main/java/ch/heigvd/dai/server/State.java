package ch.heigvd.dai.server;

import ch.heigvd.dai.Hasher;
import ch.heigvd.dai.PassSecureException;
import ch.heigvd.dai.server.commands.File;

import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

public class State {
    private static Path vaultPath = null;
    private final static String HASH_EXTENSION = ".hs";
    private boolean isLoggedIn;
    private String username;

    public State() throws PassSecureException {
        this.isLoggedIn = false;
        this.username = null;

        if (vaultPath == null)
            throw new PassSecureException(PassSecureException.Type.SERVER_ERROR);
    }

    public Path getUserVault() throws PassSecureException {
        if (!isLoggedIn) throw new PassSecureException(PassSecureException.Type.UNAUTHORIZED);
        return getVaultForUser(this.username);
    }

    private static Path getVaultForUser(String username) {
        return vaultPath.resolve(username);
    }

    public static void setVault(Path vault) {
        vaultPath = vault;
    }

    public void register(String username, String password) throws PassSecureException {
        if (getVaultForUser(username).toFile().isDirectory())
            throw new PassSecureException(PassSecureException.Type.USER_ALREADY_EXISTS);

        if (getVaultForUser(username).toFile().mkdirs())
            throw new PassSecureException(PassSecureException.Type.SERVER_ERROR);

        try {
            String passwordHash = Hasher.hash(password);
            File.write(getVaultForUser(username).resolve(username + HASH_EXTENSION), passwordHash);
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new PassSecureException(PassSecureException.Type.SERVER_ERROR);
        }

        login(username, password);
    }

    public void login(String username, String password) throws PassSecureException {
        if (isLoggedIn)
            throw new PassSecureException(PassSecureException.Type.USER_ALREADY_CONNECTED);

        if (!getVaultForUser(username).toFile().isDirectory())
            throw new PassSecureException(PassSecureException.Type.INVALID_CREDENTIALS);

        try {
            String passwordHash = Hasher.hash(password);
            String storedHash = File.read(getVaultForUser(username).resolve(username + HASH_EXTENSION));

            if (!storedHash.equals(passwordHash))
                throw new PassSecureException(PassSecureException.Type.INVALID_CREDENTIALS);
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new PassSecureException(PassSecureException.Type.SERVER_ERROR);
        }

        this.isLoggedIn = true;
        this.username = username;
    }

    public void disconnect() {
        this.isLoggedIn = false;
        this.username = null;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }
}
