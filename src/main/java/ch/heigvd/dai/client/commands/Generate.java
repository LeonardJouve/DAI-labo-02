package ch.heigvd.dai.client.commands;

import ch.heigvd.dai.Command;
import ch.heigvd.dai.PassSecureException;

import java.util.Random;

public class Generate {
    public static String generate(Command command) throws PassSecureException {
        if (command == null || command.getType() != Command.Type.GENERATE)
            throw new PassSecureException(PassSecureException.Type.INVALID_ARGUMENT);

        int length = command.getInt("length");
        boolean includeSpecialChar = command.getBoolean("special");
        if (length <= 0) length = 15;

        Random rand = new Random();
        StringBuilder password = new StringBuilder();

        char[] specialChars = {
                '!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '-', '_', '=', '+', '[', ']', '{', '}', ';',
                ':', ',', '.', '<', '>', '/', '?', '\\', '|', '\'', '"', '`', '~'
        };
        int nAlphabet = 'z' - 'a';
        int nDigits = 10;
        int numberOfCharacters = (2 * nAlphabet + nDigits);
        int max = (includeSpecialChar ? numberOfCharacters + specialChars.length : numberOfCharacters);
        int min = 0;

        for (int i = 0; i < length; i++) {
            int randomNum = rand.nextInt((max - min) + 1) + min;
            if (randomNum > numberOfCharacters) {
                password.append(specialChars[randomNum - numberOfCharacters - 1]);
            } else {
                if (randomNum < nDigits) {
                    password.append((char) (randomNum + '0'));
                } else if (randomNum <= nDigits + nAlphabet) {
                    password.append((char) ((randomNum - nDigits) + 'A'));
                } else {
                    password.append((char) ((randomNum - nDigits - nAlphabet) + 'a'));
                }
            }
        }

        return password.toString();
    }
}
