package ch.heigvd.dai.client;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;

import ch.heigvd.dai.Command;
import picocli.CommandLine;

@CommandLine.Command(name = "client", description = "Start the client part of the network game.")
public class Client implements Callable<Integer> {

  @CommandLine.Option(
      names = {"-H", "--host"},
      description = "Host to connect to.",
      required = true)
  protected String host;

  @CommandLine.Option(
      names = {"-p", "--port"},
      description = "Port to use (default: ${DEFAULT-VALUE}).",
      defaultValue = "6433")
  protected int port;

  @Override
  public Integer call() {
    try (Socket socket = new Socket(host, port);
         Reader reader = new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8);
         BufferedReader in = new BufferedReader(reader);
         Writer writer = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8);
         BufferedWriter out = new BufferedWriter(writer)
    ) {
      System.out.println("[Client] Connected to " + host + ":" + port);
      System.out.println();
      boolean quit = false;

      while (!quit) {
        String line = in.readLine();
        Command command = Command.parse(line);

        switch (command.getType()) {
          case Command.Type.REGISTER:
            break;
          case Command.Type.LOGIN:
            break;
          case Command.Type.ADD:
            break;
          case Command.Type.GENERATE:
            break;
          case Command.Type.GET:
              break;
          case Command.Type.DISCONNECT:
            break;
          case Command.Type.QUIT:
            quit = true;
            break;
        }
      }
    } catch (IOException e) {
      throw new UnsupportedOperationException(e);
    }

    return 0;
  }
}
