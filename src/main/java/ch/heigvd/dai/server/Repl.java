package ch.heigvd.dai.server;

import ch.heigvd.dai.Command;
import ch.heigvd.dai.PassSecureException;
import ch.heigvd.dai.server.State;
import ch.heigvd.dai.server.commands.Login;

import java.net.Socket;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class Repl {
    private static void sendCommand(BufferedWriter socketOut, Command command) throws PassSecureException {
        try {
            socketOut.write(command + "\n");
            socketOut.flush();
        } catch (IOException e) {
            throw new PassSecureException(PassSecureException.Type.SOCKET_EXCEPTION);
        }
    }

    public static void run(Socket socket, BufferedReader socketIn, BufferedWriter socketOut) throws PassSecureException {
        State state = new State();

        try {
            while (!socket.isClosed()) {
                String line = socketIn.readLine();
                if (line == null) {
                    socket.close();
                    continue;
                }

                Command command = Command.parse(line);

                switch (command.getType()) {
                    case Command.Type.PING:
                        break;
                    case Command.Type.REGISTER:
                        break;
                    case Command.Type.LOGIN:
                        Login.login(state, command);
                        if (!state.isLoggedIn()) {
                            sendCommand(socketOut, new Command(Command.Type.NOK));
                            continue;
                        }
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
                        break;
                }

                // Send OK
                sendCommand(socketOut, new Command(Command.Type.OK));
            }
        } catch (IOException e) {
            throw new PassSecureException(PassSecureException.Type.SOCKET_EXCEPTION);
        }
    }
}
