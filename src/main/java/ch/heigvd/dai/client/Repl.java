/**
 * The {@code Repl} class implements a Read-Eval-Print Loop (REPL) for user interaction in the
 * pass-secure system. It handles user commands, communicates with the server, and processes server
 * responses.
 */
package ch.heigvd.dai.client;

import ch.heigvd.dai.Command;
import ch.heigvd.dai.PassSecureException;
import ch.heigvd.dai.client.commands.Generate;
import ch.heigvd.dai.client.commands.Help;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;

public class Repl {

  /**
   * Sends a {@link Command} to the server, encrypts it, and processes the server's response.
   *
   * @param socketIn BufferedReader to read server responses.
   * @param socketOut BufferedWriter to send commands to the server.
   * @param command The command to be sent to the server.
   * @throws PassSecureException If an error occurs during command encryption, sending, or
   *     processing the server's response.
   */
  private static void sendCommand(
      BufferedReader socketIn, BufferedWriter socketOut, Command command)
      throws PassSecureException {
    try {
      // Encrypt the command and send it to the server
      command.encrypt();
      socketOut.write(command + "\n");
      socketOut.flush();

      // Read and validate the server's response
      String response = socketIn.readLine();
      if (response == null || !isCommandAccepted(Command.parse(response))) {
        throw new PassSecureException(PassSecureException.Type.BAD_RESPONSE);
      }
    } catch (IOException e) {
      throw new PassSecureException(PassSecureException.Type.SOCKET_EXCEPTION);
    }
  }

  /**
   * Checks if a server response command indicates success.
   *
   * @param command The server's response command.
   * @return {@code true} if the command type is {@code Command.Type.OK}, {@code false} otherwise.
   */
  private static boolean isCommandAccepted(Command command) {
    if (command.getType() == Command.Type.NOK)
      System.out.println("Error: " + command.getString("message"));
    return command.getType() == Command.Type.OK;
  }

  /**
   * Runs the REPL loop to handle user commands. Processes input from the user, communicates with
   * the server, and displays appropriate responses.
   *
   * @param keyboardIn BufferedReader for user input.
   * @param socketIn BufferedReader for server responses.
   * @param socketOut BufferedWriter to send commands to the server.
   * @throws IOException If an I/O error occurs during communication.
   */
  public static void run(
      BufferedReader keyboardIn, BufferedReader socketIn, BufferedWriter socketOut)
      throws IOException {
    // Display the welcome banner and instructions
    System.out.println(
        """
                ██████╗  █████╗ ███████╗███████╗      ███████╗███████╗ ██████╗██╗   ██╗██████╗ ███████╗
                ██╔══██╗██╔══██╗██╔════╝██╔════╝      ██╔════╝██╔════╝██╔════╝██║   ██║██╔══██╗██╔════╝
                ██████╔╝███████║███████╗███████╗█████╗███████╗█████╗  ██║     ██║   ██║██████╔╝█████╗
                ██╔═══╝ ██╔══██║╚════██║╚════██║╚════╝╚════██║██╔══╝  ██║     ██║   ██║██╔══██╗██╔══╝
                ██║     ██║  ██║███████║███████║      ███████║███████╗╚██████╗╚██████╔╝██║  ██║███████╗
                ╚═╝     ╚═╝  ╚═╝╚══════╝╚══════╝      ╚══════╝╚══════╝ ╚═════╝ ╚═════╝ ╚═╝  ╚═╝╚══════╝

                Type "HELP" to get a list of commands
                """);

    boolean quit = false;

    // Begin the REPL loop
    while (!quit) {
      String line = keyboardIn.readLine();
      if (line == null) break;

      try {
        // Parse the user input into a Command object
        Command command = Command.parse(line);

        // Process the command based on its type
        switch (command.getType()) {
          case Command.Type.PING:
            sendCommand(socketIn, socketOut, command);
            System.out.println("PONG");
            break;

          case Command.Type.REGISTER,
              Command.Type.LOGIN,
              Command.Type.DISCONNECT,
              Command.Type.ADD,
              Command.Type.REMOVE:
            sendCommand(socketIn, socketOut, command);
            break;

          case Command.Type.GET:
            sendCommand(socketIn, socketOut, command);
            String password = command.decrypt(socketIn.readLine());
            System.out.println("Password : " + password);
            break;

          case Command.Type.GENERATE:
            String generatedPassword = Generate.generate(command);
            System.out.println("Password : " + generatedPassword);
            if (command.getBoolean("store")) {
              HashMap<String, String> arguments = command.getArguments();
              arguments.put("password", generatedPassword);
              sendCommand(socketIn, socketOut, new Command(Command.Type.ADD, arguments));
            }
            break;

          case Command.Type.HELP:
            Help.help();
            break;

          case Command.Type.QUIT:
            quit = true;
            break;
        }
      } catch (PassSecureException e) {
        // Handle and display exceptions to the user
        System.err.println(e.getMessage());
      }
    }
  }
}
