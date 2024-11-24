package ch.heigvd.dai;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class File {
    public static String read(BufferedInputStream bis) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int amountRead;

        while ((amountRead = bis.read(buffer)) != -1) {
            baos.write(buffer, 0, amountRead);
        }

        return baos.toString(StandardCharsets.UTF_8);
    }

    public static String read(Path path) throws IOException {
        String content;

        try (FileInputStream fis = new FileInputStream(path.toString());
             BufferedInputStream bis = new BufferedInputStream(fis)) {
            content = read(bis);
        }

        return content;
    }

    public static void write(BufferedOutputStream bos, byte[] content) throws IOException {
        bos.write(content);
        bos.flush();
    }

    public static void write(BufferedOutputStream bos, String content) throws IOException {
        write(bos, content.getBytes(StandardCharsets.UTF_8));
    }

    public static void write(Path path, String content) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(path.toString());
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {
            write(bos, content);
        }
    }
}
