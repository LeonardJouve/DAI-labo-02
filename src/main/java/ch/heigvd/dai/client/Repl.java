package ch.heigvd.dai.client;

import ch.heigvd.dai.Command;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class Repl {
    public static void run(BufferedReader in, BufferedWriter out) throws IOException {
        boolean quit = false;

        while (!quit) {
            String line = in.readLine();
            Command command = Command.parse(line);

            switch (command.getType()) {
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
    }
}
