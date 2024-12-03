/**
 * The {@code Repl} class implements a server-side Read-Eval-Print Loop (REPL) for handling client
 * connections and processing commands in the pass-secure system. Each instance of this class is
 * responsible for managing a single client connection.
 */
package ch.heigvd.dai.server;

import ch.heigvd.dai.Command;
import ch.heigvd.dai.PassSecureException;
import ch.heigvd.dai.server.commands.*;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class Repl implements Runnable {

  private final Socket socket;

  /**
   * Constructs a new {@code Repl} instance for the given client socket.
   *
   * @param socket The {@link Socket} representing the client connection.
   */
  public Repl(Socket socket) {
    this.socket = socket;
  }

  /**
   * Sends a {@link Command} to the client through the socket's output stream.
   *
   * @param socketOut The {@link BufferedWriter} to write the command to.
   * @param command The {@link Command} to be sent.
   * @throws PassSecureException If an I/O error occurs while sending the command.
   */
  private static void sendCommand(BufferedWriter socketOut, Command command)
      throws PassSecureException {
    try {
      socketOut.write(command + "\n");
      socketOut.flush();
    } catch (IOException e) {
      throw new PassSecureException(PassSecureException.Type.SOCKET_EXCEPTION);
    }
  }

  /**
   * The main logic of the REPL. This method handles client commands, processes them using the
   * server's state, and sends responses back to the client. It continues to run until the client
   * disconnects or the socket is closed.
   */
  @Override
  public void run() {
    System.out.println(
        "[Server] New client connected from "
            + socket.getInetAddress().getHostAddress()
            + ":"
            + socket.getPort());
    try (socket;
        Reader reader = new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8);
        BufferedReader socketIn = new BufferedReader(reader);
        Writer writer = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8);
        BufferedWriter socketOut = new BufferedWriter(writer)) {

      // Initialize the server state for the client
      State state = new State();

      // Main loop to handle client commands
      while (!socket.isClosed()) {
        String line = socketIn.readLine();
        if (line == null) break; // Client disconnected

        try {
          // Parse the command received from the client
          Command command = Command.parse(line);

          // Process the command based on its type
          switch (command.getType()) {
            case Command.Type.REGISTER:
              Register.register(state, command);
              break;

            case Command.Type.LOGIN:
              Login.login(state, command);
              break;

            case Command.Type.ADD:
              Add.add(state, command);
              break;

            case Command.Type.GET:
              String password = Get.get(state, command);
              sendCommand(socketOut, new Command(Command.Type.OK));
              socketOut.write(password + "\n");
              socketOut.flush();
              continue;

            case Command.Type.REMOVE:
              Remove.remove(state, command);
              break;

            case Command.Type.DISCONNECT:
              state.disconnect();
              break;
          }

          // Send success response
          sendCommand(socketOut, new Command(Command.Type.OK));
        } catch (PassSecureException e) {
          // Handle and send error responses
          HashMap<String, String> arguments = new HashMap<>();
          arguments.put("message", e.getMessage());
          sendCommand(socketOut, new Command(Command.Type.NOK, arguments));
        }
      }
    } catch (PassSecureException | IOException e) {
      // Log exceptions for debugging purposes
      System.out.println("[Server] exception: " + e.getMessage());
    } finally {
      // Clean up and close the connection
      System.out.println("[Server] Closing connection");
    }
  }
}
