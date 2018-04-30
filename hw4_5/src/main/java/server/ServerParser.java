package server;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

/**
 * This class parses arguments from server command line.
 */
public class ServerParser {

    static void parse(@NotNull Iterator<String> iterator) {
        Server server = new NonBlockingServer();
        while (true) {
            String request = iterator.next();
            if (request.equals("start")) {
                server.start();
            } else if (request.equals("stop")) {
                server.stop();
                break;
            } else {
                System.out.println("Unknown command");
            }
        }
    }
}
