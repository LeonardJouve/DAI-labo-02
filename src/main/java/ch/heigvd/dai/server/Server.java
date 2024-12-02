package ch.heigvd.dai.server;

import ch.heigvd.dai.Cli;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import picocli.CommandLine;

@CommandLine.Command(name = "server", description = "Start the server part of the network game.")
public class Server implements Callable<Integer> {
  @CommandLine.ParentCommand private Cli parent;

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
    State.setVault(parent.getPath());

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
