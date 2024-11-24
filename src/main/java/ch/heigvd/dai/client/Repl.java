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
          case Command.Type.REGISTER, Command.Type.LOGIN, Command.Type.DISCONNECT, Command.Type.ADD, Command.Type.REMOVE:
            sendCommand(socketIn, socketOut, command);
            break;
          case Command.Type.GET: {
            sendCommand(socketIn, socketOut, command);
            String password = command.decrypt(socketIn.readLine());
            System.out.println("Password : " + password);
            break;
          }
          case Command.Type.GENERATE: {
            String password = Generate.generate(command);
            System.out.println("Password : " + password);
            if (command.getBoolean("store")) {
              HashMap<String, String> arguments = command.getArguments();
              arguments.put("password", password);
              sendCommand(socketIn, socketOut, new Command(Command.Type.ADD, arguments));
            }
            break;
          }
          case Command.Type.HELP:
            Help.help();
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
