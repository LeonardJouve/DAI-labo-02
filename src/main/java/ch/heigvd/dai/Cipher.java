package ch.heigvd.dai;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public class Cipher {
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

  private static javax.crypto.Cipher initPBECipher(
      String password, int opmode, IvParameterSpec ivParameterSpec)
      throws GeneralSecurityException {
    PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(SALT, 100_000, ivParameterSpec);
    PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray());
    Key key =
        SecretKeyFactory.getInstance("PBEWithHmacSHA256AndAES_128").generateSecret(pbeKeySpec);
    javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("PBEWithHmacSHA256AndAES_128");
    cipher.init(opmode, key, pbeParameterSpec);
    return cipher;
  }

  public static String decrypt(String content, String password)
      throws IllegalArgumentException, GeneralSecurityException {
    byte[] combined = Base64.getDecoder().decode(content);

    byte[] iv = new byte[16];
    if (combined.length < iv.length) throw new IllegalArgumentException();
    System.arraycopy(combined, 0, iv, 0, iv.length);
    IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
    javax.crypto.Cipher cipher =
        initPBECipher(password, javax.crypto.Cipher.DECRYPT_MODE, ivParameterSpec);

    byte[] ciphertext = new byte[combined.length - iv.length];
    System.arraycopy(combined, iv.length, ciphertext, 0, ciphertext.length);
    byte[] decryptedBytes = cipher.doFinal(ciphertext);

    return new String(decryptedBytes, StandardCharsets.UTF_8);
  }

  public static String encrypt(String content, String password) throws GeneralSecurityException {
    byte[] iv = new byte[16];
    SecureRandom secureRandom = new SecureRandom();
    secureRandom.nextBytes(iv);
    IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
    javax.crypto.Cipher cipher =
        initPBECipher(password, javax.crypto.Cipher.ENCRYPT_MODE, ivParameterSpec);
    byte[] ciphertext = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));

    byte[] combined = new byte[iv.length + ciphertext.length];
    System.arraycopy(iv, 0, combined, 0, iv.length);
    System.arraycopy(ciphertext, 0, combined, iv.length, ciphertext.length);

    return Base64.getEncoder().encodeToString(combined);
  }

  public static String hash(String toHash) throws GeneralSecurityException {
    MessageDigest md = MessageDigest.getInstance("SHA-512");
    md.update(SALT);

    byte[] hashedPassword = md.digest(toHash.getBytes(StandardCharsets.UTF_8));

    return new String(hashedPassword, StandardCharsets.UTF_8);
  }
}
