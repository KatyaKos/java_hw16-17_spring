package client;

import org.jetbrains.annotations.NotNull;
import server.NonBlockingServer;

import java.util.Iterator;
import java.util.List;

/**
 * This class parses arguments from client command line.
 */
public class ClientParser {
    static void parse(@NotNull Iterator<String> iterator) {
        NonBlockingServer server = new NonBlockingServer();
        Client client = new Client(server.getPort());
        while (true) {
            String query = iterator.next();
            if (query.equals("list")) {
                String path = iterator.next();
                List<String> answer = client.executeList(path);
                for (String file : answer) {
                    System.out.println(file);
                }
                continue;
            }
            if (query.equals("get")) {
                String path = iterator.next();
                String to = iterator.next();
                client.executeGet(path, to);
                continue;
            }
            if (query.equals("exit")) {
                break;
            }
            System.out.println("unknown command");
        }
    }
}
