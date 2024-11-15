package ch.heigvd.dai.client;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;

import ch.heigvd.dai.PassSecureException;
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
    try (
        Reader keyboardReader = new InputStreamReader(System.in, StandardCharsets.UTF_8);
        BufferedReader keyboardIn = new BufferedReader(keyboardReader);
        Socket socket = new Socket(host, port);
        Reader socketReader = new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8);
        BufferedReader socketIn = new BufferedReader(socketReader);
        Writer socketWriter = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8);
        BufferedWriter socketOut = new BufferedWriter(socketWriter)
    ) {
      System.out.println("[Client] Connected to " + host + ":" + port);
      System.out.println();

      Repl.run(keyboardIn, socketIn, socketOut);
    } catch (PassSecureException e) {
      System.out.println(e.getMessage());
    } catch (IOException e) {
      throw new UnsupportedOperationException(e);
    }

    return 0;
  }
}
