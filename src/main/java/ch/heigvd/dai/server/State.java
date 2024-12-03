/**
 * The {@code State} class manages the state of a user session on the server side of the pass-secure
 * system. It handles user authentication, vault management, and entry operations while ensuring
 * security and access control.
 */
package ch.heigvd.dai.server;

import ch.heigvd.dai.Cipher;
import ch.heigvd.dai.File;
import ch.heigvd.dai.PassSecureException;
import java.io.IOException;
import java.nio.file.Path;
import java.security.GeneralSecurityException;

public class State {

  private static Path vaultPath = Path.of("./");
  private static final String HASH_EXTENSION = ".hs";
  private static final String ENTRY_EXTENSION = ".ps";
  private boolean isLoggedIn;
  private String username;

  /** Constructs a new {@code State} object with no user logged in. */
  public State() {
    this.isLoggedIn = false;
    this.username = null;
  }

  /**
   * Retrieves the vault path for the currently logged-in user.
   *
   * @return The {@link Path} representing the user's vault directory.
   * @throws PassSecureException If no user is logged in or access is unauthorized.
   */
  public Path getUserVault() throws PassSecureException {
    if (!isLoggedIn) throw new PassSecureException(PassSecureException.Type.UNAUTHORIZED);
    return getVaultForUser(this.username);
  }

  /**
   * Retrieves the vault path for a specific username.
   *
   * @param username The username for which to retrieve the vault path.
   * @return The {@link Path} representing the vault directory for the user.
   */
  private static Path getVaultForUser(String username) {
    return vaultPath.resolve(username);
  }

  /**
   * Validates that a destination path does not escape the origin directory.
   *
   * @param origin The origin directory.
   * @param destination The destination path.
   * @throws PassSecureException If the destination path is outside the origin directory.
   */
  private static void checkForPathTraversal(Path origin, Path destination)
      throws PassSecureException {
    if (!destination.toAbsolutePath().normalize().startsWith(origin.toAbsolutePath().normalize())) {
      throw new PassSecureException(PassSecureException.Type.UNAUTHORIZED);
    }
  }

  /**
   * Retrieves a password entry from the user's vault.
   *
   * @param name The name of the entry to retrieve.
   * @return The password associated with the entry.
   * @throws PassSecureException If the user is not logged in, the entry is not found, or an error
   *     occurs.
   */
  public String getVaultEntry(String name) throws PassSecureException {
    Path entry = getUserVault().resolve(name + ENTRY_EXTENSION);
    checkForPathTraversal(getUserVault(), entry);

    if (!entry.toFile().exists()) {
      throw new PassSecureException(PassSecureException.Type.ENTRY_NOT_FOUND);
    }

    try {
      return File.read(entry);
    } catch (IOException e) {
      throw new PassSecureException(PassSecureException.Type.SERVER_ERROR);
    }
  }

  /**
   * Removes a password entry from the user's vault.
   *
   * @param name The name of the entry to remove.
   * @throws PassSecureException If the user is not logged in, the entry is not found, or an error
   *     occurs.
   */
  public void removeVaultEntry(String name) throws PassSecureException {
    Path entry = getUserVault().resolve(name + ENTRY_EXTENSION);
    checkForPathTraversal(getUserVault(), entry);

    if (!entry.toFile().exists()) {
      throw new PassSecureException(PassSecureException.Type.ENTRY_NOT_FOUND);
    }

    try {
      if (!entry.toFile().delete()) {
        throw new PassSecureException(PassSecureException.Type.SERVER_ERROR);
      }
    } catch (Exception e) {
      throw new PassSecureException(PassSecureException.Type.SERVER_ERROR);
    }
  }

  /**
   * Adds a password entry to the user's vault.
   *
   * @param name The name of the entry.
   * @param password The password to store.
   * @param overwrite Whether to overwrite an existing entry with the same name.
   * @throws PassSecureException If the user is not logged in, the entry already exists and
   *     overwrite is false, or an error occurs.
   */
  public void addVaultEntry(String name, String password, boolean overwrite)
      throws PassSecureException {
    Path entry = getUserVault().resolve(name + ENTRY_EXTENSION);
    checkForPathTraversal(getUserVault(), entry);

    if (entry.toFile().exists() && !overwrite) {
      throw new PassSecureException(PassSecureException.Type.ENTRY_ALREADY_EXISTS);
    }

    try {
      File.write(entry, password);
    } catch (IOException e) {
      throw new PassSecureException(PassSecureException.Type.SERVER_ERROR);
    }
  }

  /**
   * Sets the path to the vault for the server.
   *
   * @param vault The {@link Path} to use as the vault directory.
   */
  public static void setVault(Path vault) {
    vaultPath = vault;
  }

  /**
   * Registers a new user by creating a vault directory and storing the hashed password.
   *
   * @param username The username of the new user.
   * @param password The password for the new user.
   * @throws PassSecureException If the user already exists, the vault directory cannot be created,
   *     or an error occurs during the operation.
   */
  public void register(String username, String password) throws PassSecureException {
    if (getVaultForUser(username).toFile().isDirectory()) {
      throw new PassSecureException(PassSecureException.Type.USER_ALREADY_EXISTS);
    }

    checkForPathTraversal(vaultPath, getVaultForUser(username));

    if (!getVaultForUser(username).toFile().mkdirs()) {
      throw new PassSecureException(PassSecureException.Type.SERVER_ERROR);
    }

    try {
      String passwordHash = Cipher.hash(password);
      File.write(getVaultForUser(username).resolve(username + HASH_EXTENSION), passwordHash);
    } catch (GeneralSecurityException | IOException e) {
      throw new PassSecureException(PassSecureException.Type.SERVER_ERROR);
    }

    login(username, password);
  }

  /**
   * Logs in a user by validating their credentials.
   *
   * @param username The username to log in.
   * @param password The password for the user.
   * @throws PassSecureException If the user is already logged in, the credentials are invalid, or
   *     an error occurs.
   */
  public void login(String username, String password) throws PassSecureException {
    if (isLoggedIn) {
      throw new PassSecureException(PassSecureException.Type.USER_ALREADY_CONNECTED);
    }

    if (!getVaultForUser(username).toFile().isDirectory()) {
      throw new PassSecureException(PassSecureException.Type.INVALID_CREDENTIALS);
    }

    checkForPathTraversal(vaultPath, getVaultForUser(username));

    try {
      String passwordHash = Cipher.hash(password);
      String storedHash = File.read(getVaultForUser(username).resolve(username + HASH_EXTENSION));

      if (!storedHash.equals(passwordHash)) {
        throw new PassSecureException(PassSecureException.Type.INVALID_CREDENTIALS);
      }
    } catch (GeneralSecurityException | IOException e) {
      throw new PassSecureException(PassSecureException.Type.SERVER_ERROR);
    }

    this.isLoggedIn = true;
    this.username = username;
  }

  /** Disconnects the currently logged-in user. */
  public void disconnect() {
    this.isLoggedIn = false;
    this.username = null;
  }
}
