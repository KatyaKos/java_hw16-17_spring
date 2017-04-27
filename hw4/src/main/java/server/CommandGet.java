package server;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * This class represents command "get".
 */
public class CommandGet implements Command{
    /**
     * Executes the command.
     * @param path to file
     * @return file content
     */
    @Override
    public byte[] execute(@NotNull Path path) {
        if (Files.isDirectory(path) || Files.notExists(path)) {
            return null;
        }
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getCode() {
        return 2;
    }
}
