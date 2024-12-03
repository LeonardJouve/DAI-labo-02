/**
 * The {@code Cipher} class provides utility methods for encrypting, decrypting, and hashing strings
 * using secure algorithms. It is designed for use in the pass-secure system to handle sensitive
 * data.
 */
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
    (byte) 0xc9, (byte) 0x36, (byte) 0x78, (byte) 0x99,
    (byte) 0x52, (byte) 0x3e, (byte) 0xea, (byte) 0xf2
  };

  /**
   * Initializes a cipher for encryption or decryption using password-based encryption (PBE).
   *
   * @param password The password used to derive the encryption key.
   * @param opmode The cipher operation mode (e.g., {@code Cipher.ENCRYPT_MODE}).
   * @param ivParameterSpec The initialization vector (IV) specification.
   * @return The initialized {@link javax.crypto.Cipher} object.
   * @throws GeneralSecurityException If an error occurs during initialization.
   */
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

  /**
   * Decrypts a Base64-encoded, encrypted string using the provided password.
   *
   * @param content The encrypted content to decrypt, encoded in Base64.
   * @param password The password used for decryption.
   * @return The decrypted content as a plain text string.
   * @throws IllegalArgumentException If the provided content is invalid.
   * @throws GeneralSecurityException If a security error occurs during decryption.
   */
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

  /**
   * Encrypts a string using the provided password and returns the result as a Base64-encoded
   * string.
   *
   * @param content The plain text content to encrypt.
   * @param password The password used for encryption.
   * @return The encrypted content, encoded in Base64.
   * @throws GeneralSecurityException If a security error occurs during encryption.
   */
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

  /**
   * Hashes a string using SHA-512 and a predefined salt value.
   *
   * @param toHash The string to hash.
   * @return The hashed string.
   * @throws GeneralSecurityException If a security error occurs during hashing.
   */
  public static String hash(String toHash) throws GeneralSecurityException {
    MessageDigest md = MessageDigest.getInstance("SHA-512");
    md.update(SALT);

    byte[] hashedPassword = md.digest(toHash.getBytes(StandardCharsets.UTF_8));

    return new String(hashedPassword, StandardCharsets.UTF_8);
  }
}
