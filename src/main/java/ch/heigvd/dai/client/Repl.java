package ch.heigvd.dai.client;

import ch.heigvd.dai.Command;
import ch.heigvd.dai.PassSecureException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class Repl {
    private static void sendCommand(BufferedReader socketIn, BufferedWriter socketOut, Command command) throws PassSecureException {
        try {
            socketOut.write(command + "\n");
            socketOut.flush();
            String response = socketIn.readLine();
            if (response == null || !isCommandAccepted(response)) throw new PassSecureException(PassSecureException.Type.BAD_RESPONSE);
        } catch (IOException e) {
            throw new PassSecureException(PassSecureException.Type.SOCKET_EXCEPTION);
        }
    }

    private static boolean isCommandAccepted(String command) {
        return Command.parse(command).getType() == Command.Type.OK;
    }

    public static void run(BufferedReader keyboardIn, BufferedReader socketIn, BufferedWriter socketOut) throws PassSecureException {
        boolean quit = false;
        try {
            while (!quit) {
                String line = keyboardIn.readLine();
                Command command = Command.parse(line);

                switch (command.getType()) {
                    case Command.Type.PING:
                        sendCommand(socketIn, socketOut, new Command(Command.Type.PING));
                        System.out.println("PONG");
                        break;
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
            throw new PassSecureException(PassSecureException.Type.SOCKET_EXCEPTION);
        }
    }
}
