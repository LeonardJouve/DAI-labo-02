package ch.heigvd.dai.server;

import ch.heigvd.dai.Cipher;
import ch.heigvd.dai.PassSecureException;
import ch.heigvd.dai.File;
import java.io.IOException;
import java.nio.file.Path;
import java.security.GeneralSecurityException;

public class State {
  private static Path vaultPath = Path.of("./");
  private static final String HASH_EXTENSION = ".hs";
  private static final String ENTRY_EXTENSION = ".ps";
  private boolean isLoggedIn;
  private String username;

  public State() {
    this.isLoggedIn = false;
    this.username = null;
  }

  public Path getUserVault() throws PassSecureException {
    if (!isLoggedIn) throw new PassSecureException(PassSecureException.Type.UNAUTHORIZED);
    return getVaultForUser(this.username);
  }

  private static Path getVaultForUser(String username) {
    return vaultPath.resolve(username);
  }

  private static void checkForPathTraversal(Path origin, Path destination) throws PassSecureException {
    if (!destination.toAbsolutePath().normalize().startsWith(origin.toAbsolutePath().normalize())) throw new PassSecureException(PassSecureException.Type.UNAUTHORIZED);
  }

  public String getVaultEntry(String name) throws PassSecureException {
    Path entry = getUserVault().resolve(name + ENTRY_EXTENSION);
    checkForPathTraversal(getUserVault(), entry);

    if (!entry.toFile().exists())
      throw new PassSecureException(PassSecureException.Type.ENTRY_NOT_FOUND);

    try {
      return File.read(entry);
    } catch (IOException e) {
      throw new PassSecureException(PassSecureException.Type.SERVER_ERROR);
    }
  }

  public void removeVaultEntry(String name) throws PassSecureException {
    Path entry = getUserVault().resolve(name + ENTRY_EXTENSION);
    checkForPathTraversal(getUserVault(), entry);

    if (!entry.toFile().exists())
      throw new PassSecureException(PassSecureException.Type.ENTRY_NOT_FOUND);

    try {
      if (!entry.toFile().delete())
        throw new PassSecureException(PassSecureException.Type.SERVER_ERROR);
    } catch (Exception e) {
      throw new PassSecureException(PassSecureException.Type.SERVER_ERROR);
    }
  }

  public void addVaultEntry(String name, String password, boolean overwrite) throws PassSecureException {
    Path entry = getUserVault().resolve(name + ENTRY_EXTENSION);
    checkForPathTraversal(getUserVault(), entry);

    if (entry.toFile().exists() && !overwrite)
      throw new PassSecureException(PassSecureException.Type.ENTRY_ALREADY_EXISTS);

    try {
      File.write(entry, password);
    } catch (IOException e) {
      throw new PassSecureException(PassSecureException.Type.SERVER_ERROR);
    }
  }

  public static void setVault(Path vault) {
    vaultPath = vault;
  }

  public void register(String username, String password) throws PassSecureException {
    if (getVaultForUser(username).toFile().isDirectory())
      throw new PassSecureException(PassSecureException.Type.USER_ALREADY_EXISTS);

    checkForPathTraversal(vaultPath, getVaultForUser(username));

    if (!getVaultForUser(username).toFile().mkdirs())
      throw new PassSecureException(PassSecureException.Type.SERVER_ERROR);

    try {
      String passwordHash = Cipher.hash(password);
      File.write(getVaultForUser(username).resolve(username + HASH_EXTENSION), passwordHash);
    } catch (GeneralSecurityException | IOException e) {
      throw new PassSecureException(PassSecureException.Type.SERVER_ERROR);
    }

    login(username, password);
  }

  public void login(String username, String password) throws PassSecureException {
    if (isLoggedIn) throw new PassSecureException(PassSecureException.Type.USER_ALREADY_CONNECTED);

    if (!getVaultForUser(username).toFile().isDirectory())
      throw new PassSecureException(PassSecureException.Type.INVALID_CREDENTIALS);

    checkForPathTraversal(vaultPath, getVaultForUser(username));

    try {
      String passwordHash = Cipher.hash(password);
      String storedHash = File.read(getVaultForUser(username).resolve(username + HASH_EXTENSION));

      if (!storedHash.equals(passwordHash))
        throw new PassSecureException(PassSecureException.Type.INVALID_CREDENTIALS);
    } catch (GeneralSecurityException | IOException e) {
      throw new PassSecureException(PassSecureException.Type.SERVER_ERROR);
    }

    this.isLoggedIn = true;
    this.username = username;
  }

  public void disconnect() {
    this.isLoggedIn = false;
    this.username = null;
  }
}
