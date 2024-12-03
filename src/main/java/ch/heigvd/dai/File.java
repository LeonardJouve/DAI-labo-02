/**
 * The {@code File} class provides utility methods for reading and writing files in the pass-secure
 * system. It supports reading and writing both binary and text data.
 */
package ch.heigvd.dai;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class File {

  /**
   * Reads the content of a file from a {@link BufferedInputStream}.
   *
   * @param bis The {@link BufferedInputStream} to read from.
   * @return The content of the file as a {@link String}.
   * @throws IOException If an I/O error occurs during reading.
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
   * Reads the content of a file from the specified {@link Path}.
   *
   * @param path The {@link Path} of the file to read.
   * @return The content of the file as a {@link String}.
   * @throws IOException If an I/O error occurs during reading.
   */
  public static String read(Path path) throws IOException {
    String content;

    try (FileInputStream fis = new FileInputStream(path.toString());
        BufferedInputStream bis = new BufferedInputStream(fis)) {
      content = read(bis);
    }

    return content;
  }

  /**
   * Writes binary content to a {@link BufferedOutputStream}.
   *
   * @param bos The {@link BufferedOutputStream} to write to.
   * @param content The binary content to write as a byte array.
   * @throws IOException If an I/O error occurs during writing.
   */
  public static void write(BufferedOutputStream bos, byte[] content) throws IOException {
    bos.write(content);
    bos.flush();
  }

  /**
   * Writes text content to a {@link BufferedOutputStream}.
   *
   * @param bos The {@link BufferedOutputStream} to write to.
   * @param content The text content to write as a {@link String}.
   * @throws IOException If an I/O error occurs during writing.
   */
  public static void write(BufferedOutputStream bos, String content) throws IOException {
    write(bos, content.getBytes(StandardCharsets.UTF_8));
  }

  /**
   * Writes text content to a file at the specified {@link Path}.
   *
   * @param path The {@link Path} of the file to write to.
   * @param content The text content to write as a {@link String}.
   * @throws IOException If an I/O error occurs during writing.
   */
  public static void write(Path path, String content) throws IOException {
    try (FileOutputStream fos = new FileOutputStream(path.toString());
        BufferedOutputStream bos = new BufferedOutputStream(fos)) {
      write(bos, content);
    }
  }
}
