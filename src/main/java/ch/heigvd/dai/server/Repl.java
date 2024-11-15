package ch.heigvd.dai.server;

import ch.heigvd.dai.Command;

import java.net.Socket;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class Repl {
    public static void run(Socket socket, BufferedReader socketIn, BufferedWriter socketOut) throws IOException {
        while (!socket.isClosed()) {
            String line = socketIn.readLine();
            Command command = Command.parse(line);
            System.out.println(command.getType());

            if (line == null) {
                socket.close();
                continue;
            }

            switch (command.getType()) {
                case Command.Type.PING:
                    socketOut.write(Command.Type.PONG + "\n");
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
                    break;
            }

            // Send OK
            socketOut.write(Command.Type.OK + "\n");
        }
        socketOut.flush();
    }
}
