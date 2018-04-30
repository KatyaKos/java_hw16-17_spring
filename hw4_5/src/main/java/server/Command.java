package server;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

/**
 * Interface that represents Command.
 */
interface Command {
    /**
     * Executes the command that works with directories and files.
     * @param path to directory or file
     * @return data in byte array
     */
    public @Nullable byte[] execute(@NotNull Path path);

    /**
     * Codes: GET = '2', LIST = '1'.
     * @return code of the command
     */
    public int getCode();
}
