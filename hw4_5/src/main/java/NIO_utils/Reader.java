package NIO_utils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.util.Arrays;

/**
 * Reads from channel.
 */
public class Reader {
    private final static int SIZE = 1024;
    private int position = 0;
    private int bytesNumber = 0;
    private byte[] data = null;
    private ByteBuffer buffer;
    private ByteChannel channel;

    public Reader(@NotNull ByteChannel channel) {
        buffer = ByteBuffer.allocate(SIZE);
        this.channel = channel;
    }

    /**
     * Reads data.
     * @return number of read bytes or -1
     * @throws IOException if IO went wrong
     */
    public int read() throws IOException {
        bytesNumber = channel.read(buffer);
        if (bytesNumber == -1)
            return -1;
        if (data == null) {
            data = new byte[SIZE];
        }
        while (data.length < position + bytesNumber) {
            byte[] newData = new byte[2 * data.length];
            System.arraycopy(data, 0, newData, 0, position);
            data = newData;
        }
        buffer.flip();
        buffer.get(data, position, bytesNumber);
        position += bytesNumber;
        buffer.clear();
        return bytesNumber;
    }

    /**
     * Gives data we read.
     * @return data in byte array
     */
    public byte[] getData() {
        if (data == null) {
            return null;
        }
        return Arrays.copyOf(data, position);
    }
}
