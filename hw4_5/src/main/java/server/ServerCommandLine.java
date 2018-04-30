package server;

import org.jetbrains.annotations.NotNull;

import java.util.Scanner;

/**
 * This class interacts with user and sends requests to server.
 */
public class ServerCommandLine {

    public static void main(@NotNull String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            ServerParser.parse(scanner);
        }
    }
}
