package NIO_utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

/**
 * Created by KatyaKos on 27.04.2017.
 */
public class Writer {
    private ByteBuffer buffer;
    private ByteChannel channel;

    public Writer(@Nullable byte[] data, @NotNull ByteChannel channel) {
        if (data == null) {
            buffer = ByteBuffer.wrap(new byte[0]);
        }
        buffer = ByteBuffer.wrap(data);
        this.channel = channel;
    }

    /**
     * Writes from buffer to channel.
     * @return number of bytes written or -1
     * @throws IOException if IO went wrong
     */
    public int write() throws IOException {
        if (!buffer.hasRemaining()) {
            return -1;
        } else {
            return channel.write(buffer);
        }
    }
}
