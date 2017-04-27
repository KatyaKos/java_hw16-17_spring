package server;

/**
 * Created by KatyaKos on 27.04.2017.
 */

import NIO_utils.Reader;
import NIO_utils.Writer;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class NonBlockingServer implements Server {
    private int port;
    private SocketAddress PORT;
    private Selector selector;

    private List<Request> processNeeded = new LinkedList<>();

    private boolean isStopped = false;

    public NonBlockingServer() {
        port = 1420;
        PORT = new InetSocketAddress(1420);
    }

    public NonBlockingServer(int port) {
        PORT = new InetSocketAddress(port);
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    @Override
    public void start() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open(); Selector sel = Selector.open()) {
            selector = sel;
            serverSocketChannel.bind(PORT);
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            while (!isStopped) {
                int ready = selector.select();
                if (ready == 0) continue;

                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();

                    if (selectionKey.isAcceptable()) {
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        if (socketChannel != null) {
                            socketChannel.configureBlocking(false);
                            socketChannel.register(selector, SelectionKey.OP_WRITE);
                        }
                    } else if (selectionKey.isWritable()) {
                        Writer writer = (Writer) selectionKey.attachment();
                        if (writer.write() == -1) {
                            selectionKey.channel().close();
                        }
                    } else if (selectionKey.isReadable()) {
                        Reader reader = (Reader) selectionKey.attachment();
                        if (reader.read() == -1) {
                            byte[] data = reader.getData();
                            selectionKey.interestOps(0);
                            processNeeded.add(new Request(selectionKey, data));
                        }
                    }

                    iterator.remove();
                }
            }

            processRequests();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void processRequests() {
        for (Request request : processNeeded) {
            byte[] response;
            try (ByteArrayInputStream byteStream = new ByteArrayInputStream(request.getData()); DataInputStream inputStream = new DataInputStream(byteStream)) {
                int code = inputStream.readInt();
                CommandGet get = new CommandGet();
                CommandList list = new CommandList();
                if (code == list.getCode()) {
                    final String path = inputStream.readUTF();
                    response = list.execute(Paths.get(path));
                } else if (code == get.getCode()){
                    final String path = inputStream.readUTF();
                    response = get.execute(Paths.get(path));
                } else {
                    throw new RuntimeException();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                SelectableChannel channel = request.getKey().channel();
                channel.register(selector, SelectionKey.OP_WRITE, new Writer(response, (ByteChannel) request.getKey().channel()));
            } catch (ClosedChannelException e) {
                throw new RuntimeException(e);
            }
        }
        processNeeded = new ArrayList<>();
    }

    @Override
    public void stop() {
        isStopped = true;
    }
}
