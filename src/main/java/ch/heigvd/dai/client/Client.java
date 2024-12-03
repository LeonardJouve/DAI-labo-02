/**
 * The {@code Client} class is responsible for initiating the client-side application of the
 * pass-secure system. It establishes a connection to a server, manages input and output streams,
 * and runs a REPL (Read-Eval-Print Loop) for user interaction.
 */
package ch.heigvd.dai.client;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import picocli.CommandLine;

@CommandLine.Command(name = "client", description = "Start the client part of pass-secure.")
public class Client implements Callable<Integer> {

  /** The host to connect to. Defaults to "localhost" if not provided. */
  @CommandLine.Option(
      names = {"-H", "--host"},
      description = "Host to connect to.",
      defaultValue = "localhost")
  private String host;

  /** The port to connect to. Defaults to 6433 if not provided. */
  @CommandLine.Option(
      names = {"-p", "--port"},
      description = "Port to use (default: ${DEFAULT-VALUE}).",
      defaultValue = "6433")
  private int port;

  /**
   * Executes the client application by:
   *
   * <ul>
   *   <li>Establishing a socket connection to the specified host and port.
   *   <li>Setting up input and output streams for communication with the server and user input.
   *   <li>Starting a REPL (Read-Eval-Print Loop) to handle user commands and interact with the
   *       server.
   * </ul>
   *
   * @return {@code 0} on successful termination of the client application.
   * @throws UnsupportedOperationException If an {@link IOException} occurs during setup or
   *     operation.
   */
  @Override
  public Integer call() {
    try (Reader keyboardReader = new InputStreamReader(System.in, StandardCharsets.UTF_8);
        BufferedReader keyboardIn = new BufferedReader(keyboardReader);
        Socket socket = new Socket(host, port);
        Reader socketReader =
            new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8);
        BufferedReader socketIn = new BufferedReader(socketReader);
        Writer socketWriter =
            new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8);
        BufferedWriter socketOut = new BufferedWriter(socketWriter)) {
      // Notify user of successful connection
      System.out.println("[Client] Connected to " + host + ":" + port);
      System.out.println();

      // Start the REPL for user interaction
      Repl.run(keyboardIn, socketIn, socketOut);

      // Notify user of connection closure
      System.out.println("[Client] Closing connection");
    } catch (IOException e) {
      // Rethrow exceptions as UnsupportedOperationException
      throw new UnsupportedOperationException(e);
    }

    // Indicate successful termination
    return 0;
  }
}
