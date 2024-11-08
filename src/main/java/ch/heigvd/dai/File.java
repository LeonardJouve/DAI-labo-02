/**
 * The File class provides utilities to manage password storage in a secure way. It includes methods
 * to read and write password entries, with optional encryption using PBE (Password-Based
 * Encryption) with HMAC-SHA256 and AES-128.
 *
 * <p>This class allows: - Storing passwords securely with encryption. - Retrieving passwords from
 * encrypted or plain text vault entries. - Verifying the existence of vault entries.
 *
 * @author Leonard Jouve
 * @author Ali Zoubir
 * @version 1.0
 */
package ch.heigvd.dai;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public class File {

  /** The file extension used for vault entries. */
  private static final String EXTENSION = ".ps";

  /** A predefined salt value used for password-based encryption (PBE). */
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

  /**
   * Initializes a PBE cipher with the provided password, operation mode, and IV (Initialization
   * Vector).
   *
   * @param password The password used to generate the encryption key.
   * @param opmode The cipher mode (Cipher.ENCRYPT_MODE or Cipher.DECRYPT_MODE).
   * @param ivParameterSpec The initialization vector for the encryption algorithm.
   * @return A configured Cipher instance for encryption or decryption.
   * @throws GeneralSecurityException If an error occurs during cipher initialization.
   */
  private static Cipher initPBECipher(String password, int opmode, IvParameterSpec ivParameterSpec)
      throws GeneralSecurityException {
    PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(SALT, 100_000, ivParameterSpec);
    PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray());
    Key key =
        SecretKeyFactory.getInstance("PBEWithHmacSHA256AndAES_128").generateSecret(pbeKeySpec);
    Cipher cipher = Cipher.getInstance("PBEWithHmacSHA256AndAES_128");
    cipher.init(opmode, key, pbeParameterSpec);
    return cipher;
  }

  /**
   * Returns the full path for a vault entry by appending the file extension.
   *
   * @param path The base directory path.
   * @param name The name of the vault entry.
   * @return The full path for the vault entry.
   */
  public static Path getVaultEntryPath(Path path, String name) {
    return path.resolve(name + File.EXTENSION);
  }

  /**
   * Reads content from a BufferedInputStream and returns it as a String.
   *
   * @param bis The BufferedInputStream from which to read.
   * @return The content read from the stream as a String.
   * @throws IOException If an I/O error occurs.
   */
  public static String read(BufferedInputStream bis) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    byte[] buffer = new byte[1024];
    int amountRead;

    while ((amountRead = bis.read(buffer)) != -1) {
      baos.write(buffer, 0, amountRead);
    }

    return baos.toString(StandardCharsets.UTF_8);
  }

  /**
   * Checks if a file exists at the given path.
   *
   * @param path The path of the file to check.
   * @return True if the file exists, false otherwise.
   */
  public static boolean exists(Path path) {
    return path.toFile().exists();
  }

  /**
   * Reads a vault entry from the specified path and returns its content as a String.
   *
   * @param path The path of the vault entry.
   * @return The content of the vault entry as a String.
   * @throws IOException If an I/O error occurs.
   */
  public static String read(Path path) throws IOException {
    String content;

    try (FileInputStream fis = new FileInputStream(path.toString());
        BufferedInputStream bis = new BufferedInputStream(fis)) {
      content = File.read(bis);
    }

    return content;
  }

  /**
   * Reads an encrypted vault entry from the specified path using the given password.
   *
   * @param path The path of the vault entry.
   * @param password The password used for decrypting the vault entry.
   * @return The decrypted content of the vault entry as a String.
   * @throws IOException If an I/O error occurs.
   * @throws GeneralSecurityException If a decryption error occurs.
   */
  public static String read(Path path, String password)
      throws IOException, GeneralSecurityException {
    String content;

    try (FileInputStream fis = new FileInputStream(path.toString());
        BufferedInputStream bfis = new BufferedInputStream(fis)) {
      byte[] iv = bfis.readNBytes(16);
      IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
      Cipher cipher = File.initPBECipher(password, Cipher.DECRYPT_MODE, ivParameterSpec);

      try (CipherInputStream cis = new CipherInputStream(bfis, cipher);
          BufferedInputStream bcis = new BufferedInputStream(cis)) {
        content = File.read(bcis);
      }
    }

    return content;
  }

  /**
   * Writes the provided byte array content to a BufferedOutputStream.
   *
   * @param bos The BufferedOutputStream where the content will be written.
   * @param content The content to write.
   * @throws IOException If an I/O error occurs.
   */
  public static void write(BufferedOutputStream bos, byte[] content) throws IOException {
    bos.write(content);
    bos.flush();
  }

  /**
   * Writes the provided String content to a BufferedOutputStream.
   *
   * @param bos The BufferedOutputStream where the content will be written.
   * @param content The content to write as a String.
   * @throws IOException If an I/O error occurs.
   */
  public static void write(BufferedOutputStream bos, String content) throws IOException {
    File.write(bos, content.getBytes(StandardCharsets.UTF_8));
  }

  /**
   * Writes the provided content to a file at the specified path.
   *
   * @param path The path of the file.
   * @param content The content to write to the file.
   * @throws IOException If an I/O error occurs.
   */
  public static void write(Path path, String content) throws IOException {
    try (FileOutputStream fos = new FileOutputStream(path.toString());
        BufferedOutputStream bos = new BufferedOutputStream(fos)) {
      File.write(bos, content);
    }
  }

  /**
   * Encrypts and writes the provided content to a file at the specified path using the given
   * password.
   *
   * @param path The path of the file.
   * @param content The content to write and encrypt.
   * @param password The password used for encryption.
   * @throws IOException If an I/O error occurs.
   * @throws GeneralSecurityException If an encryption error occurs.
   */
  public static void write(Path path, String content, String password)
      throws IOException, GeneralSecurityException {
    byte[] iv = new byte[16];
    SecureRandom secureRandom = new SecureRandom();
    secureRandom.nextBytes(iv);
    IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
    Cipher cipher = File.initPBECipher(password, Cipher.ENCRYPT_MODE, ivParameterSpec);

    try (FileOutputStream fos = new FileOutputStream(path.toString());
        BufferedOutputStream bfos = new BufferedOutputStream(fos);
        CipherOutputStream cos = new CipherOutputStream(fos, cipher);
        BufferedOutputStream bcos = new BufferedOutputStream(cos)) {
      File.write(bfos, iv); // Write IV first
      File.write(bcos, content); // Write encrypted content
    }
  }
}
