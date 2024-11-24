package ch.heigvd.dai.client;

import ch.heigvd.dai.Command;
import ch.heigvd.dai.PassSecureException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class Repl {
  private static void sendCommand(
      BufferedReader socketIn, BufferedWriter socketOut, Command command)
      throws PassSecureException {
    try {
      command.encrypt();
      socketOut.write(command + "\n");
      socketOut.flush();
      String response = socketIn.readLine();
      if (response == null || !isCommandAccepted(Command.parse(response))) {
        throw new PassSecureException(PassSecureException.Type.BAD_RESPONSE);
      }
    } catch (IOException e) {
      throw new PassSecureException(PassSecureException.Type.SOCKET_EXCEPTION);
    }
  }

  private static boolean isCommandAccepted(Command command) {
    if (command.getType() == Command.Type.NOK)
      System.out.println("Error: " + command.getString("message"));

    return command.getType() == Command.Type.OK;
  }

  public static void run(BufferedReader keyboardIn, BufferedReader socketIn, BufferedWriter socketOut) throws IOException {
    boolean quit = false;
    while (!quit) {
      String line = keyboardIn.readLine();
      if (line == null) break;

      try {
        Command command = Command.parse(line);

        switch (command.getType()) {
          case Command.Type.PING:
            sendCommand(socketIn, socketOut, command);
            System.out.println("PONG");
            break;
          case Command.Type.REGISTER, Command.Type.LOGIN, Command.Type.DISCONNECT:
            sendCommand(socketIn, socketOut, command);
            break;
          case Command.Type.ADD:
            break;
          case Command.Type.GENERATE:
            break;
          case Command.Type.GET:
            break;
          case Command.Type.QUIT:
            quit = true;
            break;
        }
      } catch (PassSecureException e) {
        System.err.println(e.getMessage());
      }
    }
  }
}
