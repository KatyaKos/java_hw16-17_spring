package NIO_utils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.util.Arrays;

/**
 * Created by KatyaKos on 27.04.2017.
 */
public class Reader {
    private ByteBuffer buffer;
    private ByteChannel channel;
    private final static int SIZE = 1024;
    private byte[] data;
    private int position;

    public Reader(@NotNull ByteChannel channel) {
        buffer = ByteBuffer.allocate(SIZE);
        data = null;
        position = 0;
        this.channel = channel;
    }

    /**
     * Reads data.
     * @return number of read bytes or -1
     * @throws IOException if IO went wrong
     */
    public int read() throws IOException {
        int bytes = channel.read(buffer);
        if (bytes == -1)
            return -1;
        if (data == null) {
            data = new byte[SIZE];
        }
        while (data.length < position + bytes) {
            byte[] newData = new byte[2 * data.length];
            System.arraycopy(data, 0, newData, 0, position);
            data = newData;
        }
        buffer.flip();
        buffer.get(data, position, bytes);
        position += bytes;
        buffer.clear();
        return bytes;
    }

    /**
     *Gets data we read.
     * @return data in byte array
     */
    public byte[] getData() {
        if (data == null)
            return null;
        return Arrays.copyOf(data, position);
    }
}
