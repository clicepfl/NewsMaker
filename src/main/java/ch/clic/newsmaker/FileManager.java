package ch.clic.newsmaker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

public class FileManager {

    private FileManager() {}

    static public InputStream openResource(String path) {
        return FileManager.class.getResourceAsStream(path);
    }

    static public File openFile(String path) throws FileNotFoundException, URISyntaxException {
        URL fileURL = FileManager.class.getResource(path);

        if (fileURL == null) throw new FileNotFoundException();

        return new File(fileURL.toURI());
    }

    /**
     * Save string content in file on disk
     *
     * @param content the content to write
     * @param file the file where to write
     */
    public static void saveInFile(String content, File file) {
        if (Objects.isNull(file)) return;
        try (Writer wr = new FileWriter(file)) {
            wr.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Open the file from <code>path</code> read the content and return it as a <code>String</code>
     *
     * @param path the path of the file
     * @return the content of the file as a <code>String</code>
     * @throws IOException return <code>IOException</code> in case of an input-output exception (the file doesn't exist)
     */
    static public String readContentOfFile(String path) throws IOException {
        return readContentOfFile(openResource(path));
    }

    static public String readContentOfFile(File file) throws IOException {

        String content;

        try (InputStream inputStream = new FileInputStream(file)) {
            content = readContentOfFile(inputStream);
        }
        return content;
    }

    /**
     * Open the file from <code>inputStream</code> read the content and return it as a <code>String</code>
     *
     * @param inputStream the stream of the file
     * @return the content of the file as a <code>String</code>
     * @throws IOException throws <code>IOException</code> in case of an input-output exception (the file doesn't exist)
     */
    static public String readContentOfFile(InputStream inputStream) throws IOException {
        final ByteArrayOutputStream result = new ByteArrayOutputStream();
        final byte[] buffer = new byte[1024];
        int length;

        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }

        return result.toString();
    }

}
