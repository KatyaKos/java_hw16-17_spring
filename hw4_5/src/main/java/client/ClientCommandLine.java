package client;

import org.jetbrains.annotations.NotNull;

import java.util.Scanner;

/**
 * This class interacts with client and makes request to the server.
 */
public class ClientCommandLine {
    /** Interaction.
     * @param args arguments from command line.
     */
    public static void main(@NotNull String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            ClientParser.parse(scanner);
        }
    }
}
