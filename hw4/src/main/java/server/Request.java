package server;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.channels.SelectionKey;

/**
 * This class represents client's request.
 */
public class Request {
    private SelectionKey key;
    private byte[] data;

    public Request(@NotNull SelectionKey key, @Nullable byte[] data) {
        this.key = key;
        this.data = data;
    }

    public SelectionKey getKey() {
        return key;
    }

    public byte[] getData() {
        return data;
    }
}
