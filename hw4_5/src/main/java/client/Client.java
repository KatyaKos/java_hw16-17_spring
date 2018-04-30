package client;

import NIO_utils.Reader;
import org.jetbrains.annotations.NotNull;
import server.CommandGet;
import server.CommandList;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class represents a client who can make "list" and "get" requests.
 */
public class Client {
    private final int port;
    private int listCode = (new CommandList()).getCode();
    private int getCode = (new CommandGet()).getCode();

    public Client(int port) {
        this.port = port;
    }

    /**
     * Sends "list" request and receives list of files in a given directory.
     * @param path directory
     * @return list of files
     */
    public List<String> executeList(@NotNull String path) {
        byte[] content = interactWithServer(listCode, path);
        List<String> newContent = new ArrayList<>();
        if (content == null) {
            newContent.add(Integer.toString(0));
            return newContent;
        }
        try (ByteArrayInputStream byteStream = new ByteArrayInputStream(content);
             DataInputStream inputStream = new DataInputStream(byteStream)) {
            int size = inputStream.readInt();
            while (size > 0) {
                size--;
                newContent.add(inputStream.readUTF());
            }
            return newContent;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sends "get" request and receives a content of the given file.
     * @param path file
     * @param pathToWrite where to write
     */
    public void executeGet(@NotNull String path, @NotNull String pathToWrite) {
        byte[] content = interactWithServer(getCode, path);
        try {
            if (content != null && !Arrays.equals(content, new byte[0])) {
                Files.write(Paths.get(pathToWrite), Long.toString(content.length).getBytes());
                Files.write(Paths.get(pathToWrite), "\n".getBytes());
                Files.write(Paths.get(pathToWrite), content);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] interactWithServer(int code, @NotNull String data) {
        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream(); DataOutputStream outputStream = new DataOutputStream(byteStream)) {
            SocketChannel channel = SocketChannel.open();
            channel.connect(new InetSocketAddress(port));
            channel.configureBlocking(false);
            outputStream.writeInt(code);
            outputStream.writeUTF(data);
            outputStream.flush();
            byte[] bytes = byteStream.toByteArray();
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            while (buffer.hasRemaining()) {
                channel.write(buffer);
            }
            channel.shutdownOutput();
            Reader reader = new Reader(channel);
            while (reader.read() != -1) {
            }
            channel.close();
            return reader.getData();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
