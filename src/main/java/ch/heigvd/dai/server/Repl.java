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
  public Repl(Socket socket) {
    this.socket = socket;
  }

  private static void sendCommand(BufferedWriter socketOut, Command command)
      throws PassSecureException {
    try {
      socketOut.write(command + "\n");
      socketOut.flush();
    } catch (IOException e) {
      throw new PassSecureException(PassSecureException.Type.SOCKET_EXCEPTION);
    }
  }

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

      State state = new State();

      while (!socket.isClosed()) {
        String line = socketIn.readLine();
        if (line == null) break;

        try {
          Command command = Command.parse(line);
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

          sendCommand(socketOut, new Command(Command.Type.OK));
        } catch (PassSecureException e) {
          HashMap<String, String> arguments = new HashMap<>();
          arguments.put("message", e.getMessage());
          sendCommand(socketOut, new Command(Command.Type.NOK, arguments));
        }
      }
    } catch (PassSecureException | IOException e) {
      System.out.println("[Server] exception: " + e.getMessage());
    } finally {
      System.out.println("[Server] Closing connection");
    }
  }
}
