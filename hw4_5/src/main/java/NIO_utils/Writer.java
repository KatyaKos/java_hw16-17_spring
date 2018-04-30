package NIO_utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

/**
 * Writes to channel.
 */
public class Writer {
    private ByteBuffer buffer = null;
    private ByteChannel channel;

    public Writer(@Nullable byte[] data, @NotNull ByteChannel channel) {
        if (data != null) {
            this.buffer = ByteBuffer.wrap(data);
            this.channel = channel;
        }
    }

    /**
     * Writes from buffer to channel.
     * @return number of bytes written or -1
     * @throws IOException if IO went wrong
     */
    public int write() throws IOException {
        if (buffer == null || !buffer.hasRemaining()) {
            return -1;
        }
        return channel.write(buffer);
    }
}
