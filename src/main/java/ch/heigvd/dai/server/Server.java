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
   * The path to the vault where the passwords are stored. If not specified, it defaults to the
   * current directory.
   */
  @CommandLine.Option(
          names = {"-p", "--path"},
          description = "Path of the vault used to store and retrieve passwords.",
          required = false,
          defaultValue = "./")
  private String path;

  /**
   * Retrieves the vault path as a Path object.
   *
   * @return The Path representing the location of the vault.
   */
  private Path getPath() {
    return Path.of(path);
  }

  @CommandLine.Option(
      names = {"-p", "--port"},
      description = "Port to use (default: ${DEFAULT-VALUE}).",
      defaultValue = "6433")
  private int port;

  @CommandLine.Option(
      names = {"-t", "--thread"},
      description = "Maximum amount of threads (default: ${DEFAULT-VALUE}).",
      defaultValue = "5")
  private int thread;

  @Override
  public Integer call() {
    State.setVault(getPath());

    try (ServerSocket serverSocket = new ServerSocket(port);
        ExecutorService executor = Executors.newFixedThreadPool(thread)) {
      System.out.println("[Server] Listening on port " + port);

      while (!serverSocket.isClosed()) {
        Socket socket = serverSocket.accept();
        executor.submit(new Repl(socket));
      }
    } catch (IOException e) {
      System.out.println("[Server] exception: " + e);
    }

    return 0;
  }
}
