package ch.heigvd.dai.server;

import ch.heigvd.dai.Command;
import ch.heigvd.dai.PassSecureException;
import ch.heigvd.dai.server.commands.Login;
import ch.heigvd.dai.server.commands.Register;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

public class Repl {
  private static void sendCommand(BufferedWriter socketOut, Command command)
      throws PassSecureException {
    try {
      socketOut.write(command + "\n");
      socketOut.flush();
    } catch (IOException e) {
      throw new PassSecureException(PassSecureException.Type.SOCKET_EXCEPTION);
    }
  }

  public static void run(Socket socket, BufferedReader socketIn, BufferedWriter socketOut) throws IOException, PassSecureException {
    State state = new State();

    while (!socket.isClosed()) {
      String line = socketIn.readLine();
      if (line == null) break;

      try {
        Command command = Command.parse(line);
        switch (command.getType()) {
          case Command.Type.PING:
            break;
          case Command.Type.REGISTER:
            Register.register(state, command);
            break;
          case Command.Type.LOGIN:
            Login.login(state, command);
            break;
          case Command.Type.ADD:
            break;
          case Command.Type.GENERATE:
            break;
          case Command.Type.GET:
            break;
          case Command.Type.DISCONNECT:
            state.disconnect();
            break;
        }

        sendCommand(socketOut, new Command(Command.Type.OK));
      } catch (PassSecureException e) {
        HashMap<String, String> arguments = new HashMap<>();
        arguments.put("message", e.getMessage());
        sendCommand(socketOut, new Command(Command.Type.NOK, arguments));
      }
    }
  }
}
