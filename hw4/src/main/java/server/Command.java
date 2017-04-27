package server;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * Interface that represents Command.
 */
public interface Command {
    /**
     * Executes the command that works with directories and files.
     * @param path to directory or file
     * @return data in byte array
     */
    public byte[] execute(@NotNull Path path);

    public int getCode();
}
