package ch.heigvd.dai;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Hasher {
    public static String hash(String toHash) throws NoSuchAlgorithmException {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(salt);

        byte[] hashedPassword = md.digest(toHash.getBytes(StandardCharsets.UTF_8));

        return Arrays.toString(hashedPassword);
    }
}