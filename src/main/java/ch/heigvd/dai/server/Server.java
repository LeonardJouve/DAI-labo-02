/**
 * The {@code Server} class is responsible for starting and managing the server-side application of
 * the pass-secure system. It handles client connections, delegates requests to REPL handlers, and
 * manages a thread pool for concurrent client processing.
 */
package ch.heigvd.dai.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import picocli.CommandLine;

@CommandLine.Command(name = "server", description = "Start the server part of the pass-secure.")
public class Server implements Callable<Integer> {

  /**
   * The path to the vault where the passwords are stored. Defaults to the current directory if not
   * specified.
   */
  @CommandLine.Option(
      names = {"-v", "--vault"},
      description = "Path of the vault used to store and retrieve passwords.",
      required = false,
      defaultValue = "./")
  private String vault;

  /**
   * Retrieves the vault path as a {@link Path} object.
   *
   * @return The {@link Path} representing the location of the vault.
   */
  private Path getVault() {
    return Path.of(vault);
  }

  /**
   * The port number on which the server listens for incoming client connections. Defaults to 6433
   * if not specified.
   */
  @CommandLine.Option(
      names = {"-p", "--port"},
      description = "Port to use (default: ${DEFAULT-VALUE}).",
      defaultValue = "6433")
  private int port;

  /**
   * The maximum number of threads available in the server's thread pool for handling client
   * connections. Defaults to 5 if not specified.
   */
  @CommandLine.Option(
      names = {"-t", "--thread"},
      description = "Maximum amount of threads (default: ${DEFAULT-VALUE}).",
      defaultValue = "5")
  private int thread;

  /**
   * Starts the server by listening on the specified port, accepting client connections, and
   * delegating each connection to a {@link Repl} instance managed by a thread pool.
   *
   * @return {@code 0} on successful server shutdown.
   */
  @Override
  public Integer call() {
    // Set the vault path for the server state
    State.setVault(getVault());

    try (
    // Initialize the server socket and thread pool
    ServerSocket serverSocket = new ServerSocket(port);
        ExecutorService executor = Executors.newFixedThreadPool(thread)) {
      System.out.println("[Server] Listening on port " + port);

      // Main loop to accept and handle client connections
      while (!serverSocket.isClosed()) {
        Socket socket = serverSocket.accept();
        executor.submit(new Repl(socket)); // Delegate client handling to a new Repl instance
      }
    } catch (IOException e) {
      // Log any exceptions during server operation
      System.out.println("[Server] exception: " + e);
    }

    return 0; // Indicate successful termination
  }
}
