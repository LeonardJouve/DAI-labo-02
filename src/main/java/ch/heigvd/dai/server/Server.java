package ch.heigvd.dai.server;

import ch.heigvd.dai.Cli;
import ch.heigvd.dai.PassSecureException;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import picocli.CommandLine;

@CommandLine.Command(name = "server", description = "Start the server part of the network game.")
public class Server implements Callable<Integer> {
  @CommandLine.ParentCommand private Cli parent;

  @CommandLine.Option(
      names = {"-p", "--port"},
      description = "Port to use (default: ${DEFAULT-VALUE}).",
      defaultValue = "6433")
  private int port;

  @Override
  public Integer call() {
    State.setVault(parent.getPath());

    try (ServerSocket serverSocket = new ServerSocket(port)) {
      System.out.println("[Server] Listening on port " + port);

      while (!serverSocket.isClosed()) {
        try (Socket socket = serverSocket.accept();
            Reader reader = new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8);
            BufferedReader in = new BufferedReader(reader);
            Writer writer =
                new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8);
            BufferedWriter out = new BufferedWriter(writer)) {
          System.out.println(
              "[Server] New client connected from "
                  + socket.getInetAddress().getHostAddress()
                  + ":"
                  + socket.getPort());

          Repl.run(socket, in, out);

          System.out.println("[Server] Closing connection");
        } catch (PassSecureException e) {
          System.out.println(e.getMessage());
        } catch (IOException e) {
          System.out.println("[Server] IO exception: " + e);
        }
      }
    } catch (IOException e) {
      System.out.println("[Server] IO exception: " + e);
    }

    return 0;
  }
}
