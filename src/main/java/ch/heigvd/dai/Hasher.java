package ch.heigvd.dai;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hasher {
  private static final byte[] SALT = {
    (byte) 0xc9,
    (byte) 0x36,
    (byte) 0x78,
    (byte) 0x99,
    (byte) 0x52,
    (byte) 0x3e,
    (byte) 0xea,
    (byte) 0xf2
  };

  public static String hash(String toHash) throws NoSuchAlgorithmException {
    MessageDigest md = MessageDigest.getInstance("SHA-512");
    md.update(SALT);

    byte[] hashedPassword = md.digest(toHash.getBytes(StandardCharsets.UTF_8));

    return new String(hashedPassword, StandardCharsets.UTF_8);
  }
}
