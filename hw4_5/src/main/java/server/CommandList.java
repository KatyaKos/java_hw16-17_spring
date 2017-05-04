package server;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class represents command "list".
 */
public class CommandList implements Command{
    /**
     * Executes the command.
     * @param path to directory
     * @return list of files
     */
    @Override
    public byte[] execute(@NotNull Path path) {
        if (!Files.exists(path) || !Files.isDirectory(path)) {
            return null;
        }
        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream(); DataOutputStream outputStream = new DataOutputStream(byteStream)) {
            List<Path> paths = Files.list(path).collect(Collectors.toList());
            outputStream.writeInt(paths.size());
            for (Path p : paths) {
                outputStream.writeUTF(p.getFileName().toString());
            }
            outputStream.flush();
            return byteStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Converts bytes to list of filenames.
     * @param content bytes to convert
     * @return list of filenames
     * @throws IOException if IO went wrong
     */
    @NotNull
    public static List<String> fromBytes(@NotNull byte[] content) throws IOException {
        try (ByteArrayInputStream byteStream = new ByteArrayInputStream(content); DataInputStream inputStream = new DataInputStream(byteStream)) {
            int size = inputStream.readInt();
            List<String> names = new LinkedList<>();
            while (size > 0) {
                size--;
                names.add(inputStream.readUTF());
            }
            return names;
        }
    }

    @Override
    public int getCode() {
        return 1;
    }
}
